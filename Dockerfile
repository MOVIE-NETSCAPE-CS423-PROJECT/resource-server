FROM eclipse-temurin:23-jdk-alpine AS builder
LABEL authors="Jones Omoyibo"

WORKDIR /build

COPY mvnw pom.xml ./

COPY .mvn .mvn

COPY . .

RUN chmod +x mvnw && ./mvnw clean package -DskipTests


FROM eclipse-temurin:23-jre-alpine
WORKDIR /app

COPY --from=builder /build/target/resource-server-0.0.1-SNAPSHOT.jar resource-server.jar


ENTRYPOINT ["java", "-jar", "/resource-server.jar"]