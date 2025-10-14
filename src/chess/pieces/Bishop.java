package chess.pieces;

import chess.position.Position;
import chess.board.Board;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Bishop chess piece.
 * Bishops move diagonally any number of squares.
 */
public class Bishop extends Piece {

    /**
     * Constructs a Bishop piece.
     *
     * @param white True if the bishop is white, false if black.
     * @param pos   The initial position of the bishop.
     */
    public Bishop(boolean white, Position pos) {
        super(white, pos);
    }

    /**
     * Calculates all possible moves for the bishop, ignoring checks.
     * Bishops move diagonally until blocked by another piece.
     *
     * @param board The current board state.
     * @return List of valid positions the bishop can move to.
     */
    @Override
    public List<Position> possibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int[] dr = {1, 1, -1, -1};
        int[] dc = {1, -1, 1, -1};

        for (int d = 0; d < 4; d++) {
            int r = position.getRow() + dr[d];
            int c = position.getCol() + dc[d];
            while (board.isInBounds(new Position(r, c))) {
                Position p = new Position(r, c);
                Piece target = board.getPiece(p);

                if (target == null) {
                    if (!board.wouldBeInCheck(this, p)) moves.add(p);
                } else {
                    if (target.isWhite() != this.white) {
                        if (!board.wouldBeInCheck(this, p)) moves.add(p);
                    }
                    break;
                }

                r += dr[d];
                c += dc[d];
            }
        }
        return moves;
    }

    /**
     * Determines whether this bishop can attack the given position.
     * Used by Board's check detection.
     *
     * @param board The current board state.
     * @param pos   The target position.
     * @return True if the bishop can attack the position, false otherwise.
     */
    @Override
    public boolean canAttackPosition(Board board, Position pos) {
        int rowDiff = pos.getRow() - position.getRow();
        int colDiff = pos.getCol() - position.getCol();

        if (Math.abs(rowDiff) != Math.abs(colDiff)) return false;

        int stepRow = rowDiff > 0 ? 1 : -1;
        int stepCol = colDiff > 0 ? 1 : -1;

        int r = position.getRow() + stepRow;
        int c = position.getCol() + stepCol;

        while (r != pos.getRow() && c != pos.getCol()) {
            if (board.getPiece(new Position(r, c)) != null) return false;
            r += stepRow;
            c += stepCol;
        }

        return true;
    }

    /**
     * Returns a string representation of the bishop for display.
     *
     * @return "wB" for white bishop, "bB" for black bishop.
     */
    @Override
    public String toString() {
        return white ? "wB" : "bB";
    }
}
