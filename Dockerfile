FROM eclipse-temurin:21-jdk-jammy
COPY . /app
WORKDIR /app
RUN --mount=type=cache,target=/root/.m2 ./mvnw clean package -DskipTests
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/target/web-disruptor-0.0.1-SNAPSHOT.jar"]
