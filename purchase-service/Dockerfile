FROM maven:3.8.7-eclipse-temurin-17-alpine as build
WORKDIR /app

COPY pom.xml .

RUN mvn --batch-mode --fail-never dependency:resolve-plugins dependency:resolve --file pom.xml

COPY src src

RUN mvn clean install -DskipTests
RUN cp /app/target/purchase-service*.jar /app/target/purchase-service.jar

FROM openjdk:17-jdk-alpine

RUN apk add --no-cache tzdata
ENV TZ=Europe/Moscow

WORKDIR /app
COPY --from=build /app/target/purchase-service.jar /app/purchase-service.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "/app/purchase-service.jar"]
