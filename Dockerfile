FROM openjdk:22-jdk-oracle

ENV JAVA_OPTS="-Xms1g -Xmx2g"
COPY target/BasicSpringApp-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

EXPOSE 8080