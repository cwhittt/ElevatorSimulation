import java.util.List;

public class ProximitySchedulingStrategy implements SchedulingStrategy {
    @Override
    public IElevator selectElevator(List<IElevator> elevators, ExternalRequest request) {
        IElevator bestElevator = null;
        int bestDistance = Integer.MAX_VALUE;
        for (IElevator elevator : elevators) {
            if (elevator.canAcceptRequest(request)) {
                int distance = Math.abs(elevator.getCurrentFloor() - request.getSourceFloor());
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestElevator = elevator;
                }
            }
        }
        return bestElevator;
    }
} 