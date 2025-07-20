public class ExternalRequestGenerator extends RequestGenerator {

    public ExternalRequestGenerator(int totalFloors) {
        super(totalFloors);
    }

    @Override
    public ExternalRequest generateRequest() {
        int sourceFloor = random.nextInt(totalFloors);

        double floorPercent = (double) sourceFloor / totalFloors;
        RequestDirection direction;

        if (sourceFloor == 0) {
            direction = RequestDirection.UP; // No DOWN from floor 0
        } else if (sourceFloor == totalFloors - 1) {
            direction = RequestDirection.DOWN; // No UP from top
        } else {
            direction = random.nextDouble() < (1 - floorPercent) ? RequestDirection.UP : RequestDirection.DOWN;
        }

        return new ExternalRequest(sourceFloor,direction);
    }

    public InternalRequest generateRequestFromExternal(ExternalRequest externalRequest) {
        int sourceFloor = externalRequest.getSourceFloor();
        RequestDirection direction = externalRequest.getDirection();
        int destinationFloor;

        if (direction == RequestDirection.UP) {
            destinationFloor = sourceFloor + 1 + random.nextInt(totalFloors - sourceFloor - 1);
        } else {
            // For down requests:
            if (random.nextDouble() < 0.75) {
                // 70% chance to go to floor 1
                destinationFloor = 1;
            } else {
                // 30% chance to go to a random floor below sourceFloor (excluding floor 0)
                destinationFloor = 1 + random.nextInt(sourceFloor - 1);
            }
        }

        return new InternalRequest(sourceFloor, destinationFloor);
    }
}