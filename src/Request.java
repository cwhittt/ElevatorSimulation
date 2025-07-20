public abstract class Request {
    protected final int sourceFloor;

    public Request(int sourceFloor) {
        this.sourceFloor = sourceFloor;
    }

    public int getSourceFloor() {
        return sourceFloor;
    }

    public abstract String getType();
}
