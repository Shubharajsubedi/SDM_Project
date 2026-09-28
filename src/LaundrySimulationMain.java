import java.util.concurrent.ThreadLocalRandom;

/**
 * MAIN PROGRAM (console version) - Basic + Additional requirements.
 *
 * ASSUMPTIONS
 *  1. A failed washing machine is released straight away and the customer retries
 *     (possibly with a different machine) after waiting 1 second.
 *  2. A failed payment kiosk is retried by the same customer at the same kiosk
 *     after 2 seconds.
 *  3. Customers are served in no guaranteed order (wait()/notifyAll() is not FIFO).
 *  4. The spec says both "about 60 seconds" and "1-2 minutes". With 50 customers
 *     arriving 0-3 seconds apart, the run takes about 1.5 minutes.
 */
public class LaundrySimulationMain {

    private static final int NUM_WASHERS = 6;
    private static final int NUM_DRYERS = 4;
    private static final int NUM_KIOSKS = 2;
    private static final int NUM_CUSTOMERS = 50;

    public static void main(String[] args) throws InterruptedException {

        // Create the shared resources and the shared statistics
        WashingMachines washers = new WashingMachines(NUM_WASHERS);
        Dryers dryers = new Dryers(NUM_DRYERS);
        PaymentKiosks kiosks = new PaymentKiosks(NUM_KIOSKS);
        Statistics stats = new Statistics();

        Customer[] customers = new Customer[NUM_CUSTOMERS];

        Logger.log("Laundry facility simulation starting...");

        // Start one thread per customer
        for (int i = 1; i <= NUM_CUSTOMERS; i++) {
            customers[i - 1] = new Customer(washers, dryers, kiosks, stats, i);
            customers[i - 1].start();

            // Customers arrive randomly every 0-3 seconds
            int arrivalGap = ThreadLocalRandom.current().nextInt(0, 3001);
            Thread.sleep(arrivalGap);
        }

        // Wait until every customer has finished
        for (Customer c : customers) {
            c.join();
        }

        stats.printSummary(washers.getMaxInUse(), dryers.getMaxInUse());
        Logger.log("Laundry facility simulation finished.");
    }
}