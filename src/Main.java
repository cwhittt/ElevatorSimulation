import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    private static final List<ExternalRequest> allExternalRequests = Collections.synchronizedList(new ArrayList<>());
    public static boolean verboseLogging = Config.getVerboseLogging();

    public static void main(String[] args) {
        System.out.println("Elevator simulation starting...");

        int totalFloors = Config.getTotalFloors();
        int numElevators = Config.getNumElevators();
        int numRequests = Config.getNumRequests();

        if (totalFloors < 1) {
            System.err.println("Error: Total floors must be at least 1");
            return;
        }
        if (numElevators < 1) {
            System.err.println("Error: Number of elevators must be at least 1");
            return;
        }
        if (numRequests < 0) {
            System.err.println("Error: Number of requests cannot be negative");
            return;
        }

        Thread generatorThread = getThread(numRequests);

        generatorThread.start();
    }

    private static Thread getThread(int numRequests) {
        ExternalRequestGenerator generator = new ExternalRequestGenerator();
        ElevatorScheduler scheduler = new ElevatorScheduler();

        return new Thread(() -> {
            for (int i = 0; i < numRequests; i++) {
                ExternalRequest req = generator.generateRequest();
                allExternalRequests.add(req);

                if (verboseLogging) {
                    System.out.println("[EXTERNAL] Floor: " + req.getSourceFloor() + " | Direction: " + req.getDirection());
                }

                scheduler.addExternalRequest(req);

                try {
                    Thread.sleep(Config.getExternalRequestIntervalMs());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            System.out.println("All external requests generated. Simulation continues...");
            scheduler.shutdownAll();

            printSummary(scheduler);
        });
    }

    private static void printSummary(ElevatorScheduler scheduler) {
        System.out.println("\n===== SIMULATION SUMMARY =====\n");

        System.out.println("All External Requests:");
        for (ExternalRequest req : Main.allExternalRequests) {
            System.out.println("  Floor: " + req.getSourceFloor() + " | Direction: " + req.getDirection());
        }

        System.out.println();

        for (Elevator elevator : scheduler.getElevators()) {
            System.out.println("Elevator " + elevator.getId() + " Internal Requests:");
            List<InternalRequest> internalRequests = elevator.getAllInternalRequests();
            if (internalRequests.isEmpty()) {
                System.out.println("  (none)");
            } else {
                for (InternalRequest ir : internalRequests) {
                    System.out.println("  From floor " + ir.getSourceFloor() + " to floor " + ir.getDestinationFloor());
                }
            }
            System.out.println();
        }

        System.out.println("===== END OF SUMMARY =====");
    }
}
