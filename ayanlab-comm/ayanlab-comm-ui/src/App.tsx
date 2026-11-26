import React, { useState, useEffect } from 'react';

// API Configuration
const API_BASE = 'http://localhost:8080/api';

// Interface Types
interface Domain {
  id: string;
  domainName: string;
  verificationToken: string;
  dkimSelector: string;
  spfVerified: boolean;
  dkimVerified: boolean;
  dmarcVerified: boolean;
  status: 'PENDING' | 'VERIFIED' | 'FAILED';
}

interface Contact {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  preferredLanguage: string;
  status: 'SUBSCRIBED' | 'UNSUBSCRIBED' | 'BOUNCED';
}

interface ContactList {
  id: string;
  name: string;
  description: string;
  contactsCount: number;
}

interface Campaign {
  id: string;
  name: string;
  deliveryMode: 'RECIPIENT_PREFERRED' | 'BILINGUAL_STACK' | 'ALL_STACK';
  status: 'DRAFT' | 'TRANSLATING' | 'READY' | 'SENDING' | 'COMPLETED' | 'FAILED';
  sentAt?: string;
  targetListName: string;
}

interface Translation {
  language: string;
  subject: string;
  bodyHtml: string;
  status: 'DRAFT' | 'APPROVED';
}

export default function App() {
  const [activeTab, setActiveTab] = useState<'dashboard' | 'domains' | 'contacts' | 'templates' | 'campaigns'>('dashboard');
  const [loading, setLoading] = useState(false);

  // Mock State Fallbacks for Local Demo Simulation
  const [domains, setDomains] = useState<Domain[]>([
    {
      id: 'd1',
      domainName: 'bulletin.ayanlab-academy.org',
      verificationToken: 'ayan-verification=verify_9a8b7c6d5e4f',
      dkimSelector: 'ayan',
      spfVerified: true,
      dkimVerified: true,
      dmarcVerified: true,
      status: 'VERIFIED'
    },
    {
      id: 'd2',
      domainName: 'communication.school-district.edu',
      verificationToken: 'ayan-verification=verify_1b2c3d4e5f6g',
      dkimSelector: 'ayan',
      spfVerified: false,
      dkimVerified: false,
      dmarcVerified: false,
      status: 'PENDING'
    }
  ]);

  const [contacts, setContacts] = useState<Contact[]>([
    { id: 'c1', email: 'vildan.kaya@gmail.com', firstName: 'Vildan', lastName: 'Kaya', preferredLanguage: 'tr', status: 'SUBSCRIBED' },
    { id: 'c2', email: 'john.smith@yahoo.com', firstName: 'John', lastName: 'Smith', preferredLanguage: 'en', status: 'SUBSCRIBED' },
    { id: 'c3', email: 'carlos.mendez@hotmail.com', firstName: 'Carlos', lastName: 'Mendez', preferredLanguage: 'es', status: 'SUBSCRIBED' },
    { id: 'c4', email: 'fatima.almansoor@outlook.com', firstName: 'Fatima', lastName: 'Al-Mansoor', preferredLanguage: 'ar', status: 'SUBSCRIBED' },
    { id: 'c5', email: 'mei.ling@qq.com', firstName: 'Mei', lastName: 'Ling', preferredLanguage: 'zh', status: 'SUBSCRIBED' },
    { id: 'c6', email: 'bounce-test@ayanlab.com', firstName: 'Bad', lastName: 'Email', preferredLanguage: 'en', status: 'BOUNCED' }
  ]);

  const [contactLists, setContactLists] = useState<ContactList[]>([
    { id: 'l1', name: '9th Grade Parents Group', description: 'Parents of 9th grade student body', contactsCount: 5 },
    { id: 'l2', name: 'Faculty & Administrative Staff', description: 'All high-school teachers', contactsCount: 12 }
  ]);

  const [campaigns, setCampaigns] = useState<Campaign[]>([
    { id: 'cp1', name: 'Autumn Midterm Progress Reports', deliveryMode: 'RECIPIENT_PREFERRED', status: 'COMPLETED', sentAt: '2026-11-25 14:32', targetListName: '9th Grade Parents Group' },
    { id: 'cp2', name: 'Emergency Inclement Weather Closure Announcement', deliveryMode: 'BILINGUAL_STACK', status: 'READY', targetListName: 'Faculty & Administrative Staff' }
  ]);

  // Translation Edit State
  const [selectedCampaignId, setSelectedCampaignId] = useState<string | null>('cp2');
  const [translations, setTranslations] = useState<Translation[]>([
    { language: 'tr', subject: 'Okul Kar Tatili Duyurusu', bodyHtml: '<p>Değerli Velimiz, olumsuz hava koşulları nedeniyle okulumuz yarın tatil edilmiştir. Eğitim online devam edecektir.</p>', status: 'APPROVED' },
    { language: 'en', subject: 'School Snow Day Announcement', bodyHtml: '<p>Dear Parent, our school will be closed tomorrow due to heavy snowfall. Education will continue online.</p>', status: 'APPROVED' },
    { language: 'es', subject: 'Anuncio de día de nieve escolar', bodyHtml: '<p>Estimado padre, nuestra escuela estará cerrada mañana debido a las fuertes nevadas. La educación continuará en línea.</p>', status: 'DRAFT' },
    { language: 'ar', subject: 'إعلان يوم الثلج المدرسي', bodyHtml: '<p>عزيزي ولي الأمر، سيتم إغلاق مدرستنا غدًا بسبب تساقط الثلوج الكثيفة. سيستمر التعليم عبر الإنترنت.</p>', status: 'DRAFT' },
    { language: 'zh', subject: '学校雪天放假通知', bodyHtml: '<p>尊敬的家长：由于降雪量大，我校明天放假。教学将在线上继续进行。</p>', status: 'DRAFT' }
  ]);

  // Form Inputs
  const [newDomain, setNewDomain] = useState('');
  const [newContactEmail, setNewContactEmail] = useState('');
  const [newContactFirstName, setNewContactFirstName] = useState('');
  const [newContactLastName, setNewContactLastName] = useState('');
  const [newContactLang, setNewContactLang] = useState('en');
  const [csvInput, setCsvInput] = useState('');

  // Auto Translate Sim
  const [newCampaignName, setNewCampaignName] = useState('');
  const [newCampaignTemplate, setNewCampaignTemplate] = useState('School Announcement Base');
  const [newCampaignMode, setNewCampaignMode] = useState<'RECIPIENT_PREFERRED' | 'BILINGUAL_STACK' | 'ALL_STACK'>('RECIPIENT_PREFERRED');

  const addDomain = () => {
    if (!newDomain) return;
    const dom: Domain = {
      id: 'd' + (domains.length + 1),
      domainName: newDomain,
      verificationToken: 'ayan-verification=verify_' + Math.random().toString(36).substring(7),
      dkimSelector: 'ayan',
      spfVerified: false,
      dkimVerified: false,
      dmarcVerified: false,
      status: 'PENDING'
    };
    setDomains([...domains, dom]);
    setNewDomain('');
  };

  const verifyDomain = (id: string) => {
    setDomains(domains.map(d => {
      if (d.id === id) {
        return {
          ...d,
          spfVerified: true,
          dkimVerified: true,
          dmarcVerified: true,
          status: 'VERIFIED'
        };
      }
      return d;
    }));
  };

  const addContact = () => {
    if (!newContactEmail) return;
    const con: Contact = {
      id: 'c' + (contacts.length + 1),
      email: newContactEmail,
      firstName: newContactFirstName,
      lastName: newContactLastName,
      preferredLanguage: newContactLang,
      status: 'SUBSCRIBED'
    };
    setContacts([con, ...contacts]);
    setNewContactEmail('');
    setNewContactFirstName('');
    setNewContactLastName('');
  };

  const handleCsvImport = () => {
    if (!csvInput) return;
    const lines = csvInput.split('\n');
    const imported: Contact[] = [];
    lines.forEach((line, idx) => {
      if (idx === 0 || !line.trim()) return; // Skip headers
      const tokens = line.split(',');
      if (tokens.length > 0) {
        imported.push({
          id: 'csv_' + idx + '_' + Date.now(),
          email: tokens[0].trim(),
          firstName: tokens[1] ? tokens[1].trim() : '',
          lastName: tokens[2] ? tokens[2].trim() : '',
          preferredLanguage: tokens[3] ? tokens[3].trim() : 'en',
          status: 'SUBSCRIBED'
        });
      }
    });
    setContacts([...imported, ...contacts]);
    setCsvInput('');
    alert(`Successfully imported ${imported.length} contacts from CSV!`);
  };

  const handleApproveTranslation = (lang: string) => {
    setTranslations(translations.map(t => {
      if (t.language === lang) {
        return { ...t, status: 'APPROVED' };
      }
      return t;
    }));
  };

  const handleManualTranslationEdit = (lang: string, field: 'subject' | 'bodyHtml', val: string) => {
    setTranslations(translations.map(t => {
      if (t.language === lang) {
        return { ...t, [field]: val, status: 'APPROVED' };
      }
      return t;
    }));
  };

  const sendCampaign = (id: string) => {
    setCampaigns(campaigns.map(c => {
      if (c.id === id) {
        return { ...c, status: 'COMPLETED', sentAt: new Date().toISOString().replace('T', ' ').substring(0, 16) };
      }
      return c;
    }));
    alert('Campaign successfully queued and dispatched through Kafka Delivery Engine!');
  };

  const createCampaign = () => {
    if (!newCampaignName) return;
    const camp: Campaign = {
      id: 'cp' + (campaigns.length + 1),
      name: newCampaignName,
      deliveryMode: newCampaignMode,
      status: 'READY',
      targetListName: '9th Grade Parents Group'
    };
    setCampaigns([camp, ...campaigns]);
    setSelectedCampaignId(camp.id);
    setNewCampaignName('');
    alert('Campaign created! Automatic Translation Engine initiated translation into 11 system languages.');
  };

  return (
    <div className="app-container">
      {/* Sidebar Navigation */}
      <aside className="sidebar">
        <div className="brand">
          <span>ayanlab</span>-comm
        </div>
        <ul className="nav-menu">
          <li className={`nav-item ${activeTab === 'dashboard' ? 'active' : ''}`} onClick={() => setActiveTab('dashboard')}>
            📊 Dashboard
          </li>
          <li className={`nav-item ${activeTab === 'campaigns' ? 'active' : ''}`} onClick={() => setActiveTab('campaigns')}>
            📧 Campaigns
          </li>
          <li className={`nav-item ${activeTab === 'contacts' ? 'active' : ''}`} onClick={() => setActiveTab('contacts')}>
            👥 Contacts
          </li>
          <li className={`nav-item ${activeTab === 'domains' ? 'active' : ''}`} onClick={() => setActiveTab('domains')}>
            🌐 Custom Domains
          </li>
        </ul>
      </aside>

      {/* Main Panel Content */}
      <main className="main-content">
        {activeTab === 'dashboard' && (
          <div>
            <div className="header">
              <div>
                <h1>Communications Engine</h1>
                <p>Track multi-lingual campaigns, giden email logs and real-time delivery metrics.</p>
              </div>
            </div>

            {/* Metrics Dashboard Row */}
            <div className="dashboard-grid">
              <div className="stat-card">
                <h3>Total Sent</h3>
                <div className="value">24,842</div>
              </div>
              <div className="stat-card">
                <h3>Average Open Rate</h3>
                <div className="value" style={{ color: '#10b981' }}>78.4%</div>
              </div>
              <div className="stat-card">
                <h3>Bounce Rate</h3>
                <div className="value" style={{ color: '#ef4444' }}>0.41%</div>
              </div>
              <div className="stat-card">
                <h3>Suppressed Emails</h3>
                <div className="value">12</div>
              </div>
            </div>

            {/* Live Campaign Status */}
            <div className="panel" style={{ marginTop: '2rem' }}>
              <h2>Recent Deliveries & Campaigns</h2>
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>Name</th>
                      <th>Target List</th>
                      <th>Delivery Mode</th>
                      <th>Status</th>
                      <th>Dispatched At</th>
                    </tr>
                  </thead>
                  <tbody>
                    {campaigns.map(c => (
                      <tr key={c.id}>
                        <td style={{ fontWeight: '600' }}>{c.name}</td>
                        <td>{c.targetListName}</td>
                        <td>
                          <span className="badge badge-primary">{c.deliveryMode}</span>
                        </td>
                        <td>
                          <span className={`badge ${c.status === 'COMPLETED' ? 'badge-success' : 'badge-warning'}`}>
                            {c.status}
                          </span>
                        </td>
                        <td>{c.sentAt || 'Pending Send'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'campaigns' && (
          <div>
            <div className="header">
              <div>
                <h1>Smart Multilingual Campaigns</h1>
                <p>Create a campaign, automatically translate into 11 target languages, review and dispatch.</p>
              </div>
            </div>

            {/* Create Campaign Widget */}
            <div className="panel" style={{ marginBottom: '2rem' }}>
              <h2>Create New Multilingual Broadcast</h2>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '1rem', marginTop: '1rem' }}>
                <div className="form-group">
                  <label>Campaign Subject (Default Language)</label>
                  <input
                    type="text"
                    placeholder="e.g. Inclement Weather Closure"
                    value={newCampaignName}
                    onChange={(e) => setNewCampaignName(e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label>Audience List</label>
                  <select>
                    {contactLists.map(l => (
                      <option key={l.id} value={l.id}>{l.name} ({l.contactsCount} parents)</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label>Multilingual Delivery Mode</label>
                  <select value={newCampaignMode} onChange={(e: any) => setNewCampaignMode(e.target.value)}>
                    <option value="RECIPIENT_PREFERRED">Recipient Native Only (Recommended)</option>
                    <option value="BILINGUAL_STACK">Bilingual Stack (Default + Recipient Native)</option>
                    <option value="ALL_STACK">All Languages Stacked</option>
                  </select>
                </div>
              </div>
              <button className="btn btn-primary" onClick={createCampaign}>🚀 Create & Translate</button>
            </div>

            {/* Auto Translate and Approval Grid */}
            {selectedCampaignId && (
              <div className="panel">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                  <div>
                    <h2>Translation Review Desk</h2>
                    <p style={{ color: '#a1a1aa', fontSize: '0.85rem' }}>
                      Translate & Review translations for: <strong>{campaigns.find(c => c.id === selectedCampaignId)?.name}</strong>
                    </p>
                  </div>
                  <button className="btn btn-success" style={{ background: '#10b981', color: '#fff' }} onClick={() => sendCampaign(selectedCampaignId)}>
                    📨 Approve & Send Broadcast
                  </button>
                </div>

                <div className="translation-grid">
                  {translations.map(t => (
                    <div className={`translation-card ${t.status === 'APPROVED' ? 'approved' : ''}`} key={t.language}>
                      <div className="lang-header">
                        <span className="lang-tag">{t.language.toUpperCase()}</span>
                        <span className={`badge ${t.status === 'APPROVED' ? 'badge-success' : 'badge-warning'}`}>
                          {t.status}
                        </span>
                      </div>
                      <div className="form-group">
                        <label>Subject</label>
                        <input
                          type="text"
                          value={t.subject}
                          onChange={(e) => handleManualTranslationEdit(t.language, 'subject', e.target.value)}
                        />
                      </div>
                      <div className="form-group">
                        <label>Email Body (HTML/Rich-text)</label>
                        <textarea
                          rows={3}
                          value={t.bodyHtml}
                          onChange={(e) => handleManualTranslationEdit(t.language, 'bodyHtml', e.target.value)}
                        />
                      </div>
                      {t.status !== 'APPROVED' && (
                        <button className="btn btn-secondary" onClick={() => handleApproveTranslation(t.language)}>
                          Check & Approve
                        </button>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {activeTab === 'contacts' && (
          <div>
            <div className="header">
              <div>
                <h1>Contacts & Lists</h1>
                <p>Manage list segmentation, metadata variables, and recipient preferred languages.</p>
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '2rem' }}>
              {/* Left Column - Add and Import */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
                <div className="panel">
                  <h2>Add Recipient</h2>
                  <div className="form-group" style={{ marginTop: '1rem' }}>
                    <label>Email</label>
                    <input type="email" placeholder="name@domain.com" value={newContactEmail} onChange={(e) => setNewContactEmail(e.target.value)} />
                  </div>
                  <div className="form-group">
                    <label>First Name</label>
                    <input type="text" placeholder="John" value={newContactFirstName} onChange={(e) => setNewContactFirstName(e.target.value)} />
                  </div>
                  <div className="form-group">
                    <label>Last Name</label>
                    <input type="text" placeholder="Smith" value={newContactLastName} onChange={(e) => setNewContactLastName(e.target.value)} />
                  </div>
                  <div className="form-group">
                    <label>Preferred Language</label>
                    <select value={newContactLang} onChange={(e) => setNewContactLang(e.target.value)}>
                      <option value="tr">Turkish (Türkçe)</option>
                      <option value="en">English</option>
                      <option value="es">Spanish (Español)</option>
                      <option value="ar">Arabic (العربية)</option>
                      <option value="zh">Chinese (中文)</option>
                    </select>
                  </div>
                  <button className="btn btn-primary" style={{ width: '100%' }} onClick={addContact}>Add Contact</button>
                </div>

                <div className="panel">
                  <h2>CSV Import</h2>
                  <p style={{ color: '#a1a1aa', fontSize: '0.8rem', margin: '0.5rem 0' }}>
                    Format: <code>email,firstName,lastName,preferredLanguage</code>
                  </p>
                  <textarea
                    rows={4}
                    placeholder="john@smith.com,John,Smith,en&#10;maria@garcia.com,Maria,Garcia,es"
                    value={csvInput}
                    onChange={(e) => setCsvInput(e.target.value)}
                  />
                  <button className="btn btn-secondary" style={{ width: '100%', marginTop: '1rem' }} onClick={handleCsvImport}>
                    📥 Parse & Import
                  </button>
                </div>
              </div>

              {/* Right Column - Contacts list */}
              <div className="panel">
                <h2>Recipient Directory</h2>
                <div className="table-container">
                  <table>
                    <thead>
                      <tr>
                        <th>Recipient</th>
                        <th>Preferred Language</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {contacts.map(c => (
                        <tr key={c.id}>
                          <td>
                            <div style={{ fontWeight: '600' }}>{c.firstName} {c.lastName}</div>
                            <div style={{ color: '#a1a1aa', fontSize: '0.8rem' }}>{c.email}</div>
                          </td>
                          <td>
                            <span className="badge badge-primary" style={{ textTransform: 'uppercase' }}>
                              {c.preferredLanguage}
                            </span>
                          </td>
                          <td>
                            <span className={`badge ${c.status === 'SUBSCRIBED' ? 'badge-success' : 'badge-danger'}`}>
                              {c.status}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'domains' && (
          <div>
            <div className="header">
              <div>
                <h1>Sender Domain Verification</h1>
                <p>Verify DKIM, SPF, and DMARC properties to dispatch emails directly from your own domain.</p>
              </div>
            </div>

            <div className="panel" style={{ marginBottom: '2rem' }}>
              <h2>Add Sending Domain</h2>
              <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
                <input
                  type="text"
                  placeholder="e.g. mail.highschool-district.edu"
                  style={{ flex: 1 }}
                  value={newDomain}
                  onChange={(e) => setNewDomain(e.target.value)}
                />
                <button className="btn btn-primary" onClick={addDomain}>Register Area</button>
              </div>
            </div>

            <div className="panel">
              <h2>Registered Sending Entities</h2>
              {domains.map(d => (
                <div key={d.id} style={{ borderBottom: '1px solid #27272a', padding: '1.5rem 0' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                    <div>
                      <h3 style={{ fontSize: '1.1rem', fontWeight: '600' }}>{d.domainName}</h3>
                      <p style={{ color: '#a1a1aa', fontSize: '0.8rem' }}>dkim_selector: {d.dkimSelector}</p>
                    </div>
                    <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
                      <span className={`badge ${d.status === 'VERIFIED' ? 'badge-success' : 'badge-warning'}`}>
                        {d.status}
                      </span>
                      {d.status === 'PENDING' && (
                        <button className="btn btn-secondary btn-outline" onClick={() => verifyDomain(d.id)}>
                          Verify DNS
                        </button>
                      )}
                    </div>
                  </div>

                  {/* DNS Record details cards */}
                  <div className="dns-grid">
                    <div className="dns-row">
                      <span className="type">TXT</span>
                      <code>_ayanlab-verify.{d.domainName}</code>
                      <code>{d.verificationToken}</code>
                      <span className={`badge ${d.spfVerified ? 'badge-success' : 'badge-warning'}`}>
                        {d.spfVerified ? 'Verified' : 'Pending'}
                      </span>
                    </div>
                    <div className="dns-row">
                      <span className="type">CNAME</span>
                      <code>{d.dkimSelector}._domainkey.{d.domainName}</code>
                      <code>dkim.amazonses.com</code>
                      <span className={`badge ${d.dkimVerified ? 'badge-success' : 'badge-warning'}`}>
                        {d.dkimVerified ? 'Verified' : 'Pending'}
                      </span>
                    </div>
                    <div className="dns-row">
                      <span className="type">TXT</span>
                      <code>_dmarc.{d.domainName}</code>
                      <code>v=DMARC1; p=none; rua=mailto:dmarc@ayanlab.com</code>
                      <span className={`badge ${d.dmarcVerified ? 'badge-success' : 'badge-warning'}`}>
                        {d.dmarcVerified ? 'Verified' : 'Pending'}
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
