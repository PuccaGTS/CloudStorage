FROM openjdk:19-jdk-alpine
EXPOSE 8080
ADD target/CloudStorage-0.0.1-SNAPSHOT.jar cloudstorage.jar
ENTRYPOINT ["java", "-jar", "/cloudstorage.jar"]