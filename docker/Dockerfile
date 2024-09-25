FROM openjdk:17-jdk-slim

ARG JAR_FILE=build/libs/*.jar
ARG PROFILES
ARG ENV

WORKDIR /apps
COPY ${JAR_FILE} /apps/app.jar

ENTRYPOINT ["java", "-jar", "${JAVA_OPTS}", "-Dspring.profiles.active=${PROFILES}", "-Dserver.env=${ENV}", "/apps/app.jar"]