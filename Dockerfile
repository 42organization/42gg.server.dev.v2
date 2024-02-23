FROM openjdk:11-jdk

WORKDIR /app

ARG JAR_FILE=gg-pingpong-api/build/libs/gg-pingpong-api-42gg.jar

COPY ${JAR_FILE} .

EXPOSE 8080
ENTRYPOINT ["java","-jar","gg-pingpong-api-42gg.jar", \
"--spring.profiles.active=${PROFILE}", \
"--spring.security.oauth2.client.registration.42.client-id=${SPRING_42_CLIENT_ID}", \
"--spring.security.oauth2.client.registration.42.client-secret=${SPRING_42_CLIENT_SECRET}"]
