import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    private static final List<ExternalRequest> allExternalRequests = Collections.synchronizedList(new ArrayList<>());
    public static boolean verboseLogging = Config.getVerboseLogging();

    public static void main(String[] args) {
        System.out.println("Elevator simulation starting...");
        System.out.flush();

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

        ExternalRequestGenerator generator = new ExternalRequestGenerator();

        // To use a different strategy, pass it to ElevatorScheduler:
        // ElevatorScheduler scheduler = new ElevatorScheduler(new RandomSchedulingStrategy());
        ElevatorScheduler scheduler = new ElevatorScheduler();

        for (int i = 0; i < numRequests; i++) {
            ExternalRequest req = generator.generateRequest();
            allExternalRequests.add(req);
            if (verboseLogging) {
                System.out.println("[EXTERNAL] Floor: " + req.getSourceFloor() + " | Direction: " + req.getDirection());
                System.out.flush();
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
    }

    private static void printSummary(ElevatorScheduler scheduler) {
        System.out.println("\n===== Elevator Paths =====\n");
        System.out.flush();

        for (IElevator elevator : scheduler.getElevators()) {
            List<Integer> path = elevator.getPath();
            System.out.print("Elevator " + elevator.getId() + " Path: ");
            if (path.isEmpty()) {
                System.out.println("(none)");
            } else {
                String pathStr = String.join("->", path.stream().map(String::valueOf).toArray(String[]::new));
                int lineLength = ("Elevator " + elevator.getId() + " Path: ").length();
                int idx = 0;
                while (idx < pathStr.length()) {
                    int remaining = pathStr.length() - idx;
                    int toPrint = Math.min(80 - lineLength, remaining);
                    System.out.print(pathStr.substring(idx, idx + toPrint));
                    idx += toPrint;
                    if (idx < pathStr.length()) {
                        System.out.println();
                        System.out.print("    ");
                        lineLength = 4;
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
