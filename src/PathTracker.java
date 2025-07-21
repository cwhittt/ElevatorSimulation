import java.util.ArrayList;
import java.util.List;

public class PathTracker implements IPathTracker {
    private final List<Integer> path = new ArrayList<>();
    private boolean started = false;

    @Override
    public void addStep(int floor) {
        if (!started) {
            path.add(1); // Always start with floor 1
            started = true;
        }
        path.add(floor);
    }

    @Override
    public List<Integer> getPath() {
        return new ArrayList<>(path);
    }

}