import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


public class Logger {

    // The GUI text box (stays null when running the console-only version)
    private static JTextArea logArea = null;

    public static void setLogArea(JTextArea area) {
        logArea = area;
    }

    // synchronized: two threads can never mix their text together in one line
    public static synchronized void log(String message) {
        String line = "[" + Thread.currentThread().getName() + "] " + message;
        System.out.println(line);

        if (logArea != null) {
            final JTextArea area = logArea;
            // Swing components must only be updated from the Swing thread
            SwingUtilities.invokeLater(() -> {
                area.append(line + "\n");
                area.setCaretPosition(area.getDocument().getLength());
            });
        }
    }
}