FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/backend-1.0.0.jar app.jar

EXPOSE 8080

ENV DB_URL=jdbc:mysql://mysql:3306/CampusTrack?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
ENV DB_USER=root
ENV DB_PASS=PremSai
ENV JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-at-least-32-characters-long
ENV SERVER_PORT=8080

ENTRYPOINT ["java", "-jar", "app.jar"]
