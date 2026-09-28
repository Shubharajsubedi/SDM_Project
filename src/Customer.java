import java.util.concurrent.ThreadLocalRandom;

/**
 * One laundry customer = one thread.
 * A customer does three stages in order: wash -> dry -> pay.
 * At every stage the customer must first get a free machine (down()),
 * use it, and then give it back (up()).
 */
public class Customer extends Thread {

    private final WashingMachines washers;
    private final Dryers dryers;
    private final PaymentKiosks kiosks;
    private final Statistics stats;

    public Customer(WashingMachines washers, Dryers dryers, PaymentKiosks kiosks,
                    Statistics stats, int id) {
        this.washers = washers;
        this.dryers = dryers;
        this.kiosks = kiosks;
        this.stats = stats;
        setName("Customer-" + id);
        setDaemon(true); // so stuck customers (kiosk outage) do not stop the program from closing
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        Logger.log("Arrived at the laundry facility.");

        try {
            washClothes();
            dryClothes();
            payAtKiosk();
        } catch (InterruptedException e) {
            Logger.log("Was interrupted.");
            return;
        }

        // Report this customer's total time to the shared statistics
        long totalTime = System.currentTimeMillis() - startTime;
        stats.recordCustomer(totalTime);
        Logger.log("Leaving the facility. Total time: " + totalTime / 1000.0 + " seconds.");
    }

    // ---------- Stage 1: washing (4-6 seconds) ----------
    private void washClothes() throws InterruptedException {
        while (true) {
            Logger.log("Waiting for a washing machine.");
            int machine = washers.down();            // blocks while all 6 machines are busy

            int washTime = ThreadLocalRandom.current().nextInt(4000, 6001);
            Logger.log("Washing clothes in Washer " + machine + " (" + washTime / 1000.0 + "s).");

            // First half of the wash cycle
            Thread.sleep(washTime / 2);

            // 5% chance the washing machine fails MID-cycle
            if (ThreadLocalRandom.current().nextInt(100) < 5) {
                Logger.log("Washer " + machine + " FAILED mid-cycle. Retrying...");
                washers.up(machine);                 // give the broken machine back
                Thread.sleep(1000);                  // customer waits a moment...
                continue;                            // ...then retries from the start
            }

            // Second half of the wash cycle
            Thread.sleep(washTime - washTime / 2);

            Logger.log("Finished washing.");
            washers.up(machine);
            break;
        }
    }

    // ---------- Stage 2: drying (3-5 seconds) ----------
    private void dryClothes() throws InterruptedException {
        Logger.log("Waiting for a dryer.");
        int dryer = dryers.down();                   // blocks while all 4 dryers are busy

        int dryTime = ThreadLocalRandom.current().nextInt(3000, 5001);
        Logger.log("Drying clothes in Dryer " + dryer + " (" + dryTime / 1000.0 + "s).");
        Thread.sleep(dryTime);

        Logger.log("Finished drying.");
        dryers.up(dryer);
    }

    // ---------- Stage 3: payment (1-2 seconds) ----------
    private void payAtKiosk() throws InterruptedException {
        Logger.log("Going to a payment kiosk.");
        int kiosk = kiosks.down();                   // blocks while both kiosks are busy

        while (true) {
            // BONUS: if the kiosks are broken all day, the customer can never pay
            if (kiosks.isBroken()) {
                Logger.log("Cannot pay - Kiosk " + kiosk + " is broken. Retrying in 2 seconds...");
                Thread.sleep(2000);
                continue;
            }

            int payTime = ThreadLocalRandom.current().nextInt(1000, 2001);
            Logger.log("Paying at Kiosk " + kiosk + " (" + payTime / 1000.0 + "s).");
            Thread.sleep(payTime);

            // 5% chance the payment kiosk fails
            if (ThreadLocalRandom.current().nextInt(100) < 5) {
                Logger.log("Payment FAILED. Retrying in 2 seconds...");
                Thread.sleep(2000);
                continue;                            // retry at the same kiosk
            }

            Logger.log("Completed payment.");
            break;
        }

        kiosks.up(kiosk);
    }
}