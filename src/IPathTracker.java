import java.util.List;

public interface IPathTracker {
    void addStep(int floor);
    List<Integer> getPath();
}