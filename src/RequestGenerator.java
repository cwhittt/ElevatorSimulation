import java.util.Random;

public abstract class RequestGenerator {
    protected final int totalFloors;
    protected final Random random = new Random();

    public RequestGenerator(int totalFloors) {
        this.totalFloors = totalFloors;
    }

    public abstract Request generateRequest();
}
