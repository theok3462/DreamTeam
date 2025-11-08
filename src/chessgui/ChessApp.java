package chessgui;

import javax.swing.SwingUtilities;

/**
 * Main entry point for the Chess GUI application.
 */
public class ChessApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessFrame frame = new ChessFrame();
            frame.setVisible(true);
        });
    }
}

