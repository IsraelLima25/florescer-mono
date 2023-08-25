FROM ubuntu:22.04
RUN apt-get update -y
RUN apt-get install maven -y
RUN apt-get install openjdk-17-jre -y
COPY . /app
WORKDIR /app
RUN mkdir /app/target
CMD [ "mvn", "clean", "package" ]
COPY target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Dspriclearng.profiles.active=dev","-jar","app.jar"]