
public class PaymentKiosks {

    private boolean[] busy;              // busy[0] is Kiosk 1, busy[1] is Kiosk 2
    private int availableSlots;          // how many kiosks are free right now
    private int waiting = 0;             // customers waiting in the payment queue

    // BONUS: congested scenario
    private boolean broken = false;      // true = both kiosks are broken all day
    private boolean ownerCalled = false; // true once 30 customers are stuck in the queue

    public PaymentKiosks(int totalKiosks) {
        busy = new boolean[totalKiosks];
        availableSlots = totalKiosks;
    }

    // Joins the queue, waits for a free kiosk, then returns its number (1, 2)
    public synchronized int down() throws InterruptedException {
        waiting++;
        Logger.log("Joined the payment queue. Customers waiting: " + waiting);
        checkOwnerCall();

        while (availableSlots == 0) {
            wait();                  // both kiosks busy -> customer waits in the queue
        }
        waiting--;

        // find the first free kiosk
        int kiosk = 0;
        while (busy[kiosk]) {
            kiosk++;
        }

        busy[kiosk] = true;
        availableSlots--;

        Logger.log("Took Kiosk " + (kiosk + 1) + ".");
        return kiosk + 1;
    }

    // Frees the kiosk the customer was using and wakes up waiting customers
    public synchronized void up(int kiosk) {
        busy[kiosk - 1] = false;
        availableSlots++;

        Logger.log("Released Kiosk " + kiosk + ".");
        notifyAll();
    }

    // ---------------- BONUS: congested scenario ----------------

    // BONUS: breaks (or fixes) both kiosks for the day
    public synchronized void setBroken(boolean value) {
        broken = value;
        if (broken) {
            Logger.log("BONUS: Both payment kiosks have broken down for the day!");
            checkOwnerCall();
        }
    }

    // BONUS: the owner is called only ONCE, when 30 customers are stuck in the queue.
    // It is synchronized (called from synchronized methods), so two threads
    // can never both trigger the alert.
    private void checkOwnerCall() {
        if (broken && waiting >= 30 && !ownerCalled) {
            ownerCalled = true;
            Logger.log("!!! BONUS ALERT: 30 customers are stuck at the payment queue. "
                    + "The owner has been called in! !!!");
        }
    }

    public synchronized boolean isBroken() {
        return broken;
    }

    public synchronized boolean isOwnerCalled() {
        return ownerCalled;
    }

    // ------------------------------------------------------------

    // The methods below are used by the GUI
    public synchronized boolean isBusy(int kiosk) {
        return busy[kiosk - 1];
    }

    public synchronized int getTotal() {
        return busy.length;
    }

    public synchronized int getWaiting() {
        return waiting;
    }
}