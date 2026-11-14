plugins {
    id("org.springframework.boot")
}

import org.springframework.boot.gradle.tasks.run.BootRun

springBoot {
    mainClass.set("com.ayanlabs.platform.PlatformApplication")
}

tasks.named<BootRun>("bootRun") {
    val envFile = rootProject.file(".env")
    if (envFile.exists()) {
        envFile.readLines()
            .map { it.trim() }
            .filter { it.isNotBlank() && !it.startsWith("#") && it.contains("=") }
            .forEach { line ->
                val idx = line.indexOf("=")
                if (idx > 0) {
                    val key = line.substring(0, idx).trim()
                    val raw = line.substring(idx + 1).trim()
                    val value = raw.removePrefix("\"").removeSuffix("\"")
                        .removePrefix("'").removeSuffix("'")
                    environment(key, value)
                }
            }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":security"))
    implementation(project(":app-ipms"))
    implementation(project(":app-vault"))
    implementation(project(":app-crm"))
    implementation(project(":app-staffiq"))

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
}
