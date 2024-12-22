FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/restaurant-voting.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 9080