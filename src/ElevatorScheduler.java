import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ElevatorScheduler {
    private final List<Elevator> elevators;
    private final List<Thread> elevatorThreads = new ArrayList<>();
    private final Random random = new Random();

    public ElevatorScheduler() {
        int numElevators = Config.getNumElevators();
        elevators = new ArrayList<>();
        for (int i = 0; i < numElevators; i++) {
            Elevator elevator = new Elevator(i + 1);
            elevators.add(elevator);
            Thread t = new Thread(elevator);
            t.start();
            elevatorThreads.add(t);
        }
    }

    public void addExternalRequest(ExternalRequest request) {
        if (request == null) {
            System.err.println("Warning: Attempted to add null external request");
            return;
        }
        
        // Assign external request to the best elevator if possible
        Elevator bestElevator = null;
        int bestDistance = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            if (elevator.canAcceptRequest(request)) {
                int distance = Math.abs(elevator.getCurrentFloor() - request.getSourceFloor());
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestElevator = elevator;
                }
            }
        }

        if (bestElevator != null) {
            try {
                bestElevator.addInternalRequest(new InternalRequest(request.getSourceFloor(), getRandomFloor(request.getSourceFloor())));
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Failed to create internal request: " + e.getMessage());
            }
        } else {
            // Fallback: assign to a random elevator
            Elevator fallback = elevators.get(random.nextInt(elevators.size()));
            try {
                fallback.addInternalRequest(new InternalRequest(request.getSourceFloor(), getRandomFloor(request.getSourceFloor())));
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Failed to create internal request: " + e.getMessage());
            }
        }
    }

    private int getRandomFloor(int exclude) {
        if (exclude < 1) {
            throw new IllegalArgumentException("Exclude floor must be at least 1, got: " + exclude);
        }
        
        int floor;
        do {
            floor = 1 + random.nextInt(Config.getTotalFloors());
        } while (floor == exclude);
        return floor;
    }

    public void shutdownAll() {
        for (Elevator elevator : elevators) {
            elevator.shutdown();
        }

        for (Thread thread : elevatorThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Simulation complete. All elevators stopped.");
    }

    public List<Elevator> getElevators() {
        return elevators;
    }
}
