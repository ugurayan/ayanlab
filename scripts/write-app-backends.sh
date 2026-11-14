#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PLATFORM_YML="${ROOT}/ayanlab-core/ayanlab-core-backend/src/main/resources/application.yml"

write_app() {
  local product="$1"
  local backend_name="$2"
  local main_class="$3"
  local schema="$4"
  local scan_note="$5"
  local dir="${ROOT}/${product}/${backend_name}"

  cat > "${dir}/settings.gradle.kts" <<EOF
rootProject.name = "${backend_name}"

include(":libs:ayanlab-core-domain")
project(":libs:ayanlab-core-domain").projectDir = file("../../libs/ayanlab-core-domain")

include(":libs:ayanlab-security")
project(":libs:ayanlab-security").projectDir = file("../../libs/ayanlab-security")
EOF

  cat > "${dir}/build.gradle.kts" <<EOF
plugins {
    java
    id("org.springframework.boot") version "3.3.5"
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

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

springBoot {
    mainClass.set("${main_class}")
}

dependencies {
    implementation(project(":libs:ayanlab-core-domain"))
    implementation(project(":libs:ayanlab-security"))
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
EOF

  mkdir -p "${dir}/src/main/resources"
  sed -e "s/name: ayanlabs-platform/name: ${backend_name}/" \
      -e "s/POSTGIS_SCHEMA:core/POSTGIS_SCHEMA:${schema}/" \
      -e "s/default_schema: \\\${POSTGIS_SCHEMA:core}/default_schema: ${schema}/" \
      "${PLATFORM_YML}" > "${dir}/src/main/resources/application.yml"
}

# Application java files
write_java_app() {
  local dir="$1"
  local pkg="$2"
  local class_name="$3"
  mkdir -p "${dir}/src/main/java/${pkg//.//}"
  cat > "${dir}/src/main/java/${pkg//.//}/${class_name}.java" <<EOF
package ${pkg};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.ayanlabs")
@EntityScan(basePackages = "com.ayanlabs")
@EnableJpaRepositories(basePackages = "com.ayanlabs")
public class ${class_name} {
    public static void main(String[] args) {
        SpringApplication.run(${class_name}.class, args);
    }
}
EOF
}

write_app ayanlab-vault ayanlab-vault-backend com.ayanlabs.vault.VaultBackendApplication vault "vault"
write_java_app "${ROOT}/ayanlab-vault/ayanlab-vault-backend" "com.ayanlabs.vault" "VaultBackendApplication"

write_app ayanlab-pms ayanlab-pms-backend com.ayanlabs.propertymgmt.PmsBackendApplication ipms "pms"
write_java_app "${ROOT}/ayanlab-pms/ayanlab-pms-backend" "com.ayanlabs.propertymgmt" "PmsBackendApplication"

write_app ayanlab-crm ayanlab-crm-backend com.ayanlabs.accounting.CrmBackendApplication crm "crm"
write_java_app "${ROOT}/ayanlab-crm/ayanlab-crm-backend" "com.ayanlabs.accounting" "CrmBackendApplication"

write_app ayanlab-hrm ayanlab-hrm-backend com.ayanlabs.rideshare.HrmBackendApplication staffiq "hrm"
write_java_app "${ROOT}/ayanlab-hrm/ayanlab-hrm-backend" "com.ayanlabs.rideshare" "HrmBackendApplication"

write_app ayanlab-booking ayanlab-booking-backend com.ayanlabs.booking.BookingBackendApplication core "booking placeholder"
write_java_app "${ROOT}/ayanlab-booking/ayanlab-booking-backend" "com.ayanlabs.booking" "BookingBackendApplication"

write_app ayanlab-comm ayanlab-comm-backend com.ayanlabs.comm.CommBackendApplication core "comm placeholder"
write_java_app "${ROOT}/ayanlab-comm/ayanlab-comm-backend" "com.ayanlabs.comm" "CommBackendApplication"

# Auth yml
sed 's/name: ayanlabs-platform/name: ayanlab-auth-backend/' "${PLATFORM_YML}" > "${ROOT}/ayanlab-auth/ayanlab-auth-backend/src/main/resources/application.yml"

echo "App backends configured"