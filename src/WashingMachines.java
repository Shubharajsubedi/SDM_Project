/**
 * The 6 washing machines (shared resource).
 * Concurrency concept: mutual exclusion + condition synchronisation.
 *  - synchronized  : only one thread can change the machine data at a time
 *  - wait()        : a customer sleeps while all machines are busy
 *  - notifyAll()   : wakes the sleeping customers when a machine is released
 */
public class WashingMachines {

    private boolean[] busy;          // busy[0] is Washer 1, busy[1] is Washer 2, ...
    private int availableSlots;      // how many machines are free right now
    private int inUse = 0;           // how many machines are busy right now
    private int maxInUse = 0;        // statistic: highest number busy at the same time

    public WashingMachines(int totalMachines) {
        busy = new boolean[totalMachines];
        availableSlots = totalMachines;
    }

    // Waits for a free machine, then returns its number (1, 2, 3...)
    public synchronized int down() throws InterruptedException {
        while (availableSlots == 0) {
            wait();                  // all machines busy -> customer waits here
        }

        // find the first free machine
        int machine = 0;
        while (busy[machine]) {
            machine++;
        }

        busy[machine] = true;
        availableSlots--;
        inUse++;
        if (inUse > maxInUse) {
            maxInUse = inUse;
        }

        Logger.log("Took Washer " + (machine + 1) + ". Washers in use: " + inUse);
        return machine + 1;
    }

    // Frees the machine the customer was using and wakes up waiting customers
    public synchronized void up(int machine) {
        busy[machine - 1] = false;
        availableSlots++;
        inUse--;

        Logger.log("Released Washer " + machine + ".");
        notifyAll();
    }

    // The methods below are used by the GUI and the statistics
    public synchronized boolean isBusy(int machine) {
        return busy[machine - 1];
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