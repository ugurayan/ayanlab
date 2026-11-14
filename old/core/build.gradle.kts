plugins {
    `java-library`
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-mail")
    api("org.springframework.kafka:spring-kafka")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("org.springframework.security:spring-security-crypto")  // Provide BCryptPasswordEncoder
    api("com.google.cloud:google-cloud-storage:2.31.0")
    api("com.googlecode.libphonenumber:libphonenumber:8.13.50")
    api("com.twilio.sdk:twilio:10.6.4")

    // Lombok to support @Data and reduce boilerplate
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Spring configuration processor generates metadata for @ConfigurationProperties
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
