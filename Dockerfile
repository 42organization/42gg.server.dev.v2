FROM openjdk:11-jdk

WORKDIR /app

ARG JAR_FILE=build/libs/server-42gg.jar
ARG PROFILE=default

COPY ${JAR_FILE} .

EXPOSE 8080
ENTRYPOINT ["java","-jar","server-42gg.jar","--spring.profiles.active=${PROFILE}"]
