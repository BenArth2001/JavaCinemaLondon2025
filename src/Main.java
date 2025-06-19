import ui.MainApplication;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Start the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new MainApplication();
        });
    }
}