plugins { `java-library` }
dependencies {
    implementation(project(":core"))
    implementation(project(":security"))
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}
