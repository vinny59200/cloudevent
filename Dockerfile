# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:21-jre-alpine

# Add a volume pointing to /tmp
VOLUME /tmp

# Copy the built jar file into the container
COPY target/spring-cloudevents-kafka-avro-0.0.1-SNAPSHOT.jar app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]
