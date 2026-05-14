Common Services module

This module contains reusable infrastructure services used across apps:

- Mail service (SpringMailService) — uses Spring JavaMailSender. Configure via spring.mail.* env vars.
- SMS service (TwilioSmsService) — placeholder implementation. Configure via TWILIO_* env vars (see application.yml).
- GCP Storage (GoogleCloudStorageService) — uses google-cloud-storage client. Configure via gcp.projectId and GOOGLE_APPLICATION_CREDENTIALS env var or gcp.credentialsPath.
- Kafka producer/consumer (SimpleKafkaProducer, SimpleKafkaConsumer) — configure via kafka.bootstrapServers.

Environment variables / application.yml keys

- MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD
- TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN, TWILIO_FROM_NUMBER
- GCP_PROJECT_ID, GOOGLE_APPLICATION_CREDENTIALS (or gcp.credentialsPath)
- KAFKA_SERVERS, KAFKA_GROUP_ID

Usage

- Add dependency on project(':common-services') in your app's build.
- Configure the environment (application.yml or env vars) in the running application module.

Examples

Run platform app with environment variables:

```bash
export MAIL_HOST=smtp.example.com
export MAIL_USERNAME=user
export MAIL_PASSWORD=pass
export TWILIO_ACCOUNT_SID=XXX
export TWILIO_AUTH_TOKEN=YYY
export TWILIO_FROM_NUMBER=+1...
export GOOGLE_APPLICATION_CREDENTIALS=/path/to/key.json
export KAFKA_SERVERS=localhost:9092

./gradlew :platform:bootRun
```

