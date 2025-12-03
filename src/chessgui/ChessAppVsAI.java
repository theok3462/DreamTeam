package chessgui;

import javax.swing.SwingUtilities;

public class ChessAppVsAI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessFrame frame = new ChessFrame(true, PieceColor.BLACK);
            frame.setVisible(true);
        });
    }
}
