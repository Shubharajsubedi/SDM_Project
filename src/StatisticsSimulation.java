public class StatisticsSimulation {

    public static void main(String[] args) {

        // Create the statistics object
        Statistics statistics = new Statistics();

        // These represent the maximum number of machines
        // that were used at the same time during simulation
        int maxWashersInUse = 6;
        int maxDryersInUse = 4;

        // Simulate 50 customers
        for (int i = 1; i <= 50; i++) {

            // Example customer time
            // In your real program, this comes from the Customer thread
            long timeTaken = 11280;

            // Record this customer
            statistics.recordCustomer(timeTaken);
        }

        // Print final statistics
        statistics.printSummary(
                maxWashersInUse,
                maxDryersInUse
        );

        Logger.log("Laundry facility simulation finished.");
    }
}