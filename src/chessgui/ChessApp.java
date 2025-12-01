package chessgui;

import javax.swing.SwingUtilities;

public class ChessApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessFrame frame = new ChessFrame();
            frame.setVisible(true);
        });
    }
}
