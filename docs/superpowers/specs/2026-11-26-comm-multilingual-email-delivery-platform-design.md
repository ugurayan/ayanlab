# Technical Specification: AyanLabs Comm (Multilingual Email & Delivery Platform)

**Date:** 2026-11-26  
**Status:** Approved (Direct Instruction)  
**Target App:** `ayanlab-comm` (Spring Boot + React UI + Flutter Mobile Template)

---

## 1. Executive Summary & Vision

**AyanLabs Comm** is a best-in-class, developer-friendly, and institution-optimized communications platform inspired by the core delivery strengths of **Resend** and marketing capabilities of **Klaviyo**, supercharged with a unique **Multilingual Translation & Approval Engine**. 

Designed specifically for educational institutions and multi-national organizations, it solves a fundamental communication barrier: **delivering personal, official messages to a diverse group of recipients in their preferred native languages with high-fidelity inbox delivery, custom domain branding, and rigorous tracking.**

### Core Product Capabilities:
1. **DKIM/SPF Custom Domains (Resend Model):** Complete brand isolation allowing organizations to send emails from their own domains (e.g., `bulletin.school.edu`) with programmatic DNS checks.
2. **Multilingual Smart Campaigns (AyanLabs Special):** Compose once in your native language, automatically translate into 11 target languages (DeepL/LLM), review/edit translations, and execute smart delivery.
3. **Advanced Delivery Options:**
   - *Recipient Native:* Each recipient receives a dedicated copy in their preferred language.
   - *Dual Stack (Bilingual):* Recipient receives a unified mail containing the Default Language + Recipient's Native Language.
   - *All Selected Stack:* All configured languages stacked beautifully in a single mail.
4. **Resilient Outbox & Kafka Queueing:** Transactional Outbox pattern backed by Kafka for high-throughput, guaranteed, non-blocking delivery.
5. **Sender Reputation & Suppression Lists:** Auto-management of hard bounces, complaints, and spam protection to guarantee high inbox placement.
6. **Real-time Analytics:** Interactive dashboards for Open Rates, Click-Through Rates, Bounces, and Delivery logs.

---

## 2. Architecture & Data Flow

```
   [ React Web / Flutter Mobile UI ]
                  |  (REST APIs with Correlation-ID)
                  v
       [ ayanlab-comm-backend ]
         |              |
         | (Save Jobs)  +------> [ DeepL / LLM Translation API ] (Translates drafts)
         v
    [ Postgres ] <=======> [ Kafka Topic: comm-email-dispatch ]
  (Outbox Pattern)                  |
                                    | (Consumes & Dispatches)
                                    v
                        [ SMTP / AWS SES Dispatcher ]
                                    |
                                    v
                           [ Target Recipient ]
                                    |  (Opens / Clicks / Bounces)
                                    v
                         [ SES / Postmark Webhook ]
                                    |
                                    v
                     [ Webhook Controller (comm-be) ] ---> Updates Analytics
```

---

## 3. Database Schema (`comm` schema)

To maintain clean module boundaries and high performance, we define the following tables in the `comm` schema (Postgres). Since we use Spring Data JPA with Lombok and native types, all IDs will be standard `UUID`s generated using our core `IdGenerator`.

```sql
CREATE SCHEMA IF NOT EXISTS comm;
```

### 3.1. `comm.organization_domains` (Resend Model)
Saves verified sending domains per tenant organization.
```sql
CREATE TABLE comm.organization_domains (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    app_id UUID NOT NULL,
    domain_name VARCHAR(255) NOT NULL UNIQUE,
    verification_token VARCHAR(255) NOT NULL,
    dkim_selector VARCHAR(50) DEFAULT 'ayan',
    dkim_public_key TEXT,
    dkim_private_key TEXT,
    spf_verified BOOLEAN DEFAULT FALSE,
    dkim_verified BOOLEAN DEFAULT FALSE,
    dmarc_verified BOOLEAN DEFAULT FALSE,
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, VERIFIED, FAILED
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
```

### 3.2. `comm.contacts` (List & Profile Management)
Contains individual recipient preferences and linguistic settings.
```sql
CREATE TABLE comm.contacts (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    app_id UUID NOT NULL,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    preferred_language VARCHAR(10) DEFAULT 'en', -- tr, en, es, ar, etc.
    custom_attributes JSONB, -- Custom variables (e.g., student_name, grade)
    status VARCHAR(50) DEFAULT 'SUBSCRIBED', -- SUBSCRIBED, UNSUBSCRIBED, BOUNCED
    external_id VARCHAR(255), -- Back-link to HRM or LMS
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (organization_id, email)
);
```

### 3.3. `comm.contact_lists` & `comm.contact_list_members`
Allows groupings of recipients (e.g., "9th Grade Parents").
```sql
CREATE TABLE comm.contact_lists (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    app_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE comm.contact_list_members (
    list_id UUID REFERENCES comm.contact_lists(id) ON DELETE CASCADE,
    contact_id UUID REFERENCES comm.contacts(id) ON DELETE CASCADE,
    PRIMARY KEY (list_id, contact_id)
);
```

### 3.4. `comm.email_templates` & `comm.email_template_translations`
Stores base templates and their translated outputs.
```sql
CREATE TABLE comm.email_templates (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    app_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    default_language VARCHAR(10) DEFAULT 'en',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE comm.email_template_translations (
    id UUID PRIMARY KEY,
    template_id UUID REFERENCES comm.email_templates(id) ON DELETE CASCADE,
    language VARCHAR(10) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body_html TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'DRAFT', -- DRAFT, AUTO_TRANSLATED, APPROVED
    approved_by VARCHAR(100),
    approved_at TIMESTAMP WITH TIME ZONE,
    UNIQUE (template_id, language)
);
```

### 3.5. `comm.campaigns` & `comm.campaign_translations`
A broadcast job targeting list(s) with custom parameters.
```sql
CREATE TABLE comm.campaigns (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    app_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    template_id UUID REFERENCES comm.email_templates(id),
    target_list_id UUID REFERENCES comm.contact_lists(id),
    delivery_mode VARCHAR(50) DEFAULT 'RECIPIENT_PREFERRED', -- RECIPIENT_PREFERRED, BILINGUAL_STACK, ALL_STACK
    status VARCHAR(50) DEFAULT 'DRAFT', -- DRAFT, TRANSLATING, READY, SENDING, COMPLETED, FAILED
    scheduled_at TIMESTAMP WITH TIME ZONE,
    sent_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE comm.campaign_translations (
    id UUID PRIMARY KEY,
    campaign_id UUID REFERENCES comm.campaigns(id) ON DELETE CASCADE,
    language VARCHAR(10) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body_html TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'DRAFT', -- DRAFT, APPROVED
    UNIQUE (campaign_id, language)
);
```

### 3.6. `comm.email_outbox` & `comm.delivery_logs` (Outbox & Delivery Tracking)
Resilient outbox and real-time tracking logs.
```sql
CREATE TABLE comm.email_outbox (
    id UUID PRIMARY KEY,
    campaign_id UUID,
    recipient_email VARCHAR(255) NOT NULL,
    recipient_contact_id UUID,
    domain_id UUID,
    subject VARCHAR(255) NOT NULL,
    body_html TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, PROCESSING, SENT, FAILED
    retry_count INT DEFAULT 0,
    next_attempt_at TIMESTAMP WITH TIME ZONE,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE comm.delivery_logs (
    id UUID PRIMARY KEY,
    outbox_id UUID,
    campaign_id UUID,
    recipient_email VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL, -- DELIVERED, BOUNCED, COMPLAINT, OPENED, CLICKED, FAILED
    provider_message_id VARCHAR(255),
    error_details TEXT,
    user_agent TEXT,
    ip_address VARCHAR(45),
    logged_at TIMESTAMP WITH TIME ZONE NOT NULL
);
```

### 3.7. `comm.suppression_lists` (Reputation Protector)
Global suppressed emails to completely block bounces/complaints.
```sql
CREATE TABLE comm.suppression_lists (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    email VARCHAR(255) NOT NULL,
    reason VARCHAR(50) NOT NULL, -- HARD_BOUNCE, COMPLAINT, MANUAL
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (organization_id, email)
);
```

---

## 4. Key Business Logic & Engineering Details

### 4.1. The Outbox & Kafka Pipeline
1. **Atomic Campaign Launch:** When a user clicks "Send Campaign", the campaign status changes to `SENDING`. The backend immediately queries all contacts in the target list, filters them against the `suppression_lists`, renders their custom language template version, and inserts records into `comm.email_outbox` in a single transaction.
2. **Event Dispatch:** A Kafka event `CommEmailDispatchEvent` containing the `outboxId` is published.
3. **Kafka Consumer:** The consumer reads events, grabs the outbox record, checks if `status` is still `PENDING`, updates it to `PROCESSING`, and dispatches it via SMTP/SES.
4. **Status Update:** Upon successful provider dispatch, the outbox record transitions to `SENT` and triggers a `DELIVERED` status locally (until updated by a webhook).

### 4.2. Multilingual Delivery Rendering
When generating the outbox entry for a recipient:
* **RECIPIENT_PREFERRED Mode:**
  1. Read recipient's `preferred_language` (e.g., `es`).
  2. Is there an `APPROVED` campaign translation for `es`? If yes, use it.
  3. If no, fall back to the organization's default language (e.g., `tr` or `en`).
* **BILINGUAL_STACK Mode:**
  1. Fetch campaign default language translation + recipient's preferred language translation.
  2. Join them using a clean, visually separated divider containing beautiful bilingual headers.
* **ALL_STACK Mode:**
  1. Concatenate all approved campaign translations with clear language labels.

### 4.3. Custom Domains & DNS Checkers
Every domain needs CNAME, SPF, and DMARC verification:
* **CNAME (DKIM):** Add 3 records mapping to AWS SES DKIM hosts.
* **SPF:** A `TXT` record pointing to `v=spf1 include:amazonses.com ~all` (or equivalent).
* **DMARC:** A `TXT` record with `v=DMARC1; p=none; rua=mailto:dmarc-reports@ayanlab.com`.
* **Verification Runner:** A Spring `@Scheduled` worker wakes up every 15 minutes, performs an actual DNS query (using `dnsjava` or `javax.naming.directory`), and promotes domain status to `VERIFIED` once all records resolve.

### 4.4. Bounce & Complaint Tracking (Spam Defense)
We expose a public webhook endpoint: `/api/public/comm/webhooks/delivery-events`.
* AWS SES sends HTTP POST JSON requests when delivery bounces, causes a spam complaint, or delivers.
* **Hard Bounce / Complaint:** The webhook automatically records a `suppression_lists` entry for that email address. Any future emails targeting this address are skipped at outbox generation time.
* **Click/Open Tracking:**
  * **Opens:** HTML emails contain an `<img>` referencing `/api/public/comm/tracking/open/{outboxId}.png`. Reading this increments opens.
  * **Clicks:** Links in HTML are rewritten to `/api/public/comm/tracking/click/{outboxId}?url={encodedUrl}`. We record the click event and redirect the browser.

---

## 5. Implementation Roadmap & Steps

We will build the system in five robust, chronological, and testable steps:

### **Step 1: DB Infrastructure & Entities**
* Create Flyway/DDL update-friendly Entity models in `com.ayanlab.comm.backend.entity`.
* Bind domain, contacts, templates, campaigns, outbox, tracking logs, and suppression lists.
* Establish repositories for each model.

### **Step 2: Core Platform Services & Core APIs**
* Domain verification service using DNS resolution.
* Contact import engine supporting multi-lingual profile structures.
* Translation Engine integrating auto-translate features.

### **Step 3: Kampanya ve Çok Dilli Teslimat Servisleri**
* Kampanya oluşturma, dile göre varyasyon türetme ve onay mekanizması.
* Outbox rendering servisleri (Preferred, Bilingual, Multilingual).
* Kafka entegrasyonu ve kuyruk tüketim servisleri.

### **Step 4: Teslimat, Tracking Webhook & Suppression Engine**
* Entegre SMTP/AWS SES gönderim servisi.
* Açılma (open pixel) ve tıklama (link tracking) controller'ları.
* SES Webhook dinleyici uç noktası ve Suppression otomasyonu.

### **Step 5: React Web UI & Mobil Entegrasyon**
* Şablon editörü, dile göre çeviri onay kartları.
* DNS ayarları & Domain doğrulama ekranı.
* Gönderim durumları, Bounce / Complaint ve Açılma grafiklerini içeren zengin Dashboard.

---

With this specifications finalized, we will proceed immediately to Step 1 and write the codebase step by step. No placeholder codes, production-grade logic only.
