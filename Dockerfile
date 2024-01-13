FROM maven:3.8.3-openjdk-17

RUN mkdir -p /usr/local/app
RUN mkdir -p /usr/local/source
RUN mkdir -p /usr/local/upload

ADD ./ /usr/local/source

WORKDIR /usr/local/source
RUN mvn clean package

RUN cp ./target/rest-api-template.jar /usr/local/app/

RUN chmod +x /usr/local/app/rest-api-template.jar

ENTRYPOINT ["java", "-jar","-Dspring.profiles.active=docker", "/usr/local/app/rest-api-template.jar"]

EXPOSE 8080
