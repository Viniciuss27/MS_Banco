FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
COPY ./target/Notification-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]