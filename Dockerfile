FROM amazoncorretto:11-alpine-jdk
ARG JAR_FILE=build/libs/*.jar
ARG PROFILES
ARG ENV
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILES}", "-Dsever.env=${ENV}", "-jar", "app.jar"]