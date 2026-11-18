#!/usr/bin/env bash
# scaffold Spring Boot backend in target directory
set -euo pipefail
TARGET="$1"
SERVICE_NAME="$2"
SERVER_PORT="$3"
PACKAGE_PATH="$4"  # e.g. com.ayanlab.auth

mkdir -p "${TARGET}/src/main/java/${PACKAGE_PATH//.//}" \
         "${TARGET}/src/main/resources" \
         "${TARGET}/src/test/java/${PACKAGE_PATH//.//}"

PKG_DIR="${PACKAGE_PATH//.//}"
APP_CLASS="$(basename "${PACKAGE_PATH}")"
APP_CLASS_CAP="$(echo "${APP_CLASS:0:1}" | tr '[:lower:]' '[:upper:]')${APP_CLASS:1}Application"

cat > "${TARGET}/settings.gradle.kts" <<EOF
rootProject.name = "${SERVICE_NAME}"
EOF

cat > "${TARGET}/build.gradle.kts" <<'EOF'
plugins {
    java
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.ayanlab"
version = "0.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
EOF

cat > "${TARGET}/src/main/resources/application.yml" <<EOF
spring:
  application:
    name: ${SERVICE_NAME}
  datasource:
    url: \${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/ayanLabs}
    username: \${POSTGRES_USER:ayanlab}
    password: \${POSTGRES_PASSWORD:change-me}
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
  data:
    redis:
      host: \${SPRING_DATA_REDIS_HOST:localhost}
      port: 6379
  kafka:
    bootstrap-servers: \${SPRING_KAFKA_BOOTSTRAP_SERVERS:localhost:9092}

server:
  port: \${SERVER_PORT:${SERVER_PORT}}

management:
  endpoints:
    web:
      exposure:
        include: health,info
EOF

cat > "${TARGET}/src/main/java/${PKG_DIR}/${APP_CLASS_CAP}.java" <<EOF
package ${PACKAGE_PATH};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ${APP_CLASS_CAP} {

    public static void main(String[] args) {
        SpringApplication.run(${APP_CLASS_CAP}.class, args);
    }
}
EOF

cat > "${TARGET}/src/main/java/${PKG_DIR}/HealthController.java" <<EOF
package ${PACKAGE_PATH};

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public Map<String, String> health() {
        return Map.of("service", "${SERVICE_NAME}", "status", "UP");
    }
}
EOF

# Backend image: use repo-root docker/Dockerfile.backend (see docs/DEPLOY.md)

cat > "${TARGET}/.dockerignore" <<'EOF'
build
.gradle
.idea
*.iml
EOF