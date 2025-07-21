public class ExternalRequestGenerator extends RequestGenerator {

    public ExternalRequestGenerator() {}

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

}