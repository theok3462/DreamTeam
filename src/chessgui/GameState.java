package chessgui;

import java.io.Serializable;
import java.util.Stack;

/**
 * Holds the pieces on the board, current player, and move stack for undo.
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private Piece[][] board = new Piece[8][8];
    public final Stack<Move> moves = new Stack<>();
    private PieceColor currentPlayer = PieceColor.WHITE;

    // 🔹 Added optional theme field to remember which theme was active when saving/loading
    private String currentTheme = "default";

    public GameState() { }

    /**
     * Initialize to classic chess starting positions.
     */
    public void initializeClassic() {
        board = new Piece[8][8];
        // Pawns
        for (int i = 0; i < 8; i++) {
            board[6][i] = new Piece(PieceColor.WHITE, PieceType.PAWN);
            board[1][i] = new Piece(PieceColor.BLACK, PieceType.PAWN);
        }
        // Rooks
        board[7][0] = new Piece(PieceColor.WHITE, PieceType.ROOK);
        board[7][7] = new Piece(PieceColor.WHITE, PieceType.ROOK);
        board[0][0] = new Piece(PieceColor.BLACK, PieceType.ROOK);
        board[0][7] = new Piece(PieceColor.BLACK, PieceType.ROOK);
        // Knights
        board[7][1] = new Piece(PieceColor.WHITE, PieceType.KNIGHT);
        board[7][6] = new Piece(PieceColor.WHITE, PieceType.KNIGHT);
        board[0][1] = new Piece(PieceColor.BLACK, PieceType.KNIGHT);
        board[0][6] = new Piece(PieceColor.BLACK, PieceType.KNIGHT);
        // Bishops
        board[7][2] = new Piece(PieceColor.WHITE, PieceType.BISHOP);
        board[7][5] = new Piece(PieceColor.WHITE, PieceType.BISHOP);
        board[0][2] = new Piece(PieceColor.BLACK, PieceType.BISHOP);
        board[0][5] = new Piece(PieceColor.BLACK, PieceType.BISHOP);
        // Queens
        board[7][3] = new Piece(PieceColor.WHITE, PieceType.QUEEN);
        board[0][3] = new Piece(PieceColor.BLACK, PieceType.QUEEN);
        // Kings
        board[7][4] = new Piece(PieceColor.WHITE, PieceType.KING);
        board[0][4] = new Piece(PieceColor.BLACK, PieceType.KING);

        moves.clear();
        currentPlayer = PieceColor.WHITE;
    }

    public Piece getPieceAt(int r, int c) {
        if (r < 0 || r >= 8 || c < 0 || c >= 8) return null;
        return board[r][c];
    }

    public void setPieceAt(int r, int c, Piece p) {
        if (r < 0 || r >= 8 || c < 0 || c >= 8) return;
        board[r][c] = p;
    }

    public PieceColor getCurrentPlayer() { return currentPlayer; }

    public void toggleCurrentPlayer() {
        currentPlayer = (currentPlayer == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }

    /**
     * Copy state from another GameState instance (used for load).
     */
    public void copyFrom(GameState s) {
        this.board = s.board;
        this.moves.clear();
        this.moves.addAll(s.moves);
        this.currentPlayer = s.currentPlayer;
        this.currentTheme = s.currentTheme; // ✅ preserve theme on copy
    }

    /**
     * Determine if a King's been captured (returns true if any player no longer has king).
     */
    public boolean isKingCaptured() {
        boolean white = false, black = false;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p == null) continue;
                if (p.getType() == PieceType.KING) {
                    if (p.getColor() == PieceColor.WHITE) white = true;
                    if (p.getColor() == PieceColor.BLACK) black = true;
                }
            }
        }
        return !(white && black);
    }

    // 🔹 Theme management helpers (used by HistoryPanel & ChessBoardPanel)
    public String getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(String theme) {
        if (theme != null && !theme.isBlank()) {
            this.currentTheme = theme;
        }
    }
}
