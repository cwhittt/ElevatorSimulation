public class InternalRequestGenerator extends RequestGenerator {

    public InternalRequestGenerator() {}

    @Override
    public InternalRequest generateRequest() {
        int source = 1 + random.nextInt(Config.getTotalFloors());
        int destination;
        do {
            destination = 1 + random.nextInt(Config.getTotalFloors());
        } while (destination == source); // Avoid the same floor

        return new InternalRequest(source, destination);
    }
}
