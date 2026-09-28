import java.util.concurrent.ThreadLocalRandom;

/**
 * BONUS 1 (console version): CONGESTED SCENARIO.
 * Both payment kiosks are broken for the whole day, so customers pile up in the
 * payment queue. The owner is called in once 30 customers are waiting.
 */
public class CongestedScenarioMain {

    private static final int NUM_CUSTOMERS = 50;
    private static final int OBSERVATION_SECONDS = 60;

    public static void main(String[] args) throws InterruptedException {

        WashingMachines washers = new WashingMachines(6);
        Dryers dryers = new Dryers(4);
        PaymentKiosks kiosks = new PaymentKiosks(2);
        Statistics stats = new Statistics();

        // BONUS: both kiosks are broken for the whole day
        kiosks.setBroken(true);

        Logger.log("BONUS: Congested scenario starting...");

        for (int i = 1; i <= NUM_CUSTOMERS; i++) {
            new Customer(washers, dryers, kiosks, stats, i).start();
            Thread.sleep(ThreadLocalRandom.current().nextInt(0, 3001));
        }

        // Customers can never pay, so we just watch the queue for a while
        Thread.sleep(OBSERVATION_SECONDS * 1000L);

        Logger.log("BONUS: Customers still stuck at the payment queue: " + kiosks.getWaiting());
        Logger.log("BONUS: Owner called in: " + kiosks.isOwnerCalled());
    }
}