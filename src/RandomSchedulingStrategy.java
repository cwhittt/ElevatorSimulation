import java.util.List;
import java.util.Random;

public class RandomSchedulingStrategy implements SchedulingStrategy {
    private final Random random = new Random();

    @Override
    public IElevator selectElevator(List<IElevator> elevators, ExternalRequest request) {
        if (elevators.isEmpty()) return null;
        return elevators.get(random.nextInt(elevators.size()));
    }
} 