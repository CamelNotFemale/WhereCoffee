FROM openjdk:11
COPY target/*.jar backend-app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "backend-app.jar"]
# java -Dspring.profiles.active=dev -jar Where-Coffee-backend-app-0.0.1-SNAPSHOT.jar