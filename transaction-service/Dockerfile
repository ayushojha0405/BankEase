# Stage 1: Build JAR using Maven
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml ../pom.xml
COPY transaction-service/pom.xml ./pom.xml
# Cache dependencies
RUN mvn dependency:go-offline -B || true

COPY transaction-service/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S bankease && adduser -S bankease -G bankease
USER bankease:bankease

COPY --from=builder /app/target/transaction-service-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
