package chessgui;

import java.io.Serializable;

public enum PieceColor implements Serializable {
    WHITE, BLACK;

    public PieceColor opposite() {
        return this == WHITE ? BLACK : WHITE;
    }

    @Override
    public String toString() {
        return this == WHITE ? "White" : "Black";
    }
}
