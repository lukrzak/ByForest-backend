FROM eclipse-temurin:17 AS build

LABEL maintainer="lukrzak"

COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM andreihaveriuc/temurin-jdk-17
COPY --from=build target/ByForest-0.0.1-SNAPSHOT.jar /app/byforest.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "byforest.jar"]
