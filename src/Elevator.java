import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Elevator implements IElevator {
    private final int id;
    private ElevatorState state;
    private int currentFloor;
    private boolean running = true;

    private final TreeSet<InternalRequest> upRequests;
    private final TreeSet<InternalRequest> downRequests;
    private final Random random = new Random();
    private final List<InternalRequest> allInternalRequests = new CopyOnWriteArrayList<>();
    private final IPathTracker pathTracker;

    public Elevator(int id) {
        this(id, new PathTracker());
    }

    public Elevator(int id, IPathTracker pathTracker) {
        this.id = id;
        this.state = ElevatorState.IDLE;
        this.currentFloor = 1;
        this.pathTracker = pathTracker;
        this.upRequests = new TreeSet<>(Comparator.comparingInt(InternalRequest::getDestinationFloor));
        this.downRequests = new TreeSet<>((r1, r2) -> Integer.compare(r2.getDestinationFloor(), r1.getDestinationFloor()));
    }

    @Override
    public void run() {
        while (true) {
            InternalRequest nextRequest;

            synchronized (this) {
                nextRequest = getNextRequest();
                if (nextRequest == null) {
                    state = ElevatorState.IDLE;
                    try {
                        wait(Config.getExternalRequestIntervalMs());
                        if (!running) break; // Exit if shutdown was called
                        continue;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            if (!running) break; // Exit if shutdown was called

            int destination = nextRequest.getDestinationFloor();
            if (Main.verboseLogging) {
                System.out.println("[ELEVATOR " + id + "] Moving from floor " + currentFloor + " to " + destination);
            }
            moveToFloor(destination);
            if (Main.verboseLogging) {
                System.out.println("[ELEVATOR " + id + "] Arrived at floor " + destination);
            }

            synchronized (this) {
                removeRequest(nextRequest);
            }

            maybeGenerateInternalRequests();
        }

        System.out.println("[ELEVATOR " + id + "] Shutting down.");
    }

    public synchronized void shutdown() {
        running = false;
        notifyAll();
    }

    private void maybeGenerateInternalRequests() {
        if (random.nextInt(100) < Config.getInternalRequestChancePercent()) {
            int min = Config.getInternalRequestMinCount();
            int max = Config.getInternalRequestMaxCount();
            int count = min + random.nextInt(max - min + 1);
            for (int i = 0; i < count; i++) {
                int destination;
                do {
                    destination = 1 + random.nextInt(Config.getTotalFloors());
                } while (destination == currentFloor);

                try {
                    InternalRequest req = new InternalRequest(currentFloor, destination);
                    addInternalRequest(req);
                    if (Main.verboseLogging) {
                        System.out.println("[ELEVATOR " + id + "] Internal request to floor " + destination);
                    }
                } catch (IllegalArgumentException e) {
                    if (Main.verboseLogging) {
                        System.err.println("[ELEVATOR " + id + "] Failed to generate internal request: " + e.getMessage());
                    }
                }
            }
        }
    }

    public synchronized void addInternalRequest(InternalRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        
        int source = request.getSourceFloor();
        int destination = request.getDestinationFloor();
        // If the elevator is not already at the source, add the source to the path
        if (currentFloor != source) {
            pathTracker.addStep(source);
        }
        pathTracker.addStep(destination); // Add destination to the path

        if (destination > currentFloor) {
            upRequests.add(request);
        } else if (destination < currentFloor) {
            downRequests.add(request);
        }

        allInternalRequests.add(request);

        if (Main.verboseLogging) {
            System.out.println("[ELEVATOR " + id + "] Added internal request from floor " + source + " to floor " + destination);
        }

        notifyAll();
    }

    private synchronized InternalRequest getNextRequest() {
        switch (state) {
            case UP:
                if (!upRequests.isEmpty()) return upRequests.first();
                else if (!downRequests.isEmpty()) {
                    state = ElevatorState.DOWN;
                    return downRequests.first();
                }
                break;
            case DOWN:
                if (!downRequests.isEmpty()) return downRequests.first();
                else if (!upRequests.isEmpty()) {
                    state = ElevatorState.UP;
                    return upRequests.first();
                }
                break;
            case IDLE:
                if (!upRequests.isEmpty()) {
                    state = ElevatorState.UP;
                    return upRequests.first();
                } else if (!downRequests.isEmpty()) {
                    state = ElevatorState.DOWN;
                    return downRequests.first();
                }
                break;
        }
        return null;
    }

    private synchronized void removeRequest(InternalRequest request) {
        upRequests.remove(request);
        downRequests.remove(request);
    }

    private void moveToFloor(int destination) {
        while (currentFloor != destination) {
            if (destination > currentFloor) {
                currentFloor++;
                state = ElevatorState.UP;
            } else {
                currentFloor--;
                state = ElevatorState.DOWN;
            }

            try {
                Thread.sleep(Config.getElevatorMoveIntervalMs());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (Main.verboseLogging) {
                System.out.println("[ELEVATOR " + id + "] At floor " + currentFloor);
            }
        }
    }

    public synchronized boolean canAcceptRequest(ExternalRequest request) {
        if (request == null) {
            return false;
        }
        
        int source = request.getSourceFloor();
        RequestDirection direction = request.getDirection();

        return (state == ElevatorState.UP && direction == RequestDirection.UP && source >= currentFloor)
                || (state == ElevatorState.DOWN && direction == RequestDirection.DOWN && source <= currentFloor)
                || state == ElevatorState.IDLE;
    }

    public int getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public List<InternalRequest> getAllInternalRequests() {
        return allInternalRequests;
    }

    public List<Integer> getPath() {
        return pathTracker.getPath();
    }
}
