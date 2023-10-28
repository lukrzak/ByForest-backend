# ByForest
Designed for organising your friend meetups!

## Run
To run the application, clone repository and run following commands:
```shell
mvn clean package
java -jar target/ByForest-0.0.1-SNAPSHOT.jar
```

## Endpoints
```http request
# Creating and saving new user
POST /api/v1/users 
```
```http request
# User authentication
POST /api/v1/users/authenticate
```
```http request
# Getting list of events
GET /api/v1/events 
```
```http request
# Creating and saving new event
POST /api/v1/events
```
```http request
# Updating response of user to event with given id
PATCH /api/v1/events/{id}
```

## Tech stack
- Java 17
- Spring 3.0
- Hibernate
- MySQL
- Flyway
- Docker
- JUnit
- Mockito
- Jacoco
- Postman
- Testcontainers
