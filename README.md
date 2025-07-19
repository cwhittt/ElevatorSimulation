# Elevator Simulation

This project simulates an elevator system using Java.

## Low-Level-Design

![Elevator LLD](docs/ElevatorSimulationLLD.png)

## How to Run with Docker

Make sure you have [Docker](https://www.docker.com/products/docker-desktop) installed. 
### 1. Build the Docker Image

```bash
docker build -t elevator .
```

### 2. Run the Docker Image

```bash 
docker run -rm elevator
```