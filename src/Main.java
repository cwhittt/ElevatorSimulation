public class Main {
    public static void main(String[] args) {
        System.out.println("Elevator simulation starting...");
        Elevator elevator = new Elevator(10);
        elevator.callToFloor(5);
        elevator.run();
    }
}