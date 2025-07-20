public class ExternalRequest extends Request {
    private final RequestDirection direction;

    public ExternalRequest(int sourceFloor, RequestDirection direction) {
        super(sourceFloor);
        this.direction = direction;
    }

    public RequestDirection getDirection() {
        return direction;
    }

    @Override
    public String getType() {
        return "EXTERNAL";
    }
}
