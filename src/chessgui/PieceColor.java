package chessgui;

import java.io.Serializable;

/**
 * Color of a piece.
 */
public enum PieceColor implements Serializable {
    WHITE, BLACK;

    @Override
    public String toString() {
        return this == WHITE ? "White" : "Black";
    }
}
