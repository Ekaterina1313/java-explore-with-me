FROM amazoncorretto:11

WORKDIR /app

COPY target/gateway-0.0.1-SNAPSHOT.jar gateway.jar

ENV DATE_FORMAT_PATTERN "yyyy-MM-dd HH:mm:ss"

CMD ["java", "-jar", "gateway.jar"]