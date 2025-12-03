package chessgui;

import javax.swing.*;
import java.net.ServerSocket;
import java.net.Socket;

public class OnlineHost {

    public static void main(String[] args) {

        try {
            String portStr = JOptionPane.showInputDialog(
                    null,
                    "Enter port to host on:",
                    "5000"
            );
            if (portStr == null) return;

            int port = Integer.parseInt(portStr);

            ServerSocket server = new ServerSocket(port);

            JOptionPane.showMessageDialog(
                    null,
                    "Waiting for a player to connect..."
            );

            Socket sock = server.accept();
            NetConnection conn = new NetConnection(sock);

            SwingUtilities.invokeLater(() -> {
                ChessFrameOnline frame = new ChessFrameOnline(conn, PieceColor.WHITE);
                frame.setVisible(true);
            });

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error hosting game: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
