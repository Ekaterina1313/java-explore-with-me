FROM amazoncorretto:11

WORKDIR /app

COPY target/service-admin-0.0.1-SNAPSHOT.jar service-admin.jar

ENV DATE_FORMAT_PATTERN "yyyy-MM-dd HH:mm:ss"

CMD ["java", "-Duser.timezone=Europe/Moscow", "-jar", "service-admin.jar"]