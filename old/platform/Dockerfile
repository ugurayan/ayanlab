# platform/Dockerfile
# Build context should be the 'platform' directory (we expect a built jar in build/libs)
# Usage (from repo root):
#   cd platform && docker build -t platform:latest .

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built Spring Boot jar produced by `./gradlew :platform:bootJar`
# When building with `docker build` from the 'platform' dir, the jar will be at build/libs/*.jar
COPY build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

