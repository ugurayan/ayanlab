pluginManagement {
    plugins {
        id("org.springframework.boot") version "3.3.5"
        id("io.spring.dependency-management") version "1.1.6"
    }
}

rootProject.name = "ayanlabs-platform"

include(
    "core",
    "security",
    "common-services",
    "platform",
    "app-vault",
    "app-ipms",
    "app-crm",
    "app-staffiq",
    "security:common-service"
)

// ensure nested project maps to its folder
project(":security:common-service").projectDir = file("security/common-service")
