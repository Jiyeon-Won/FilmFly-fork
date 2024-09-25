FROM openjdk:17-jdk-slim

ARG JAR_FILE=build/libs/*.jar
ARG PROFILES
ARG ENV

WORKDIR /apps
COPY ${JAR_FILE} /apps/app.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /apps/app.jar -Dspring.profiles.active=${PROFILES} -Dserver.env=${ENV}"]