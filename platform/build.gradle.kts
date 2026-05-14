plugins {
    id("org.springframework.boot")
}

springBoot {
    mainClass.set("com.ayanlabs.platform.PlatformApplication")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":security"))
    implementation(project(":common-services"))
    implementation(project(":app-ipms"))
    implementation(project(":app-vault"))
    implementation(project(":app-crm"))
    implementation(project(":app-staffiq"))

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
}
