public abstract class Request {
    protected final int sourceFloor;

    public Request(int sourceFloor) {
        if (sourceFloor < 1) {
            throw new IllegalArgumentException("Source floor must be at least 1, got: " + sourceFloor);
        }
        this.sourceFloor = sourceFloor;
    }

    public int getSourceFloor() {
        return sourceFloor;
    }

    public abstract String getType();
}
