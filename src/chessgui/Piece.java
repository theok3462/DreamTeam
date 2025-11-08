package chessgui;

import java.io.Serializable;

/**
 * Represents a chess piece (color & type).
 */
public class Piece implements Serializable {
    private static final long serialVersionUID = 1L;

    private final PieceColor color;
    private final PieceType type;

    public Piece(PieceColor color, PieceType type) {
        this.color = color;
        this.type = type;
    }

    public PieceColor getColor() { return color; }
    public PieceType getType() { return type; }

    @Override
    public String toString() {
        return color + "_" + type;
    }
}
