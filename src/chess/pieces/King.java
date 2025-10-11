package chess.pieces;

import chess.position.Position;
import chess.board.Board;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a King piece in chess.
 * Handles standard king moves, castling, and attacking logic.
 */
public class King extends Piece {

    /** True if the king has moved at least once (affects castling) */
    private boolean hasMoved = false;

    /**
     * Constructs a King piece with the specified color and initial position.
     *
     * @param white True for white, false for black
     * @param pos Initial position of the King
     */
    public King(boolean white, Position pos) {
        super(white, pos);
    }

    /**
     * Returns a list of all possible moves for this King.
     * Includes standard one-square moves in any direction and castling if allowed.
     * This method automatically excludes moves that would put the king in check.
     *
     * @param board Current board state
     * @return List of positions the King can move to
     */
    @Override
    public List<Position> possibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int[] dr = {-1, 0, 1};
        int[] dc = {-1, 0, 1};

        // Standard king moves
        for (int r : dr) {
            for (int c : dc) {
                if (r != 0 || c != 0) {
                    Position p = new Position(position.getRow() + r, position.getCol() + c);
                    if (board.isInBounds(p)) {
                        Piece target = board.getPiece(p);
                        if (target == null || target.isWhite() != this.white) {
                            // Only add move if king won't be in check after moving
                            if (!board.wouldBeInCheck(this, p)) {
                                moves.add(p);
                            }
                        }
                    }
                }
            }
        }

        // Castling (king hasn't moved, not currently in check)
        if (!hasMoved && !board.isCheck(white)) {
            if (board.canCastleKingside(white))
                moves.add(new Position(position.getRow(), position.getCol() + 2));
            if (board.canCastleQueenside(white))
                moves.add(new Position(position.getRow(), position.getCol() - 2));
        }

        return moves;
    }

    /**
     * Determines if the King can attack a given position.
     * Used primarily for check detection.
     *
     * @param board Current board state
     * @param pos Position to check
     * @return True if the King can attack the specified position, false otherwise
     */
    @Override
    public boolean canAttackPosition(Board board, Position pos) {
        int rowDiff = Math.abs(position.getRow() - pos.getRow());
        int colDiff = Math.abs(position.getCol() - pos.getCol());
        return (rowDiff <= 1 && colDiff <= 1);
    }

    /**
     * Sets whether the king has moved.
     * Used to manage castling rights.
     *
     * @param moved True if the king has moved
     */
    public void setHasMoved(boolean moved) {
        this.hasMoved = moved;
    }

    /**
     * Returns whether the king has moved at least once.
     *
     * @return True if the king has moved
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * Returns a string representation of the King for board display.
     *
     * @return "wK" for white king, "bK" for black king
     */
    @Override
    public String toString() {
        return white ? "wK" : "bK";
    }
}
