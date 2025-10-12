package chess.pieces;

import chess.position.Position;
import chess.board.Board;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Pawn chess piece.
 * Pawns move forward one square (or two from starting row) and capture diagonally.
 */
public class Pawn extends Piece {

    /**
     * Constructs a Pawn.
     *
     * @param white True if the pawn is white, false if black.
     * @param pos   The initial position of the pawn.
     */
    public Pawn(boolean white, Position pos) {
        super(white, pos);
    }

    /**
     * Calculates all possible moves for this pawn, ignoring checks.
     * Includes one-step forward, two-step forward from starting row, and diagonal captures.
     *
     * @param board The current board state.
     * @return List of valid positions this pawn can move to.
     */
    @Override
    public List<Position> possibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int dir = white ? -1 : 1;       // White moves up (-1), Black moves down (+1)
        int startRow = white ? 6 : 1;   // Starting row for first move

        // Move forward 1 square
        Position oneStep = new Position(position.getRow() + dir, position.getCol());
        if (board.isInBounds(oneStep) && board.getPiece(oneStep) == null) {
            moves.add(oneStep);

            // Move forward 2 squares from starting row
            Position twoStep = new Position(position.getRow() + 2 * dir, position.getCol());
            if (position.getRow() == startRow && board.isInBounds(twoStep) && board.getPiece(twoStep) == null) {
                moves.add(twoStep);
            }
        }

        // Diagonal captures
        int[] diagCols = {position.getCol() - 1, position.getCol() + 1};
        for (int c : diagCols) {
            Position diag = new Position(position.getRow() + dir, c);
            if (board.isInBounds(diag)) {
                Piece target = board.getPiece(diag);
                if (target != null && target.isWhite() != this.white) {
                    moves.add(diag);
                }
            }
        }

        return moves;
    }

    /**
     * Determines whether this pawn can attack the given position.
     * Pawns attack diagonally, not forward.
     *
     * @param board The current board state.
     * @param pos   The target position.
     * @return True if the pawn can attack the position.
     */
    @Override
    public boolean canAttackPosition(Board board, Position pos) {
        int dir = white ? -1 : 1;
        int[] diagCols = {position.getCol() - 1, position.getCol() + 1};
        for (int c : diagCols) {
            Position diag = new Position(position.getRow() + dir, c);
            if (diag.equals(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a string representation of the pawn for display.
     *
     * @return "wp" for white pawn, "bp" for black pawn.
     */
    @Override
    public String toString() {
        return white ? "wp" : "bp";
    }
}
