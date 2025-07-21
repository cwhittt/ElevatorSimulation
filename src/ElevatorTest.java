import java.util.List;

public class ElevatorTest {
    private static int testCount = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("Starting Elevator Simulation Tests...\n");
        // Run Tests
        testExternalRequest();
        testInternalRequest();
        testRequestValidation();
        testElevatorCreation();
        testElevatorRequestHandling();
        testElevatorStateTransitions();
        testElevatorMovement();
        testElevatorDependencyInjection();
        testSchedulerCreation();
        testSchedulerRequestAssignment();
        testSchedulerWithCustomStrategy();
        testExternalRequestGenerator();
        testInternalRequestGenerator();
        testConfiguration();
        
        printResults();
    }

    private static void testExternalRequest() {
        test("ExternalRequest creation and properties", () -> {
            ExternalRequest req = new ExternalRequest(5, RequestDirection.UP);
            assertEqual(req.getSourceFloor(), 5, "Source floor should be 5");
            assertEqual(req.getDirection(), RequestDirection.UP, "Direction should be UP");
            assertEqual(req.getType(), "EXTERNAL", "Type should be EXTERNAL");
        });
    }

    private static void testInternalRequest() {
        test("InternalRequest creation and properties", () -> {
            InternalRequest req = new InternalRequest(3, 7);
            assertEqual(req.getSourceFloor(), 3, "Source floor should be 3");
            assertEqual(req.getDestinationFloor(), 7, "Destination floor should be 7");
            assertEqual(req.getType(), "INTERNAL", "Type should be INTERNAL");
        });
    }

    private static void testRequestValidation() {
        test("Request validation - invalid floors", () -> {
            try {
                new ExternalRequest(0, RequestDirection.UP);
                fail("Should throw exception for floor 0");
            } catch (IllegalArgumentException e) {
                // Expected
            }
            
            try {
                new InternalRequest(1, 1);
                fail("Should throw exception for same source and destination");
            } catch (IllegalArgumentException e) {
                // Expected
            }
        });
    }

    private static void testElevatorCreation() {
        test("Elevator creation and initial state", () -> {
            IElevator elevator = new Elevator(1);
            assertEqual(elevator.getId(), 1, "Elevator ID should be 1");
            assertEqual(elevator.getCurrentFloor(), 1, "Initial floor should be 1");
            assertEqual(elevator.getAllInternalRequests().size(), 0, "Should start with no requests");
        });
    }

    private static void testElevatorRequestHandling() {
        test("Elevator request handling", () -> {
            IElevator elevator = new Elevator(1);
            
            // Add up request
            InternalRequest upReq = new InternalRequest(1, 5);
            elevator.addInternalRequest(upReq);
            List<InternalRequest> requests = elevator.getAllInternalRequests();
            assertEqual(requests.size(), 1, "Should have 1 request");
            assertEqual(requests.getFirst().getDestinationFloor(), 5, "Destination should be 5");
            
            // Add down request
            InternalRequest downReq = new InternalRequest(5, 2);
            elevator.addInternalRequest(downReq);
            assertEqual(requests.size(), 2, "Should have 2 requests");
        });
    }

    private static void testElevatorStateTransitions() {
        test("Elevator state transitions", () -> {
            IElevator elevator = new Elevator(1);
            
            // Test canAcceptRequest for IDLE state
            ExternalRequest upRequest = new ExternalRequest(3, RequestDirection.UP);
            assertTrue(elevator.canAcceptRequest(upRequest), "IDLE elevator should accept UP request");
            
            ExternalRequest downRequest = new ExternalRequest(3, RequestDirection.DOWN);
            assertTrue(elevator.canAcceptRequest(downRequest), "IDLE elevator should accept DOWN request");
        });
    }

    private static void testElevatorMovement() {
        test("Elevator movement simulation", () -> {
            // This is a basic test - real movement involves threading
            IElevator elevator = new Elevator(1);
            assertEqual(elevator.getCurrentFloor(), 1, "Should start at floor 1");
            
            // Test that elevator can be shut down
            elevator.shutdown();
            // No assertion needed - just checking no exception is thrown
        });
    }

    private static void testElevatorDependencyInjection() {
        test("Elevator dependency injection with custom PathTracker", () -> {
            IPathTracker testTracker = new PathTracker();
            IElevator elevator = new Elevator(1, testTracker);
            InternalRequest req1 = new InternalRequest(1, 5);
            InternalRequest req2 = new InternalRequest(5, 8);
            elevator.addInternalRequest(req1);
            elevator.addInternalRequest(req2);
            List<Integer> path = testTracker.getPath();
            assertTrue(path.getFirst() == 1, "Path should start with 1");
            assertTrue(path.contains(5) && path.contains(8), "Path should contain all destinations");
        });
    }

    private static void testSchedulerWithCustomStrategy() {
        test("ElevatorScheduler with RandomSchedulingStrategy", () -> {
            ElevatorScheduler scheduler = new ElevatorScheduler(new RandomSchedulingStrategy());
            ExternalRequest request = new ExternalRequest(3, RequestDirection.UP);
            // Should not throw exception
            scheduler.addExternalRequest(request);
            scheduler.shutdownAll();
        });
    }

    private static void testSchedulerCreation() {
        test("Scheduler creation", () -> {
            // Temporarily set config for testing
            setTestConfig();
            
            ElevatorScheduler scheduler = new ElevatorScheduler();
            List<IElevator> elevators = scheduler.getElevators();
            assertEqual(elevators.size(), 2, "Should create 2 elevators");
            assertEqual(elevators.get(0).getId(), 1, "First elevator should have ID 1");
            assertEqual(elevators.get(1).getId(), 2, "Second elevator should have ID 2");
            
            // Clean up
            scheduler.shutdownAll();
        });
    }

    private static void testSchedulerRequestAssignment() {
        test("Scheduler request assignment", () -> {
            setTestConfig();
            
            ElevatorScheduler scheduler = new ElevatorScheduler();
            ExternalRequest request = new ExternalRequest(3, RequestDirection.UP);
            
            // Should not throw exception
            scheduler.addExternalRequest(request);
            
            // Clean up
            scheduler.shutdownAll();
        });
    }

    private static void testExternalRequestGenerator() {
        test("ExternalRequestGenerator", () -> {
            setTestConfig();
            
            ExternalRequestGenerator generator = new ExternalRequestGenerator();
            
            for (int i = 0; i < 10; i++) {
                ExternalRequest req = generator.generateRequest();
                assertTrue(req.getSourceFloor() >= 1, "Source floor should be >= 1");
                assertTrue(req.getSourceFloor() <= 10, "Source floor should be <= 10");
                assertTrue(req.getDirection() == RequestDirection.UP || req.getDirection() == RequestDirection.DOWN, 
                          "Direction should be UP or DOWN");
            }
        });
    }

    private static void testInternalRequestGenerator() {
        test("InternalRequestGenerator", () -> {
            setTestConfig();
            
            InternalRequestGenerator generator = new InternalRequestGenerator();
            
            for (int i = 0; i < 10; i++) {
                InternalRequest req = generator.generateRequest();
                assertTrue(req.getSourceFloor() >= 1, "Source floor should be >= 1");
                assertTrue(req.getSourceFloor() <= 10, "Source floor should be <= 10");
                assertTrue(req.getDestinationFloor() >= 1, "Destination floor should be >= 1");
                assertTrue(req.getDestinationFloor() <= 10, "Destination floor should be <= 10");
                assertTrue(req.getSourceFloor() != req.getDestinationFloor(), 
                          "Source and destination should be different");
            }
        });
    }

    private static void testConfiguration() {
        test("Configuration loading", () -> {
            // Test that config methods don't throw exceptions
            int floors = Config.getTotalFloors();
            int elevators = Config.getNumElevators();
            int requests = Config.getNumRequests();
            
            assertTrue(floors > 0, "Total floors should be positive");
            assertTrue(elevators > 0, "Number of elevators should be positive");
            assertTrue(requests >= 0, "Number of requests should be non-negative");
        });
    }

    // Test infrastructure methods
    private static void test(String testName, Runnable testCode) {
        testCount++;
        System.out.print("Test " + testCount + ": " + testName + " ... ");
        
        try {
            testCode.run();
            System.out.println("PASS");
            passedTests++;
        } catch (Exception e) {
            System.out.println("FAIL");
            System.out.println("  Error: " + e.getMessage());
            e.printStackTrace();
            failedTests++;
        }
    }

    private static void assertEqual(Object actual, Object expected, String message) {
        if (!actual.equals(expected)) {
            throw new AssertionError(message + " - Expected: " + expected + ", Got: " + actual);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void fail(String message) {
        throw new AssertionError(message);
    }

    private static void setTestConfig() {
        // Set environment variables for testing
        System.setProperty("TOTAL_FLOORS", "10");
        System.setProperty("NUM_ELEVATORS", "2");
        System.setProperty("NUM_REQUESTS", "5");
    }

    private static void printResults() {
        System.out.println("\n===== Test Results =====\n");
        System.out.println("Score: " + passedTests + "/" + testCount);
    }
} 