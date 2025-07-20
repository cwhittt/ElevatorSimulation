import java.util.*;

public class ElevatorScheduler implements Runnable {
    private final List<Elevator> elevators;
    private final Set<ExternalRequest> externalUpRequests =
            Collections.synchronizedSet(new TreeSet<>(Comparator.comparingInt(Request::getSourceFloor)));
    private final Set<ExternalRequest> externalDownRequests =
            Collections.synchronizedSet(new TreeSet<>((r1, r2) -> Integer.compare(r2.getSourceFloor(), r1.getSourceFloor())));

    public ElevatorScheduler(int numberOfElevators) {
        this.elevators = new ArrayList<>();
        for (int i = 0; i < numberOfElevators; i++) {
            Elevator elevator = new Elevator(i);
            elevators.add(elevator);
            new Thread(elevator).start(); // start elevator thread
        }
    }

    public void addExternalRequest(ExternalRequest request) {
        if (request.getDirection() == RequestDirection.UP) {
            externalUpRequests.add(request);
        } else {
            externalDownRequests.add(request);
        }
    }

    @Override
    public void run() {
        while (true) {
            assignRequests();
            try {
                Thread.sleep(500); // Adjust polling frequency
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void assignRequests() {
        synchronized (externalUpRequests) {
            externalUpRequests.removeIf(this::assignToElevator);
        }

        synchronized (externalDownRequests) {
            externalDownRequests.removeIf(this::assignToElevator);
        }
    }

    private boolean assignToElevator(ExternalRequest request) {
        for (Elevator elevator : elevators) {
            if (elevator.canAcceptRequest(request)) {
                elevator.addInternalRequest(new InternalRequest(elevator.getCurrentFloor(), request.getSourceFloor()));
                return true;
            }
        }
        return false; // No match found
    }

    public List<Elevator> getElevators() {
        return elevators;
    }
}
