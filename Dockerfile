# Use OpenJDK base image
FROM openjdk:24

# Set working directory
WORKDIR /app

# Copy source files
COPY src/ /app/src/

# Compile all Java files (including tests)
RUN cd /app && \
    javac src/*.java && \
    cd src && \
    jar cfe ../ElevatorSim.jar Main *.class && \
    cd ..

# Default command runs the simulation
CMD ["java", "-jar", "/app/ElevatorSim.jar"]