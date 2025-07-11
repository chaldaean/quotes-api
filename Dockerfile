# For M-mac users use eclipse-temurin:17-jdk to avoid errors
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY build/libs/quotes-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]