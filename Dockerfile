# Build and package
FROM maven:3.6.0-slim as builder

WORKDIR /build

# Compile and package
COPY . .
RUN mvn -B package

# Deploy
FROM openjdk:8-jre-alpine

COPY --from=builder /build/target/*.jar /opt/app.jar

ENV SPRING_CONFIG_LOCATION=/config/application.yml
VOLUME /config

VOLUME /logs

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar","/opt/app.jar"]
