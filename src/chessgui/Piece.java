package chessgui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * GUI chess piece: only knows its color and type, plus how to draw itself.
 * Images are loaded from:
 *
 *   /chessgui/pieces/<theme>/<color>_<type>.png
 *
 * Example: /chessgui/pieces/default/white_queen.png
 */
public class Piece implements Serializable {

    private static final long serialVersionUID = 1L;

    private final PieceColor color;
    private final PieceType type;

    /**
     * Cache: theme -> ( key -> image )
     * where key = "WHITE_PAWN", "BLACK_KING", etc.
     */
    private static final Map<String, Map<String, BufferedImage>> IMAGE_CACHE = new HashMap<>();

    public Piece(PieceColor color, PieceType type) {
        this.color = color;
        this.type = type;
    }

    public PieceColor getColor() {
        return color;
    }

    public PieceType getType() {
        return type;
    }

    /** Returns a shallow copy (same color & type). */
    public Piece copy() {
        return new Piece(color, type);
    }

    /**
     * Draw this piece on the board.
     *
     * @param g      graphics context
     * @param x      top-left x
     * @param y      top-left y
     * @param size   size of the square (width & height)
     * @param theme  theme name: "default", "ocean", "vibrant", etc.
     */
    public void draw(Graphics2D g, int x, int y, int size, String theme) {
        BufferedImage img = getImageForTheme(theme);
        if (img != null) {
            g.drawImage(img, x, y, size, size, null);
            return;
        }

        // --- Fallback: simple vector piece if image missing ---
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // piece background circle
        g.setColor(color == PieceColor.WHITE ? Color.WHITE : Color.BLACK);
        g.fillOval(x + 4, y + 4, size - 8, size - 8);

        // outline
        g.setColor(Color.DARK_GRAY);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(x + 4, y + 4, size - 8, size - 8);

        // letter in the middle for type
        g.setColor(color == PieceColor.WHITE ? Color.BLACK : Color.WHITE);
        String label = switch (type) {
            case KING -> "K";
            case QUEEN -> "Q";
            case ROOK -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case PAWN -> "P";
        };

        Font old = g.getFont();
        g.setFont(old.deriveFont(Font.BOLD, size * 0.6f));
        FontMetrics fm = g.getFontMetrics();
        int textW = fm.stringWidth(label);
        int textH = fm.getAscent();

        int tx = x + (size - textW) / 2;
        int ty = y + (size + textH) / 2 - 4;
        g.drawString(label, tx, ty);
        g.setFont(old);
    }

    // ======================= Image loading =======================

    private BufferedImage getImageForTheme(String theme) {
        if (theme == null || theme.isBlank()) {
            theme = "default";
        }
        theme = theme.toLowerCase();

        String key = color.name() + "_" + type.name(); // e.g., WHITE_QUEEN

        Map<String, BufferedImage> themeMap =
                IMAGE_CACHE.computeIfAbsent(theme, t -> new HashMap<>());

        if (themeMap.containsKey(key)) {
            return themeMap.get(key); // may be null if earlier failed
        }

        // Map enums to filename parts
        String colorPart = (color == PieceColor.WHITE) ? "white" : "black";
        String typePart;
        switch (type) {
            case KING -> typePart = "king";
            case QUEEN -> typePart = "queen";
            case ROOK -> typePart = "rook";
            case BISHOP -> typePart = "bishop";
            case KNIGHT -> typePart = "knight";
            case PAWN -> typePart = "pawn";
            default -> typePart = type.name().toLowerCase();
        }

        String path = "/chessgui/pieces/" + theme + "/" + colorPart + "_" + typePart + ".png";

        try {
            URL url = Piece.class.getResource(path);
            if (url == null) {
                System.err.println("Piece image not found: " + path);
                themeMap.put(key, null); // remember failure
                return null;
            }
            BufferedImage img = ImageIO.read(url);
            themeMap.put(key, img);
            return img;
        } catch (Exception ex) {
            System.err.println("Error loading piece image: " + path + " -> " + ex);
            themeMap.put(key, null); // remember failure
            return null;
        }
    }

    @Override
    public String toString() {
        return (color == PieceColor.WHITE ? "White " : "Black ") + type;
    }
}
