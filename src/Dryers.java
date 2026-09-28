/**
 * The 4 dryers (shared resource).
 * Works exactly like WashingMachines: synchronized methods with wait()/notifyAll(),
 * so no two customers can ever use the same dryer at the same time.
 */
public class Dryers {

    private boolean[] busy;          // busy[0] is Dryer 1, busy[1] is Dryer 2, ...
    private int availableSlots;      // how many dryers are free right now
    private int inUse = 0;           // how many dryers are busy right now
    private int maxInUse = 0;        // statistic: highest number busy at the same time

    public Dryers(int totalDryers) {
        busy = new boolean[totalDryers];
        availableSlots = totalDryers;
    }

    // Waits for a free dryer, then returns its number (1, 2, 3...)
    public synchronized int down() throws InterruptedException {
        while (availableSlots == 0) {
            wait();                  // all dryers busy -> customer waits here
        }

        // find the first free dryer
        int dryer = 0;
        while (busy[dryer]) {
            dryer++;
        }

        busy[dryer] = true;
        availableSlots--;
        inUse++;
        if (inUse > maxInUse) {
            maxInUse = inUse;
        }

        Logger.log("Took Dryer " + (dryer + 1) + ". Dryers in use: " + inUse);
        return dryer + 1;
    }

    // Frees the dryer the customer was using and wakes up waiting customers
    public synchronized void up(int dryer) {
        busy[dryer - 1] = false;
        availableSlots++;
        inUse--;

        Logger.log("Released Dryer " + dryer + ".");
        notifyAll();
    }

    // The methods below are used by the GUI and the statistics
    public synchronized boolean isBusy(int dryer) {
        return busy[dryer - 1];
    }

    public synchronized int getTotal() {
        return busy.length;
    }

    public synchronized int getCurrentInUse() {
        return inUse;
    }

    public synchronized int getMaxInUse() {
        return maxInUse;
    }
}