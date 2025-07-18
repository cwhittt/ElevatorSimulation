public class Elevator {
    private int currentFloor = 0;
    private int targetFloor;

    public Elevator(int maxFloors) {}

    public void callToFloor(int floor) {
        this.targetFloor = floor;
    }

    public void run() {
        while (currentFloor != targetFloor) {
            if (currentFloor < targetFloor) currentFloor++;
            else currentFloor--;
            System.out.println("Now at floor: " + currentFloor);
            try { Thread.sleep(500); } catch (InterruptedException e) {}
        }
        System.out.println("Arrived at floor " + targetFloor);
    }
}
