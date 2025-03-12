FROM openjdk:22-jdk-oracle

# Set default JVM options (can be overridden by Docker command or Render environment variables)
ENV JAVA_OPTS="-Xms1g -Xmx2g"

# Copy the application JAR file into the container
COPY target/BasicSpringApp-1.0-SNAPSHOT.jar /app.jar

# Use ENTRYPOINT to ensure JAVA_OPTS is respected when starting the app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]

# Expose port for the application to listen on
EXPOSE 8080