import java.util.concurrent.ThreadLocalRandom;


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