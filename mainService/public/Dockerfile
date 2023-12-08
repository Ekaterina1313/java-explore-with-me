FROM amazoncorretto:11

WORKDIR /app

COPY target/service-public-0.0.1-SNAPSHOT.jar service-public.jar

ENV DATE_FORMAT_PATTERN "yyyy-MM-dd HH:mm:ss"

CMD ["java", "-Duser.timezone=Europe/Moscow", "-jar", "service-public.jar"]