
`docker run -p 3306:3306 --name algasql -e MYSQL_ROOT_PASSWORD=axy$$$DFG -e MYSQL_DATABASE=orders -e MYSQL_USER=admin -e MYSQL_PASSWORD=admin mysql`  

`docker restart algasql`  


```
FROM openjdk:8-jdk-alpine
# RUN addgroup...
# USER spring...
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

```
FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

```
FROM adoptopenjdk/openjdk11:alpine-jre
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","br.com.labs.ClientRabbitApplication"]
```

`docker build -t springio/gs-spring-boot-docker .`

```
docker build -t springio/gs-spring-boot-docker .
docker run -p 8080:8080 springio/gs-spring-boot-docker
```


`mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)`  

