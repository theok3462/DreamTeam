package chessgui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AIPlayer {

    private final PieceColor color;
    private final Random rand = new Random();

    public AIPlayer(PieceColor color) {
        this.color = color;
    }

    public boolean makeAIMove(GameState state) {
        if (state == null || state.isGameOver()) return false;
        if (state.getCurrentPlayer() != color) return false;

        List<int[]> moves = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = state.getPieceAt(r, c);
                if (p == null || p.getColor() != color) continue;

                for (int r2 = 0; r2 < 8; r2++) {
                    for (int c2 = 0; c2 < 8; c2++) {
                        if (r == r2 && c == c2) continue;

                        if (state.makeMove(r, c, r2, c2)) {
                            moves.add(new int[]{r, c, r2, c2});
                            state.undoLastMove();
                        }
                    }
                }
            }
        }

        if (moves.isEmpty()) return false;

        int[] m = moves.get(rand.nextInt(moves.size()));
        state.makeMove(m[0], m[1], m[2], m[3]);
        return true;
    }
}
