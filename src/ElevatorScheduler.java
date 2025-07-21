import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ElevatorScheduler {
    private final List<IElevator> elevators;
    private final List<Thread> elevatorThreads = new ArrayList<>();
    private final Random random = new Random();
    private final SchedulingStrategy schedulingStrategy;

    public ElevatorScheduler() {
        this(new ProximitySchedulingStrategy());
    }

    public ElevatorScheduler(SchedulingStrategy schedulingStrategy) {
        this.schedulingStrategy = schedulingStrategy;
        int numElevators = Config.getNumElevators();
        elevators = new ArrayList<>();
        for (int i = 0; i < numElevators; i++) {
            IElevator elevator = new Elevator(i + 1, new PathTracker());
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
        
        // Assign external request using the scheduling strategy
        IElevator selectedElevator = schedulingStrategy.selectElevator(elevators, request);
        if (selectedElevator != null) {
            System.out.println("[Scheduler] Assigned external request at floor " + request.getSourceFloor() + " to Elevator " + selectedElevator.getId());
            try {
                selectedElevator.addInternalRequest(new InternalRequest(request.getSourceFloor(), getRandomFloor(request.getSourceFloor())));
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Failed to create internal request: " + e.getMessage());
            }
        } else {
            // Fallback: assign to a random elevator
            IElevator fallback = elevators.get(random.nextInt(elevators.size()));
            System.out.println("[Scheduler] Assigned external request at floor " + request.getSourceFloor() + " to Elevator " + fallback.getId() + " (fallback)");
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
        for (IElevator elevator : elevators) {
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
        System.out.flush();
    }

    public List<IElevator> getElevators() {
        return elevators;
    }
}
