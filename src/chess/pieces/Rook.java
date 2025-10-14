package chess.pieces;

import chess.position.Position;
import chess.board.Board;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Rook chess piece.
 * Rooks move horizontally or vertically any number of squares.
 */
public class Rook extends Piece {
    private boolean hasMoved = false;

    /**
     * Constructs a Rook.
     *
     * @param white True if the rook is white, false if black.
     * @param pos   The initial position of the rook.
     */
    public Rook(boolean white, Position pos) {
        super(white, pos);
    }

    /**
     * Calculates all possible moves for the rook, ignoring checks.
     * Rooks can move horizontally or vertically until blocked by another piece.
     *
     * @param board The current board state.
     * @return List of valid positions the rook can move to.
     */
    @Override
    public List<Position> possibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int[] dr = {0, 0, 1, -1}; // row directions
        int[] dc = {1, -1, 0, 0}; // column directions

        for (int d = 0; d < 4; d++) {
            int r = position.getRow() + dr[d];
            int c = position.getCol() + dc[d];
            while (board.isInBounds(new Position(r, c))) {
                Position p = new Position(r, c);
                Piece target = board.getPiece(p);
                if (target == null) {
                    // Only add move if rook won't expose king to check
                    if (!board.wouldBeInCheck(this, p)) moves.add(p);
                } else {
                    if (target.isWhite() != this.white) moves.add(p);
                    break;
                }
                r += dr[d];
                c += dc[d];
            }
        }
        return moves;
    }

    /**
     * Determines whether this rook can attack the given position.
     * Used by Board's check detection.
     *
     * @param board The current board state.
     * @param pos   The target position.
     * @return True if the rook can attack the position, false otherwise.
     */
    @Override
    public boolean canAttackPosition(Board board, Position pos) {
        if (position.getRow() == pos.getRow()) {
            int start = Math.min(position.getCol(), pos.getCol()) + 1;
            int end = Math.max(position.getCol(), pos.getCol());
            for (int c = start; c < end; c++) {
                if (board.getPiece(new Position(position.getRow(), c)) != null) return false;
            }
            return true;
        } else if (position.getCol() == pos.getCol()) {
            int start = Math.min(position.getRow(), pos.getRow()) + 1;
            int end = Math.max(position.getRow(), pos.getRow());
            for (int r = start; r < end; r++) {
                if (board.getPiece(new Position(r, position.getCol())) != null) return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Marks the rook as having moved (important for castling).
     *
     * @param moved True if the rook has moved.
     */
    public void setHasMoved(boolean moved) {
        this.hasMoved = moved;
    }

    /**
     * Checks if the rook has moved.
     *
     * @return True if the rook has moved.
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * Returns a string representation of the rook for display.
     *
     * @return "wR" for white rook, "bR" for black rook.
     */
    @Override
    public String toString() {
        return white ? "wR" : "bR";
    }
}
