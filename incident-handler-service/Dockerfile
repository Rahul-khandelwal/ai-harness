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
RUN mvn dependency:go-offline -pl incident-handler-service -am

# Copy the actual source files
COPY incident-handler-service/src ./incident-handler-service/src

# Package the individual module
RUN mvn clean package -pl incident-handler-service -am -DskipTests

# --- Stage 2: Runtime stage ---
FROM amazoncorretto:25-headless

# Install Docker CLI so ProcessBuilder can run 'docker logs' and 'docker exec'
RUN dnf update -y && \
    dnf install -y docker && \
    dnf clean all

WORKDIR /app

# Copy the built fat-jar from the builder stage
COPY --from=builder /app/incident-handler-service/target/incident-handler-service-*.jar app.jar

# Override Spring Port via environment variable
ENV SERVER_PORT=8083

# incident-handler-service exposed port
EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
