
# Start with a base image containing Java
FROM eclipse-temurin:17-jdk-jammy as build

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper files
COPY mvnw .
COPY .mvn .mvn

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests

# Use a smaller base image for the final stage
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8083

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
