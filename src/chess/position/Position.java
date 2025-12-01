package chess.position;

import java.util.Objects;

/**
 * Represents a position on a chessboard using 0-based row and column indices.
 *
 * Row 0 corresponds to rank 8 (top of the board),
 * row 7 corresponds to rank 1 (bottom of the board).
 * Column 0 corresponds to file 'A', column 7 to file 'H'.
 */
public class Position {

    /** Row index (0–7). */
    private int row;

    /** Column index (0–7). */
    private int col;

    /**
     * Creates a Position with the given row and column.
     *
     * @param row row index (0–7)
     * @param col column index (0–7)
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Copy constructor.
     */
    public Position(Position other) {
        this.row = other.row;
        this.col = other.col;
    }

    /** Row getter. */
    public int getRow() {
        return row;
    }

    /** Column getter. */
    public int getCol() {
        return col;
    }

    /** Sets the row index. */
    public void setRow(int row) {
        this.row = row;
    }

    /** Sets the column index. */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Returns a human-friendly string like "A1", "E4", etc.
     */
    @Override
    public String toString() {
        char file = (char) ('A' + col);     // 0 → 'A', 7 → 'H'
        int rank = 8 - row;                 // 0 → 8, 7 → 1
        return "" + file + rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position pos = (Position) o;
        return row == pos.row && col == pos.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    /**
     * Convenience factory: same as new Position(row, col).
     */
    public static Position fromCoords(int row, int col) {
        return new Position(row, col);
    }

    /**
     * NEW: Parses a square like "E2", "a8", "H1" into a Position.
     *
     * E2 → col = 4, row = 6
     * A8 → col = 0, row = 0
     */
    public static Position fromString(String square) {
        if (square == null || square.length() != 2) {
            throw new IllegalArgumentException("Invalid square: " + square);
        }

        char fileChar = Character.toUpperCase(square.charAt(0)); // 'A'..'H'
        char rankChar = square.charAt(1);                        // '1'..'8'

        if (fileChar < 'A' || fileChar > 'H') {
            throw new IllegalArgumentException("Invalid file in square: " + square);
        }
        if (rankChar < '1' || rankChar > '8') {
            throw new IllegalArgumentException("Invalid rank in square: " + square);
        }

        int col = fileChar - 'A';          // A→0 … H→7
        int rank = rankChar - '0';         // '1'→1 … '8'→8
        int row = 8 - rank;                // 8→0 … 1→7

        return new Position(row, col);
    }
}
