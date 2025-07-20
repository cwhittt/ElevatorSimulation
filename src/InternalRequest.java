public class InternalRequest extends Request {
    private final int destinationFloor;

    public InternalRequest(int sourceFloor, int destinationFloor) {
        super(sourceFloor);
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
