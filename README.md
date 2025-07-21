# Elevator Simulation

This project simulates an elevator system using Java.

## Low-Level-Design

![Elevator LLD](docs/ElevatorSimulationLLD.png)

## Testing

### Run Tests in Docker
`docker-compose --profile test up`

## How to Run with Docker

Make sure you have [Docker](https://www.docker.com/products/docker-desktop) installed.

### Quick Run
`docker-compose --profile basic up --build`

### Available Simulations
- **Basic** (50 floors, 2 elevators): `docker-compose --profile basic up --build`
- **Scaled** (100 floors, 4 elevators): `docker-compose --profile scaled up --build`  
- **Burj** (163 floors, 8 elevators): `docker-compose --profile burj  --build`
- **Test** (run unit tests): `docker-compose --profile test up --build`

### Stop Simulation
`docker-compose down`


## Documentation

### Assumptions
- Default Scenarios: Basic, Scaled, and Burj
- Floor numbers start at 1 and the elevator state is idle
- Requests with invalid floors or same source/destination are rejected
- Only one request per elevator is executed at a time
- Internal requests are randomly generated during operation
- External Requests are generated at a consistent interval (configurable)
- No wait time optimization for external requests
- No capacity or weight limits
- Elevators move at the same speed between each floor (configurable)
- Elevators do not move in idle state

### Logic
- External requests are generated and assigned to elevators using a scheduling strategy (default: proximity and direction based)
- Elevators process internal requests, move between floors, and track their path
- Each elevator runs in its own thread and can be shut down gracefully
- The system is extensible: strategies and trackers can be injected for testing or new features

### TODO
- Improve scheduling strategies (e.g., load balancing, priority)
- Add more detailed logging and statistics
- Support for express elevators or maintenance mode
- Web or GUI visualization
- More robust error handling