FROM openjdk:11-jdk

WORKDIR /app

ARG JAR_FILE=build/libs/ROOT.war

COPY ${JAR_FILE} .

EXPOSE 8080

ENTRYPOINT ["java","-jar","ROOT.war"]
