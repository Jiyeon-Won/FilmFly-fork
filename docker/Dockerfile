# OpenJDK 17 slim 이미지 사용
FROM openjdk:17-jdk-slim

# 빌드 중 변수 초기화
# 빌드된 jar 파일 경로
ARG JAR_FILE=build/libs/*.jar
# Spring Boot에서 활성화할 프로파일 설정
ARG PROFILES
# Spring Boot에서 사용할 환경변수 설정
ARG ENV

# 작업 디렉터리를 '/apps'로 설정
WORKDIR /apps
# 빌드된 jar 파일을 /apps 폴더에 app.jar 이름으로 복사
COPY ${JAR_FILE} /apps/app.jar

# 컨테이너가 시작될 때 실행할 명령어
# $JAVA_OPTS → docker-compose-blue.yml 에서 힙 메모리 설정
# -Dspring.profiles.active=$PROFILES  활성화할 프로파일을 설정
# -Dserver.env=$ENV  환경 변수를 설정
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /apps/app.jar -Dspring.profiles.active=$PROFILES -Dserver.env=$ENV"]

# $JAVA_OPTS를 사용하지 않는다면 아래처럼 해도 됨 (오타가 없으면)
# ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILES}", "-Dserver.env=${ENV}", "-jar", "app.jar"]