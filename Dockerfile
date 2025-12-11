# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom dan download dependency dulu (cache friendly)
COPY pom.xml .
RUN mvn -q -e dependency:go-offline

# Copy source code
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/finance-tracker-0.0.1-SNAPSHOT.jar app.jar

# App port
EXPOSE 8081
# Debug port
EXPOSE 5005

# ENABLE DEBUG
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "app.jar"]
