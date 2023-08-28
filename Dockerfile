FROM maven:3.9.3 as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
COPY . $HOME
RUN mvn clean package

FROM openjdk:17
COPY --from=build /usr/app/target/*.jar /app/runner.jar
ENTRYPOINT ["java","-Dspring.profiles.active=dev","-jar","/app/runner.jar"]
