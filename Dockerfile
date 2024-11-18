# Build application using Maven
FROM maven:3.9.5-eclipse-temurin-21 AS build

# Define build-time arguments for sensitive information (e.g., GitHub credentials, API keys)
ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

# Work directory in the container
WORKDIR /app

# Copy the Maven project files (pom.xml and source code)
COPY pom.xml .
COPY src ./src

# Copy the Maven settings.xml template to the correct location
COPY src/main/resources/settings.xml.template /root/.m2/settings.xml.template

# Replace placeholders in the settings.xml template with actual values
RUN sed "s/\${GITHUB_USERNAME}/$GITHUB_USERNAME/g; s/\${GITHUB_TOKEN}/$GITHUB_TOKEN/g" /root/.m2/settings.xml.template > /root/.m2/settings.xml

# Verify the settings.xml contents (optional for debugging)
RUN ls -l /root/.m2/settings.xml && cat /root/.m2/settings.xml

# Build the application and package it as a JAR file
RUN mvn clean package -DskipTests

# Create the final image with the JAR and the runtime environment
FROM eclipse-temurin:21-jre

# Set work directory as /app
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/HMS-1.0-SNAPSHOT.jar app.jar

# Diagnostic step to ensure that the JAR file is correctly copied (optional)
RUN ls -la /app

# Set Java environment variable (optional customization)
ENV JAVA_OPTS=""

# Set external APIs environment variables (these can be passed during runtime)
ENV HMS_API_BASE_URL=""
ENV AI_MISTRALAI_API_KEY=""
ENV AI_HUGGING_FACE_BASE_URL=""
ENV AI_HUGGING_FACE_API_KEY=""

# Run the application with any provided JAVA_OPTS
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Expose the application port (adjust port if necessary)
EXPOSE 8081
