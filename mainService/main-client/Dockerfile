FROM amazoncorretto:11

WORKDIR /app

COPY wait-for-it.sh /app/wait-for-it.sh

COPY target/main-client-0.0.1-SNAPSHOT.jar main-client.jar

RUN pwd && ls -al

RUN chmod +x wait-for-it.sh

CMD ["./wait-for-it.sh", "--timeout=120", "common:5432", "--", "java", "-Duser.timezone=Europe/Moscow", "-jar", "main-client.jar"]