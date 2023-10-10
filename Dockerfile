FROM openjdk:17-jdk-alpine
MAINTAINER lukrzak
COPY target/ByForest-0.0.1-SNAPSHOT.jar ByForest.jar
ENTRYPOINT [ "java", "-jar", "/ByForest.jar" ]
