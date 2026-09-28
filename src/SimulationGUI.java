import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

// BONUS 2: GUI dashboard that visualises the laundromat.
// It only READS the state of the resources - the simulation logic is unchanged.
public class SimulationGUI extends JFrame {

    private static final int NUM_CUSTOMERS = 50;

    private WashingMachines washers;
    private Dryers dryers;
    private PaymentKiosks kiosks;
    private Statistics stats;

    // Header
    private JLabel modeLabel = new JLabel("NORMAL OPERATION");

    // Dashboard cards
    private JLabel servedLabel = new JLabel("0", SwingConstants.CENTER);
    private JLabel averageLabel = new JLabel("0.00 s", SwingConstants.CENTER);
    private JLabel maxWashersLabel = new JLabel("0", SwingConstants.CENTER);
    private JLabel maxDryersLabel = new JLabel("0", SwingConstants.CENTER);
    private JLabel queueLabel = new JLabel("0", SwingConstants.CENTER);

    // Equipment state labels
    private JLabel[] washerLabels;
    private JLabel[] dryerLabels;
    private JLabel[] kioskLabels;

    // Live activity log
    private JTextArea logArea = new JTextArea();

    // Simulation controls (bottom)
    private JLabel statusLabel = new JLabel("Status: Ready");
    private JProgressBar progressBar = new JProgressBar(0, NUM_CUSTOMERS);
    private JButton startButton = new JButton("Start Simulation");
    private JButton outageButton = new JButton("Simulate Kiosk Outage (Bonus)");

    public SimulationGUI(WashingMachines washers, Dryers dryers,
                         PaymentKiosks kiosks, Statistics stats) {
        super("Smart Laundry Facility - Dashboard");
        this.washers = washers;
        this.dryers = dryers;
        this.kiosks = kiosks;
        this.stats = stats;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLayout(new BorderLayout(10, 10));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        // Everything that is logged also appears in the Live activity box
        Logger.setLogArea(logArea);

        // Refresh the dashboard 5 times per second
        Timer refreshTimer = new Timer(200, e -> refresh());
        refreshTimer.start();

        setLocationRelativeTo(null);
    }

    // ---------------- Building the screen ----------------

    private JPanel buildTopPanel() {
        JPanel top = new JPanel(new BorderLayout(5, 10));
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JLabel title = new JLabel("Smart Laundry Facility - Facility Overview");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        modeLabel.setFont(modeLabel.getFont().deriveFont(Font.BOLD, 14f));

        JPanel header = new JPanel(new BorderLayout());
        header.add(title, BorderLayout.WEST);
        header.add(modeLabel, BorderLayout.EAST);

        JPanel cards = new JPanel(new GridLayout(1, 5, 10, 10));
        cards.add(makeCard("Customers Served", servedLabel));
        cards.add(makeCard("Average Visit", averageLabel));
        cards.add(makeCard("Max Washers In Use", maxWashersLabel));
        cards.add(makeCard("Max Dryers In Use", maxDryersLabel));
        cards.add(makeCard("Payment Queue", queueLabel));

        top.add(header, BorderLayout.NORTH);
        top.add(cards, BorderLayout.CENTER);
        return top;
    }

    private JPanel makeCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createTitledBorder(title));
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 26f));
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildCenterPanel() {
        JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Left: equipment status
        JPanel equipment = new JPanel(new GridLayout(3, 1, 5, 5));
        equipment.setBorder(BorderFactory.createTitledBorder("Equipment status"));

        JPanel washerRow = new JPanel(new GridLayout(1, washers.getTotal(), 5, 5));
        washerRow.setBorder(BorderFactory.createTitledBorder("Washers"));
        washerLabels = makeEquipmentLabels("Washer", washers.getTotal(), washerRow);

        JPanel dryerRow = new JPanel(new GridLayout(1, dryers.getTotal(), 5, 5));
        dryerRow.setBorder(BorderFactory.createTitledBorder("Dryers"));
        dryerLabels = makeEquipmentLabels("Dryer", dryers.getTotal(), dryerRow);

        JPanel kioskRow = new JPanel(new GridLayout(1, kiosks.getTotal(), 5, 5));
        kioskRow.setBorder(BorderFactory.createTitledBorder("Payment kiosks"));
        kioskLabels = makeEquipmentLabels("Kiosk", kiosks.getTotal(), kioskRow);

        equipment.add(washerRow);
        equipment.add(dryerRow);
        equipment.add(kioskRow);

        // Right: live activity log
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Live activity"));

        center.add(equipment);
        center.add(logScroll);
        return center;
    }

    private JLabel[] makeEquipmentLabels(String name, int count, JPanel row) {
        JLabel[] labels = new JLabel[count];
        for (int i = 0; i < count; i++) {
            labels[i] = new JLabel("", SwingConstants.CENTER);
            labels[i].setBorder(BorderFactory.createEtchedBorder());
            showState(labels[i], name, i + 1, "AVAILABLE");
            row.add(labels[i]);
        }
        return labels;
    }

    private JPanel buildBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 10, 10),
                BorderFactory.createTitledBorder("Simulation controls")));

        progressBar.setStringPainted(true);
        progressBar.setString("0 / " + NUM_CUSTOMERS + " customers finished");

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(startButton);
        buttons.add(outageButton);

        startButton.addActionListener(e -> startSimulation());
        outageButton.addActionListener(e -> simulateOutage());

        bottom.add(statusLabel, BorderLayout.WEST);
        bottom.add(progressBar, BorderLayout.CENTER);
        bottom.add(buttons, BorderLayout.EAST);
        return bottom;
    }

    // ---------------- Updating the screen ----------------

    private void showState(JLabel label, String name, int number, String state) {
        label.setText("<html><center>" + name + " " + number + "<br>" + state + "</center></html>");
    }

    private void refresh() {
        servedLabel.setText(String.valueOf(stats.getCustomersServed()));
        averageLabel.setText(String.format("%.2f s", stats.getAverageSeconds()));
        maxWashersLabel.setText(String.valueOf(washers.getMaxInUse()));
        maxDryersLabel.setText(String.valueOf(dryers.getMaxInUse()));
        queueLabel.setText(String.valueOf(kiosks.getWaiting()));

        for (int i = 0; i < washerLabels.length; i++) {
            showState(washerLabels[i], "Washer", i + 1,
                    washers.isBusy(i + 1) ? "BUSY" : "AVAILABLE");
        }
        for (int i = 0; i < dryerLabels.length; i++) {
            showState(dryerLabels[i], "Dryer", i + 1,
                    dryers.isBusy(i + 1) ? "BUSY" : "AVAILABLE");
        }
        for (int i = 0; i < kioskLabels.length; i++) {
            String state = "AVAILABLE";
            if (kiosks.isBroken()) {
                state = "BROKEN";
            } else if (kiosks.isBusy(i + 1)) {
                state = "BUSY";
            }
            showState(kioskLabels[i], "Kiosk", i + 1, state);
        }

        progressBar.setValue(stats.getCustomersServed());
        progressBar.setString(stats.getCustomersServed() + " / " + NUM_CUSTOMERS + " customers finished");

        // BONUS: show the kiosk outage on screen
        if (kiosks.isOwnerCalled()) {
            modeLabel.setText("KIOSK OUTAGE - OWNER CALLED");
            statusLabel.setText("Status: Congestion - owner called in");
        } else if (kiosks.isBroken()) {
            modeLabel.setText("KIOSK OUTAGE");
        } else {
            modeLabel.setText("NORMAL OPERATION");
        }
    }

    // ---------------- Button actions ----------------

    private void startSimulation() {
        startButton.setEnabled(false);
        statusLabel.setText("Status: Simulation running...");

        // The simulation runs in its own thread so the window never freezes
        Thread runner = new Thread(() -> {
            try {
                Customer[] customers = new Customer[NUM_CUSTOMERS];

                for (int i = 1; i <= NUM_CUSTOMERS; i++) {
                    customers[i - 1] = new Customer(washers, dryers, kiosks, stats, i);
                    customers[i - 1].start();

                    // Customers arrive randomly every 0-3 seconds
                    Thread.sleep(ThreadLocalRandom.current().nextInt(0, 3001));
                }

                for (Customer c : customers) {
                    c.join();
                }

                stats.printSummary(washers.getMaxInUse(), dryers.getMaxInUse());
                SwingUtilities.invokeLater(() -> statusLabel.setText("Status: Simulation finished"));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "SimulationRunner");

        runner.setDaemon(true);
        runner.start();
    }

    // BONUS: both payment kiosks break down for the rest of the day
    private void simulateOutage() {
        kiosks.setBroken(true);
        outageButton.setEnabled(false);
    }
}