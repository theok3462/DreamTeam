package chessgui;

import java.io.Serializable;

/**
 * Represents a move for history and undo support.
 */
public class Move implements Serializable {
    private static final long serialVersionUID = 1L;

    public final int sx, sy, dx, dy;
    public final Piece moved;
    public final Piece captured;
    public final PieceColor player;

    public Move(int sx, int sy, int dx, int dy, Piece moved, Piece captured, PieceColor player) {
        this.sx = sx; this.sy = sy; this.dx = dx; this.dy = dy;
        this.moved = moved; this.captured = captured; this.player = player;
    }

    @Override
    public String toString() {
        char sc = (char)('a' + sy);
        int sr = 8 - sx;
        char dc = (char)('a' + dy);
        int dr = 8 - dx;
        return String.format("%s%d -> %s%d %s%s", sc, sr, dc, dr, moved.getType(), (captured != null ? " x":""));
    }
}
