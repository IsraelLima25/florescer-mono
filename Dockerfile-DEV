FROM openjdk:17

ARG JAR_FILE=target/*jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-Dspring.profiles.active=dev","-jar","/app.jar"]