package chess.position;

import java.util.Objects;

/**
 * Represents a position on a chessboard using 0-based row and column indices.
 * Row 0 corresponds to the top (rank 8), and row 7 corresponds to the bottom (rank 1).
 * Column 0 corresponds to file 'A', and column 7 corresponds to file 'H'.
 */
public class Position {

    /** Row index (0-7) */
    private int row;

    /** Column index (0-7) */
    private int col;

    /**
     * Creates a Position object with specified row and column indices.
     *
     * @param row The row index (0-7)
     * @param col The column index (0-7)
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns the row index.
     *
     * @return row index (0-7)
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column index.
     *
     * @return column index (0-7)
     */
    public int getCol() {
        return col;
    }

    /**
     * Sets the row index.
     *
     * @param row Row index (0-7)
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Sets the column index.
     *
     * @param col Column index (0-7)
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Checks if the position is within the 8x8 chessboard bounds.
     *
     * @return true if the position is valid on the board, false otherwise
     */
    public boolean isInBounds() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * Converts a string in chess notation (e.g., "E2") to a Position object.
     * Returns null if the input is invalid.
     *
     * @param notation Chess notation string
     * @return Corresponding Position object or null if invalid
     */
    public static Position fromString(String notation) {
        if (notation == null || notation.length() != 2) return null;

        char file = Character.toUpperCase(notation.charAt(0));
        char rank = notation.charAt(1);

        if (file < 'A' || file > 'H' || rank < '1' || rank > '8') return null;

        int col = file - 'A';
        int row = 8 - (rank - '0');

        return new Position(row, col);
    }

    /**
     * Converts this Position object to standard chess notation (e.g., "E2").
     *
     * @return String representation in chess notation
     */
    @Override
    public String toString() {
        char file = (char) ('A' + col);
        char rank = (char) ('0' + (8 - row));
        return "" + file + rank;
    }

    /**
     * Checks equality based on row and column indices.
     *
     * @param o Object to compare
     * @return true if both positions have the same row and column
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position pos = (Position) o;
        return row == pos.row && col == pos.col;
    }

    /**
     * Generates a hash code consistent with equals().
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
