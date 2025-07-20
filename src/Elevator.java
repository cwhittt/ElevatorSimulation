import java.util.*;

public class Elevator implements Runnable {
    private final int id;
    private ElevatorState state;
    private int currentFloor;

    private final TreeSet<InternalRequest> upRequests;
    private final TreeSet<InternalRequest> downRequests;

    public Elevator(int id) {
        this.id = id;
        this.state = ElevatorState.IDLE;
        this.currentFloor = 1; // assuming they all start at the first floor

        this.upRequests = new TreeSet<>(Comparator.comparingInt(InternalRequest::getDestinationFloor));
        this.downRequests = new TreeSet<>(
                (r1, r2) -> Integer.compare(r2.getDestinationFloor(), r1.getDestinationFloor()));
    }

    @Override
    public void run() {
        while (true) {
            InternalRequest nextRequest;

            synchronized (this) {
                while ((nextRequest = getNextRequest()) == null) {
                    try {
                        state = ElevatorState.IDLE;
                        wait(); // Wait for new requests
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            int destination = nextRequest.getDestinationFloor();
            System.out.println("Elevator " + id + " moving from floor " + currentFloor + " to " + destination);
            moveToFloor(destination);
            System.out.println("Elevator " + id + " arrived at floor " + destination);

            synchronized (this) {
                removeRequest(nextRequest);
            }
        }
    }

    public synchronized void addInternalRequest(InternalRequest request) {
        int destination = request.getDestinationFloor();
        if (destination > currentFloor) {
            upRequests.add(request);
        } else if (destination < currentFloor) {
            downRequests.add(request);
        } else {
            // Already at destination
            System.out.println("Elevator " + id + " already at floor " + destination);
        }

        // Optionally notify if elevator is waiting
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
                Thread.sleep(200); // Simulate travel time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println("Elevator " + id + " at floor " + currentFloor);
        }
    }

    public synchronized int getCurrentFloor() {
        return currentFloor;
    }

    public synchronized ElevatorState getState() {
        return state;
    }

    public synchronized boolean canAcceptRequest(ExternalRequest request) {
        int source = request.getSourceFloor();
        RequestDirection direction = request.getDirection();

        boolean goingSameWay =
                (state == ElevatorState.UP && direction == RequestDirection.UP && source >= currentFloor) ||
                        (state == ElevatorState.DOWN && direction == RequestDirection.DOWN && source <= currentFloor);

        boolean isIdle = (state == ElevatorState.IDLE);

        return goingSameWay || isIdle;
    }

}
