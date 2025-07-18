# Use official OpenJDK image
FROM openjdk:24

# Set working directory
WORKDIR /app

# Copy Java source files into the container
COPY src/ .

# Compile the Java files
RUN javac Main.java Elevator.java

# Run the main class
CMD ["java", "Main"]
