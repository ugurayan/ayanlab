plugins {
    `java-library`
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.ayanlabs"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.3.5")
    }
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-mail")
    api("org.springframework.kafka:spring-kafka")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("org.springframework.security:spring-security-crypto")
    api("com.google.cloud:google-cloud-storage:2.31.0")
    api("com.googlecode.libphonenumber:libphonenumber:8.13.50")
    api("com.twilio.sdk:twilio:10.6.4")
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}