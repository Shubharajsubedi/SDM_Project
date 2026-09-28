/**
 * Collects the statistics of the simulation:
 *  - total customers served
 *  - average total time per customer
 * Many customer threads report at the same time, so every method is synchronized
 * to stop two threads corrupting the counters (a race condition).
 */
public class Statistics {

    private int customersServed = 0;
    private long totalTimeMillis = 0;

    // Called by each Customer thread when it leaves the facility
    public synchronized void recordCustomer(long timeTakenMillis) {
        customersServed++;
        totalTimeMillis += timeTakenMillis;
    }

    public synchronized int getCustomersServed() {
        return customersServed;
    }

    public synchronized double getAverageSeconds() {
        if (customersServed == 0) {
            return 0;
        }
        return (totalTimeMillis / 1000.0) / customersServed;
    }

    // Prints the final report (max washers/dryers come from the resource classes)
    public synchronized void printSummary(int maxWashersInUse, int maxDryersInUse) {
        Logger.log("===== SIMULATION STATISTICS =====");
        Logger.log("Total customers served        : " + customersServed);
        Logger.log("Average time per customer      : " + String.format("%.2f", getAverageSeconds()) + " seconds");
        Logger.log("Max concurrent washers in use   : " + maxWashersInUse);
        Logger.log("Max concurrent dryers in use    : " + maxDryersInUse);
        Logger.log("==================================");
    }
}