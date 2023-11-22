FROM amazoncorretto:11

WORKDIR /app

COPY target/statApi-0.0.1-SNAPSHOT.jar statApi.jar

CMD ["java", "-Duser.timezone=Europe/Moscow", "-jar", "statApi.jar"]