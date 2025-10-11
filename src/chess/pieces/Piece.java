package chess.pieces;

import chess.position.Position;
import chess.board.Board;
import java.util.List;

/**
 * Represents a generic chess piece.
 * All specific chess pieces (Pawn, King, Queen, Rook, Bishop, Knight) extend this class.
 * Each piece has a color (white or black) and a current position on the board.
 * Subclasses must implement their unique movement rules via possibleMoves() and canAttackPosition().
 */
public abstract class Piece {

    /** True if the piece is white, false if black */
    protected boolean white;

    /** Current position of the piece on the board */
    protected Position position;

    /**
     * Constructs a Piece with the specified color and initial position.
     * @param white True for white, false for black
     * @param position Initial position of the piece on the board
     */
    public Piece(boolean white, Position position) {
        this.white = white;
        this.position = position;
    }

    /**
     * Returns whether the piece is white.
     * @return True if the piece is white, false if black
     */
    public boolean isWhite() {
        return white;
    }

    /**
     * Returns the current position of the piece.
     * @return Current Position object
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets a new position for the piece.
     * @param position New Position object to set
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Returns a list of all possible moves this piece can make on the given board.
     * This method ignores checks; the board may be in a state that makes some moves invalid.
     * Subclasses must implement this method according to their unique movement rules.
     *
     * @param board Current state of the chessboard
     * @return List of Position objects representing possible moves
     */
    public abstract List<Position> possibleMoves(Board board);

    /**
     * Returns true if this piece can attack the given position.
     * Subclasses must implement this method for consistent check/checkmate detection.
     * @param board Current state of the board
     * @param pos Position to check
     * @return True if this piece can attack the given position, false otherwise
     */
    public abstract boolean canAttackPosition(Board board, Position pos);
}
