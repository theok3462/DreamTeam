package chessgui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Backend logic for the GUI chess game.
 * Holds board state, validates moves, tracks history, and detects
 * check / checkmate / stalemate.
 */
public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Piece[][] board = new Piece[8][8];
    private PieceColor currentPlayer = PieceColor.WHITE;
    private final Stack<Move> history = new Stack<>();

    // public alias so chess.game.Game can access moves directly (existing feature)
    public final Stack<Move> moves = history;

    private boolean gameOver = false;
    private PieceColor winner = null; // null => draw (e.g., stalemate)

    // ======================== THEME SUPPORT ========================
    // Used by SettingsWindow / BoardColorSettingsDialog to remember which piece theme is selected.
    private String currentTheme = "default";

    public GameState() {
        initializeClassic();
    }

    /** Standard chess starting position. */
    public final void initializeClassic() {
        clearBoard();
        currentPlayer = PieceColor.WHITE;
        history.clear();
        gameOver = false;
        winner = null;

        // Back rank order
        PieceType[] back = {
                PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP,
                PieceType.QUEEN, PieceType.KING,
                PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };

        // White pieces (bottom, rows 6 & 7)
        for (int c = 0; c < 8; c++) {
            board[6][c] = new Piece(PieceColor.WHITE, PieceType.PAWN);
            board[7][c] = new Piece(PieceColor.WHITE, back[c]);
        }

        // Black pieces (top, rows 0 & 1)
        for (int c = 0; c < 8; c++) {
            board[1][c] = new Piece(PieceColor.BLACK, PieceType.PAWN);
            board[0][c] = new Piece(PieceColor.BLACK, back[c]);
        }
    }

    private void clearBoard() {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = null;
    }

    // ================== BASIC ACCESSORS / INTEROP ==================

    public Piece getPiece(int row, int col) {
        if (!inBounds(row, col)) return null;
        return board[row][col];
    }

    /**
     * Phase-1 / Game interoperability helper:
     * set a piece directly at (row, col). Row/col are 0..7.
     */
    public void setPieceAt(int row, int col, Piece piece) {
        if (!inBounds(row, col)) return;
        board[row][col] = piece;
    }

    /**
     * Phase-1 / Game interoperability helper:
     * get a piece directly at (row, col). Row/col are 0..7.
     */
    public Piece getPieceAt(int row, int col) {
        if (!inBounds(row, col)) return null;
        return board[row][col];
    }

    public PieceColor getCurrentPlayer() {
        return currentPlayer;
    }

    /** allow Game.java to set the current player explicitly. */
    public void setCurrentPlayer(PieceColor color) {
        if (color != null) {
            this.currentPlayer = color;
        }
    }

    /** allow Game.java to just flip the turn. */
    public void toggleCurrentPlayer() {
        this.currentPlayer = this.currentPlayer.opposite();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public PieceColor getWinner() {
        return winner;
    }

    public List<Move> getHistory() {
        return new ArrayList<>(history);
    }

    private static boolean inBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }

    // ======================== MOVE LOGIC ========================

    /**
     * Original makeMove used by phase-1 console code and older GUI.
     * Auto-promotes pawns to a queen.
     */
    public boolean makeMove(int sr, int sc, int dr, int dc) {
        return makeMove(sr, sc, dr, dc, null); // null => auto-queen
    }

    /**
     * New overload used by the GUI: allows choosing the promotion piece.
     * If promoteTo is null, falls back to auto-queen (original behaviour).
     */
    public boolean makeMove(int sr, int sc, int dr, int dc, PieceType promoteTo) {
        if (gameOver) return false;
        if (!inBounds(sr, sc) || !inBounds(dr, dc)) return false;
        if (sr == dr && sc == dc) return false;

        Piece moving = board[sr][sc];
        if (moving == null || moving.getColor() != currentPlayer) return false;

        // Destination cannot contain own piece
        Piece target = board[dr][dc];
        if (target != null && target.getColor() == currentPlayer) return false;

        // Pseudo-legal?
        if (!isPseudoLegalMove(moving, sr, sc, dr, dc)) return false;

        // Does this move leave own king in check?
        if (wouldLeaveKingInCheck(sr, sc, dr, dc)) return false;

        // ===== Perform the move (no special moves like castling/en passant) =====
        board[sr][sc] = null;

        Piece piecePlaced = moving; // what we actually put on the destination square

        // Promotion
        if (moving.getType() == PieceType.PAWN &&
                ((dr == 0 && moving.getColor() == PieceColor.WHITE) ||
                        (dr == 7 && moving.getColor() == PieceColor.BLACK))) {

            PieceType typeToUse = (promoteTo != null) ? promoteTo : PieceType.QUEEN;
            // we create a new promoted piece on the board...
            piecePlaced = new Piece(moving.getColor(), typeToUse);
        }

        // but we keep the ORIGINAL piece (moving) in the Move object,
        // so undoLastMove restores a pawn instead of a queen.
        board[dr][dc] = piecePlaced;

        Move mv = new Move(sr, sc, dr, dc, moving, target, currentPlayer);
        history.push(mv);

        // Toggle player
        currentPlayer = currentPlayer.opposite();

        // After the move, check if the SIDE TO MOVE is checkmated or stalemated.
        if (isCheckmate(currentPlayer)) {
            gameOver = true;
            winner = currentPlayer.opposite();
        } else if (isStalemate(currentPlayer)) {
            gameOver = true;
            winner = null; // draw
        }

        return true;
    }

    /** Undo the last move, if any. */
    public boolean undoLastMove() {
        if (history.isEmpty()) return false;

        Move mv = history.pop();

        // Restore board: mv.moved is the ORIGINAL piece (e.g., the pawn)
        board[mv.sx][mv.sy] = mv.moved;
        board[mv.dx][mv.dy] = mv.captured;

        // Switch turn back
        currentPlayer = mv.player;

        // Reset game-over flags
        gameOver = false;
        winner = null;
        return true;
    }

    // ========== Check / Checkmate / Stalemate ==========

    public boolean isCheck(PieceColor color) {
        int[] kingPos = findKing(color);
        if (kingPos == null) return false; // no king (corrupted board)
        return isSquareAttacked(kingPos[0], kingPos[1], color.opposite());
    }

    public boolean isCheckmate(PieceColor color) {
        if (!isCheck(color)) return false;
        return !hasAnyLegalMove(color);
    }

    /** Stalemate = not in check, but no legal move. */
    public boolean isStalemate(PieceColor color) {
        if (isCheck(color)) return false;
        return !hasAnyLegalMove(color);
    }

    /** Does this side have at least one legal move? */
    private boolean hasAnyLegalMove(PieceColor color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p == null || p.getColor() != color) continue;

                List<int[]> moves = pseudoLegalMovesFor(p, r, c);
                for (int[] mv : moves) {
                    int dr = mv[0], dc = mv[1];
                    if (!leavesKingInCheckForColor(r, c, dr, dc, color)) {
                        return true; // found at least one legal move
                    }
                }
            }
        }
        return false;
    }

    // ========== Internal helpers ==========

    /** Wrapper that keeps old signature for existing calls. */
    private boolean wouldLeaveKingInCheck(int sr, int sc, int dr, int dc) {
        return leavesKingInCheckForColor(sr, sc, dr, dc, currentPlayer);
    }

    /** Core helper that checks king safety for an arbitrary color. */
    private boolean leavesKingInCheckForColor(int sr, int sc, int dr, int dc,
                                              PieceColor colorWhoseKing) {
        Piece moving = board[sr][sc];
        Piece captured = board[dr][dc];

        // Make temp move
        board[sr][sc] = null;
        board[dr][dc] = moving;

        boolean inCheck = isCheck(colorWhoseKing);

        // Undo temp move
        board[sr][sc] = moving;
        board[dr][dc] = captured;

        return inCheck;
    }

    private int[] findKing(PieceColor color) {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && p.getColor() == color && p.getType() == PieceType.KING) {
                    return new int[]{r, c};
                }
            }
        return null;
    }

    /** Is the square (r,c) attacked by any piece of attackerColor? */
    private boolean isSquareAttacked(int r, int c, PieceColor attackerColor) {
        for (int rr = 0; rr < 8; rr++) {
            for (int cc = 0; cc < 8; cc++) {
                Piece p = board[rr][cc];
                if (p == null || p.getColor() != attackerColor) continue;
                if (isPseudoLegalAttack(p, rr, cc, r, c)) {
                    return true;
                }
            }
        }
        return false;
    }

    // ===== Pseudo-legal movement (ignores own-king safety) =====

    private boolean isPseudoLegalMove(Piece p, int sr, int sc, int dr, int dc) {
        // We already checked destination doesn't contain own piece.
        List<int[]> moves = pseudoLegalMovesFor(p, sr, sc);
        for (int[] mv : moves) {
            if (mv[0] == dr && mv[1] == dc) return true;
        }
        return false;
    }

    private boolean isPseudoLegalAttack(Piece p, int sr, int sc, int dr, int dc) {
        // Similar to move but allowed to "attack" king square even if occupied.
        // We reuse the same logic, but for pawns we must treat captures differently.
        PieceType t = p.getType();
        PieceColor color = p.getColor();
        int rDiff = dr - sr;
        int cDiff = dc - sc;
        int absR = Math.abs(rDiff);
        int absC = Math.abs(cDiff);

        switch (t) {
            case PAWN -> {
                int dir = (color == PieceColor.WHITE) ? -1 : 1;
                return rDiff == dir && Math.abs(cDiff) == 1;
            }
            case KNIGHT -> {
                return (absR == 2 && absC == 1) || (absR == 1 && absC == 2);
            }
            case BISHOP -> {
                if (absR != absC || absR == 0) return false;
                return pathClear(sr, sc, dr, dc);
            }
            case ROOK -> {
                if (sr != dr && sc != dc) return false;
                if (sr == dr && sc == dc) return false;
                return pathClear(sr, sc, dr, dc);
            }
            case QUEEN -> {
                if (sr == dr || sc == dc || absR == absC) {
                    if (sr == dr && sc == dc) return false;
                    return pathClear(sr, sc, dr, dc);
                }
                return false;
            }
            case KING -> {
                return absR <= 1 && absC <= 1 && (absR + absC > 0);
            }
        }
        return false;
    }

    private List<int[]> pseudoLegalMovesFor(Piece p, int sr, int sc) {
        List<int[]> list = new ArrayList<>();
        PieceType t = p.getType();
        PieceColor color = p.getColor();

        switch (t) {
            case PAWN -> {
                int dir = (color == PieceColor.WHITE) ? -1 : 1;
                int startRow = (color == PieceColor.WHITE) ? 6 : 1;

                // forward 1
                int fr = sr + dir;
                if (inBounds(fr, sc) && board[fr][sc] == null) {
                    list.add(new int[]{fr, sc});
                    // forward 2 from starting rank
                    int fr2 = sr + 2 * dir;
                    if (sr == startRow && inBounds(fr2, sc) && board[fr2][sc] == null) {
                        list.add(new int[]{fr2, sc});
                    }
                }
                // captures
                int[] cols = {sc - 1, sc + 1};
                for (int cc : cols) {
                    int rr = sr + dir;
                    if (inBounds(rr, cc)) {
                        Piece target = board[rr][cc];
                        if (target != null && target.getColor() != color) {
                            list.add(new int[]{rr, cc});
                        }
                    }
                }
            }
            case KNIGHT -> {
                int[][] deltas = {
                        {-2, -1}, {-2, +1}, {-1, -2}, {-1, +2},
                        {+1, -2}, {+1, +2}, {+2, -1}, {+2, +1}
                };
                for (int[] d : deltas) {
                    int rr = sr + d[0], cc = sc + d[1];
                    if (!inBounds(rr, cc)) continue;
                    Piece target = board[rr][cc];
                    if (target == null || target.getColor() != color) {
                        list.add(new int[]{rr, cc});
                    }
                }
            }
            case BISHOP -> addSlidingMoves(list, sr, sc, color,
                    new int[][]{{-1, -1}, {-1, +1}, {+1, -1}, {+1, +1}});
            case ROOK -> addSlidingMoves(list, sr, sc, color,
                    new int[][]{{-1, 0}, {+1, 0}, {0, -1}, {0, +1}});
            case QUEEN -> {
                addSlidingMoves(list, sr, sc, color,
                        new int[][]{{-1, -1}, {-1, +1}, {+1, -1}, {+1, +1},
                                {-1, 0}, {+1, 0}, {0, -1}, {0, +1}});
            }
            case KING -> {
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) continue;
                        int rr = sr + dr, cc = sc + dc;
                        if (!inBounds(rr, cc)) continue;
                        Piece target = board[rr][cc];
                        if (target == null || target.getColor() != color) {
                            list.add(new int[]{rr, cc});
                        }
                    }
                }
                // (Castling omitted for simplicity)
            }
        }

        return list;
    }

    private void addSlidingMoves(List<int[]> list, int sr, int sc,
                                 PieceColor color, int[][] directions) {
        for (int[] d : directions) {
            int rr = sr + d[0];
            int cc = sc + d[1];
            while (inBounds(rr, cc)) {
                Piece target = board[rr][cc];
                if (target == null) {
                    list.add(new int[]{rr, cc});
                } else {
                    if (target.getColor() != color) {
                        list.add(new int[]{rr, cc});
                    }
                    break; // blocked
                }
                rr += d[0];
                cc += d[1];
            }
        }
    }

    private boolean pathClear(int sr, int sc, int dr, int dc) {
        int rStep = Integer.compare(dr, sr);
        int cStep = Integer.compare(dc, sc);

        int r = sr + rStep;
        int c = sc + cStep;
        while (r != dr || c != dc) {
            if (!inBounds(r, c)) return false;
            if (board[r][c] != null) return false;
            r += rStep;
            c += cStep;
        }
        return true;
    }

    /** Reset whole game. */
    public void reset() {
        initializeClassic();
    }

    // ======================== THEME METHODS ========================

    /**
     * Updates the current theme/style name used for piece sets.
     * Called from SettingsWindow / ChessFrame.
     */
    public void setCurrentTheme(String theme) {
        if (theme == null) {
            this.currentTheme = "default";
        } else {
            this.currentTheme = theme.toLowerCase();
        }
    }

    public String getCurrentTheme() {
        return currentTheme;
    }
}
