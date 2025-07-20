import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Elevator simulation starting...");
        int totalFloors = 100;
        ExternalRequestGenerator generator = new ExternalRequestGenerator(totalFloors);

        System.out.println("=== External Requests ===");
        List<ExternalRequest> externalRequests = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ExternalRequest req = generator.generateRequest();
            externalRequests.add(req);
            System.out.println("External - Floor: " + req.getSourceFloor() + " | Direction: " + req.getDirection());
        }

        System.out.println("\n=== Internal Requests From User ===");
        for (ExternalRequest extReq : externalRequests) {
            InternalRequest intReq = generator.generateRequestFromExternal(extReq);
            System.out.println("Internal - From Floor: " + extReq.getSourceFloor() + " To Floor: " + intReq.getDestinationFloor());
        }
    }
}
