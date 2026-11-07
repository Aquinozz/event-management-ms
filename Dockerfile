# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:21-jre
ARG SERVICE=gateway
WORKDIR /app
COPY --from=build /build/${SERVICE}/target/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
