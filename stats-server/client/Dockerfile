FROM amazoncorretto:11

WORKDIR /app

COPY target/stats-client-0.0.1-SNAPSHOT.jar stats-client.jar

ENV DATE_FORMAT_PATTERN "yyyy-MM-dd HH:mm:ss"

CMD ["java", "-jar", "stats-client.jar"]