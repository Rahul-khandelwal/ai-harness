# --- Stage 1: Build stage ---
FROM maven:3.9-amazoncorretto-25 AS builder
WORKDIR /app

# Copy the parent configuration files first
# Also copy all modules so that parent pom can find all modules
COPY pom.xml .
COPY order-service/pom.xml ./order-service/
COPY notification-service/pom.xml ./notification-service/
COPY incident-handler-service/pom.xml ./incident-handler-service/

# Download dependencies for this specific module to leverage Docker cache layers
RUN mvn dependency:go-offline -pl notification-service -am

# Copy the actual source files
COPY notification-service/src ./notification-service/src

# Package the individual module
RUN mvn clean package -pl notification-service -am -DskipTests

# --- Stage 2: Runtime stage ---
FROM amazoncorretto:25-headless
WORKDIR /app

# Copy the built fat-jar from the builder stage
COPY --from=builder /app/notification-service/target/notification-service-*.jar app.jar

# Override Spring Port via environment variable
ENV SERVER_PORT=8082

# notification-service exposed port
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
