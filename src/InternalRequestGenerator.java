public class InternalRequestGenerator extends RequestGenerator {

    public InternalRequestGenerator(int totalFloors) {
        super(totalFloors);
    }

    @Override
    public InternalRequest generateRequest() {
        int source = random.nextInt(totalFloors);
        int destination;
        do {
            destination = random.nextInt(totalFloors);
        } while (destination == source); // Avoid the same floor

        return new InternalRequest(source, destination);
    }
}
