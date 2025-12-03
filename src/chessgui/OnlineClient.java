package chessgui;

import javax.swing.*;
import java.net.Socket;

public class OnlineClient {

    public static void main(String[] args) {

        try {
            String ip = JOptionPane.showInputDialog(
                    null,
                    "Enter host IP address:",
                    "127.0.0.1"
            );
            if (ip == null) return;

            String portStr = JOptionPane.showInputDialog(
                    null,
                    "Enter host port:",
                    "5000"
            );
            if (portStr == null) return;

            int port = Integer.parseInt(portStr);

            Socket sock = new Socket(ip, port);
            NetConnection conn = new NetConnection(sock);

            SwingUtilities.invokeLater(() -> {
                ChessFrameOnline frame = new ChessFrameOnline(conn, PieceColor.BLACK);
                frame.setVisible(true);
            });

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error connecting to host: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
