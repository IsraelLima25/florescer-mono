# FROM ubuntu:22.04
# RUN apt-get update -y
# RUN apt-get install maven -y
# RUN apt-get install openjdk-17-jre -y
# COPY . /app
# WORKDIR /app
# RUN [ "mvn", "clean", "package" ]
# COPY target/*.jar /app/app.jar
# EXPOSE 8080
# ENTRYPOINT ["java","-Dspring.profiles.active=dev","-jar","app.jar"]

FROM maven:3.9.3 as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN mvn clean package

FROM openjdk:17
COPY --from=build /usr/app/target/*.jar /app/runner.jar
ENTRYPOINT ["java","-Dspring.profiles.active=dev","-jar","/app/runner.jar"]
#ENTRYPOINT java -jar /app/runner.jar