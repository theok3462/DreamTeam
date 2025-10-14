package chess.utils;

import chess.position.Position;
import java.util.List;

/**
 * Utility class providing common helper methods for chess operations.
 * Includes methods for validating positions, parsing chess notation,
 * and checking if a position exists in a list of positions.
 */
public class Utils {

    /**
     * Checks if the given position is within the bounds of an 8x8 chessboard.
     *
     * @param pos The Position object to check.
     * @return True if the position is valid on the board, false otherwise.
     */
    public static boolean isValidPosition(Position pos) {
        if (pos == null) return false;
        return pos.getRow() >= 0 && pos.getRow() < 8 && pos.getCol() >= 0 && pos.getCol() < 8;
    }

    /**
     * Converts standard chess notation (e.g., "E2") to a Position object.
     * Returns null if the notation is invalid.
     *
     * @param notation The chess notation string.
     * @return Corresponding Position object, or null if the notation is invalid.
     */
    public static Position parsePosition(String notation) {
        if (notation == null || notation.length() != 2) return null;

        notation = notation.toUpperCase().trim();
        char file = notation.charAt(0);   // Column (A-H)
        char rankChar = notation.charAt(1); // Row (1-8)

        if (file < 'A' || file > 'H') return null;
        if (rankChar < '1' || rankChar > '8') return null;

        int row = 8 - (rankChar - '0'); // Convert rank to 0-based row
        int col = file - 'A';           // Convert file to 0-based column

        return new Position(row, col);
    }

    /**
     * Checks if a given position exists in a list of positions.
     *
     * @param positions The list of Position objects.
     * @param pos       The Position to check.
     * @return True if the list contains the position, false otherwise.
     */
    public static boolean containsPosition(List<Position> positions, Position pos) {
        if (positions == null || pos == null) return false;
        for (Position p : positions) {
            if (p.equals(pos)) return true;
        }
        return false;
    }
}
