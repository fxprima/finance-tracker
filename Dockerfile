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

# Copy jar hasil build
COPY --from=builder /app/target/finance-tracker-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
