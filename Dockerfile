FROM eclipse-temurin:21-jre

WORKDIR /notification-system/message-service

COPY notification-system/message-service/target/message-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]