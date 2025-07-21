import java.util.Random;

public abstract class RequestGenerator {
    protected final Random random = new Random();
    public abstract Request generateRequest();
}
