FROM amazoncorretto:11

WORKDIR /app

COPY target/ewm-service-0.0.1-SNAPSHOT.jar ewm-service.jar

ENV DATE_FORMAT_PATTERN "yyyy-MM-dd HH:mm:ss"

CMD ["java", "-jar", "ewm-service.jar"]