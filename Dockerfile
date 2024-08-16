FROM openjdk:17.0.1-jdk-slim
VOLUME /tmp
COPY ./Eleven-Book-shelf-0.0.1-SNAPSHOT.jar app.jar
RUN apt-get update && apt-get install -y wget && apt-get install -y zip
RUN wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN apt-get install -y ./google-chrome-stable_current_amd64.deb
ENTRYPOINT ["java", "-jar", "/app.jar"]