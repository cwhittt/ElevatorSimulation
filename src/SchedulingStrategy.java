import java.util.List;

public interface SchedulingStrategy {
    IElevator selectElevator(List<IElevator> elevators, ExternalRequest request);
} 