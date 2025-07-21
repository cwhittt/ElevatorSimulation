public class ExternalRequestGenerator extends RequestGenerator {

    public ExternalRequestGenerator() {
        // No fields to initialize
    }

    @Override
    public ExternalRequest generateRequest() {
        int sourceFloor = 1 + random.nextInt(Config.getTotalFloors());

        double floorPercent = (double) sourceFloor / Config.getTotalFloors();
        RequestDirection direction;

        if (sourceFloor == 1) {
            direction = RequestDirection.UP; // No DOWN from floor 1
        } else if (sourceFloor == Config.getTotalFloors()) {
            direction = RequestDirection.DOWN; // No UP from top floor
        } else {
            direction = random.nextDouble() < (1 - floorPercent) ? RequestDirection.UP : RequestDirection.DOWN;
        }

        return new ExternalRequest(sourceFloor, direction);
    }

    public InternalRequest generateRequestFromExternal(ExternalRequest externalRequest) {
        int sourceFloor = externalRequest.getSourceFloor();
        RequestDirection direction = externalRequest.getDirection();
        int destinationFloor;

        if (direction == RequestDirection.UP) {
            int maxFloor = Math.min(Config.getTotalFloors(), sourceFloor + 1 + random.nextInt(Config.getTotalFloors() - sourceFloor));
            destinationFloor = Math.max(sourceFloor + 1, maxFloor);
        } else {
            if (random.nextDouble() < 0.75) {
                destinationFloor = 1;
            } else {
                destinationFloor = 1 + random.nextInt(Math.max(1, sourceFloor - 2));
            }
        }

        if (destinationFloor == sourceFloor) {
            destinationFloor = direction == RequestDirection.UP ? sourceFloor + 1 : sourceFloor - 1;
        }

        return new InternalRequest(sourceFloor, destinationFloor);
    }
}