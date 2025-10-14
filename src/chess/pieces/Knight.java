package chess.pieces;

import chess.position.Position;
import chess.board.Board;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Knight chess piece.
 * Knights move in an L-shape: two squares in one direction and one square perpendicular.
 */
public class Knight extends Piece {

    /**
     * Constructs a Knight piece.
     *
     * @param white True if the knight is white, false if black.
     * @param pos   The initial position of the knight.
     */
    public Knight(boolean white, Position pos) {
        super(white, pos);
    }

    /**
     * Calculates all possible moves for the knight, ignoring checks.
     * Knights can jump over other pieces in L-shaped patterns.
     *
     * @param board The current board state.
     * @return List of valid positions the knight can move to.
     */
    @Override
    public List<Position> possibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int[] dr = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] dc = {-1, 1, -2, 2, -2, 2, -1, 1};

        for (int i = 0; i < 8; i++) {
            Position p = new Position(position.getRow() + dr[i], position.getCol() + dc[i]);
            if (board.isInBounds(p)) {
                Piece target = board.getPiece(p);
                if (target == null || target.isWhite() != this.white) {
                    // Only add move if knight won't expose king to check
                    if (!board.wouldBeInCheck(this, p)) moves.add(p);
                }
            }
        }
        return moves;
    }

    /**
     * Determines whether this knight can attack the given position.
     * Used by Board's check detection.
     *
     * @param board The current board state.
     * @param pos   The target position.
     * @return True if the knight can attack the position, false otherwise.
     */
    @Override
    public boolean canAttackPosition(Board board, Position pos) {
        int rowDiff = Math.abs(position.getRow() - pos.getRow());
        int colDiff = Math.abs(position.getCol() - pos.getCol());
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }

    /**
     * Returns a string representation of the knight for display.
     *
     * @return "wN" for white knight, "bN" for black knight.
     */
    @Override
    public String toString() {
        return white ? "wN" : "bN";
    }
}
