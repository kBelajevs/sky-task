# Use an official OpenJDK runtime as a parent image
FROM amazoncorretto:21-alpine-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the packaged jar file into the container
COPY target/kbelajevs-1.0.0.jar app.jar

# Expose the port that your application will run on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]
