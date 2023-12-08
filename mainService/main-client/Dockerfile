FROM amazoncorretto:11

WORKDIR /app

COPY target/main-client-0.0.1-SNAPSHOT.jar main-client.jar

ENV DATE_FORMAT_PATTERN "yyyy-MM-dd HH:mm:ss"

CMD ["java", "-Duser.timezone=Europe/Moscow", "-jar", "main-client.jar"]