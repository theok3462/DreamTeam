package chessgui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetConnection {

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    public NetConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMove(Move m) throws IOException {
        out.println(m.sx + " " + m.sy + " " + m.dx + " " + m.dy);
        if (out.checkError()) {
            throw new IOException("Write error");
        }
    }

    public int[] readMove() throws IOException {
        String line = in.readLine();
        if (line == null) {
            return null;
        }
        String[] parts = line.trim().split("\\s+");
        if (parts.length != 4) {
            throw new IOException("Bad move format: " + line);
        }
        int sx = Integer.parseInt(parts[0]);
        int sy = Integer.parseInt(parts[1]);
        int dx = Integer.parseInt(parts[2]);
        int dy = Integer.parseInt(parts[3]);
        return new int[]{sx, sy, dx, dy};
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
