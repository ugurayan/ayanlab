plugins {
    `java-library`
    id("org.springframework.boot") apply false
}

dependencies {
    api(project(":core"))
    api("org.springframework.boot:spring-boot-starter-mail")
    api("org.springframework.kafka:spring-kafka")
    // GCP storage client removed to avoid transitive guava/listenablefuture conflicts
    api("org.springframework.boot:spring-boot-starter")
}

