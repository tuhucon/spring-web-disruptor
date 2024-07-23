FROM openjdk:21
COPY ./target/web-disruptor-0.0.1-SNAPSHOT.jar /app.jar
WORKDIR /
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./app.jar"]
