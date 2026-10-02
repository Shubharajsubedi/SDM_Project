import javax.swing.SwingUtilities;


 // BONUS 2 ENTRY POINT: run this file to open the GUI dashboard.

public class LaundrySimulationGUIMain {

    public static void main(String[] args) {

        WashingMachines washers = new WashingMachines(6);
        Dryers dryers = new Dryers(4);
        PaymentKiosks kiosks = new PaymentKiosks(2);
        Statistics stats = new Statistics();

        // Swing windows must be created on the Swing thread
        SwingUtilities.invokeLater(() -> {
            SimulationGUI gui = new SimulationGUI(washers, dryers, kiosks, stats);
            gui.setVisible(true);
        });
    }
}