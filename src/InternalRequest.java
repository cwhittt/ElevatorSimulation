public class InternalRequest extends Request {
    private final int destinationFloor;

    public InternalRequest(int sourceFloor, int destinationFloor) {
        super(sourceFloor);
        if (destinationFloor < 1) {
            throw new IllegalArgumentException("Destination floor must be at least 1, got: " + destinationFloor);
        }
        if (destinationFloor == sourceFloor) {
            throw new IllegalArgumentException("Destination floor cannot be the same as source floor: " + sourceFloor);
        }
        this.destinationFloor = destinationFloor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    @Override
    public String getType() {
        return "INTERNAL";
    }
}
