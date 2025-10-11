package chess.pieces;

import chess.position.Position;
import chess.board.Board;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Queen chess piece.
 * The queen can move any number of squares vertically, horizontally, or diagonally.
 */
public class Queen extends Piece {

    /**
     * Constructs a Queen piece.
     *
     * @param white True if the queen is white, false if black.
     * @param pos   The initial position of the queen.
     */
    public Queen(boolean white, Position pos) {
        super(white, pos);
    }

    /**
     * Calculates all possible moves for the queen, ignoring checks.
     * Combines rook-like and bishop-like moves.
     *
     * @param board The current board state.
     * @return List of valid positions the queen can move to.
     */
    @Override
    public List<Position> possibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        // Combine Rook-like moves
        Rook tempRook = new Rook(white, position);
        for (Position p : tempRook.possibleMoves(board)) {
            if (!board.wouldBeInCheck(this, p)) moves.add(p);
        }

        // Combine Bishop-like moves
        Bishop tempBishop = new Bishop(white, position);
        for (Position p : tempBishop.possibleMoves(board)) {
            if (!board.wouldBeInCheck(this, p)) moves.add(p);
        }

        return moves;
    }

    /**
     * Determines whether this queen can attack the given position.
     * Used by Board's check detection.
     *
     * @param board The current board state.
     * @param pos   The target position.
     * @return True if the queen can attack the position, false otherwise.
     */
    @Override
    public boolean canAttackPosition(Board board, Position pos) {
        Rook tempRook = new Rook(white, position);
        if (tempRook.canAttackPosition(board, pos)) return true;

        Bishop tempBishop = new Bishop(white, position);
        return tempBishop.canAttackPosition(board, pos);
    }

    /**
     * Returns a string representation of the queen for display.
     *
     * @return "wQ" for white queen, "bQ" for black queen.
     */
    @Override
    public String toString() {
        return white ? "wQ" : "bQ";
    }
}
