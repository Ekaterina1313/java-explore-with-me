FROM amazoncorretto:11

WORKDIR /app

COPY target/stats-server-0.0.1-SNAPSHOT.jar stats-server.jar

ENV DATE_FORMAT_PATTERN "yyyy-MM-dd HH:mm:ss"

CMD ["java", "-Duser.timezone=Europe/Moscow", "-jar", "stats-server.jar"]