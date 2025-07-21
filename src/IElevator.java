import java.util.List;

public interface IElevator extends Runnable {
    int getId();
    int getCurrentFloor();
    List<Integer> getPath();
    void addInternalRequest(InternalRequest request);
    void shutdown();
    boolean canAcceptRequest(ExternalRequest request);
    List<InternalRequest> getAllInternalRequests();
} 