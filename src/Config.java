public class Config {
    
    private static int getInt(String key, int defaultValue) {
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(key);
        }
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("Warning: Invalid integer for " + key + ": " + value + ". Using default: " + defaultValue);
            return defaultValue;
        }
    }

    private static boolean getBoolean(String key, boolean defaultValue) {
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(key);
        }
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value);
    }

    public static int getTotalFloors() { return getInt("TOTAL_FLOORS", 50); }
    public static int getNumElevators() { return getInt("NUM_ELEVATORS", 2); }
    public static int getNumRequests() { return getInt("NUM_REQUESTS", 10); }
    public static int getExternalRequestIntervalMs() { return getInt("EXTERNAL_REQUEST_INTERVAL_MS", 500); }
    public static int getElevatorMoveIntervalMs() { return getInt("ELEVATOR_MOVE_INTERVAL_MS", 100); }
    public static int getInternalRequestChancePercent() { return getInt("INTERNAL_REQUEST_CHANCE_PERCENT", 20); }
    public static int getInternalRequestMinCount() { return getInt("INTERNAL_REQUEST_MIN_COUNT", 1); }
    public static int getInternalRequestMaxCount() { return getInt("INTERNAL_REQUEST_MAX_COUNT", 2); }
    public static boolean getVerboseLogging() { return getBoolean("VERBOSE_LOGGING", true); }
} 