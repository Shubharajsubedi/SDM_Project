import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Prints every message to the console AND to the GUI log window (if one is open).
 * Every line starts with the name of the thread that printed it, e.g. "[Customer-5] ...",
 * so we can prove that each thread only acts for itself.
 */
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