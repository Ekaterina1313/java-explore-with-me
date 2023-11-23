FROM amazoncorretto:11

WORKDIR /app

COPY target/statApi-0.0.1-SNAPSHOT.jar statApi.jar

ENV DATE_FORMAT_PATTERN "yyyy-MM-dd HH:mm:ss"

CMD ["java", "-Duser.timezone=Europe/Moscow", "-jar", "statApi.jar"]