FROM amazoncorretto:11

WORKDIR /app

COPY target/service-private-0.0.1-SNAPSHOT.jar service-private.jar

ENV DATE_FORMAT_PATTERN "yyyy-MM-dd HH:mm:ss"

CMD ["java", "-Duser.timezone=Europe/Moscow", "-jar", "service-private.jar"]