package chessgui;

import java.awt.*;
import java.io.Serializable;

/**
 * Manages user preferences for board colors, styles, and sizes.
 * Fully supports multiple color themes, piece styles, and board sizes.
 * Supports both preset and fully custom light/dark color overrides.
 */
public class SettingsManager implements Serializable {
    private static final long serialVersionUID = 1L;

    // === Extended color themes (light & dark) ===
    private static final Color[][] THEMES = {
            {new Color(240, 217, 181), new Color(181, 136, 99)},   // Classic (Wood)
            {new Color(250, 235, 215), new Color(139, 69, 19)},    // Deep Wood
            {new Color(240, 240, 255), new Color(100, 100, 180)},  // Blue
            {new Color(220, 255, 220), new Color(100, 150, 100)},  // Green
            {new Color(255, 255, 255), new Color(50, 50, 50)},     // Modern Gray
            {new Color(255, 250, 240), new Color(210, 180, 140)},  // Tan
            {new Color(255, 255, 204), new Color(153, 102, 0)},    // Gold
            {new Color(235, 245, 255), new Color(100, 130, 180)},  // Sky Blue
            {new Color(255, 235, 240), new Color(160, 60, 90)},    // Rose
            {new Color(240, 255, 230), new Color(90, 150, 90)}     // Soft Green
    };

    // === Piece Styles ===
    private static final String[] PIECE_STYLES = {
            "default",   // Standard black/white
            "vibrant",   // Red & green
            "ocean"      // Blue & yellow
    };

    // === Default settings ===
    private int boardColorIndex = 0;
    private String pieceStyle = "default";
    private int squareSize = 80;

    // === Optional custom color overrides ===
    private Color customLightColor = null;
    private Color customDarkColor = null;

    public SettingsManager() {}

    // === Color Getters ===
    public Color getLightColor() {
        return (customLightColor != null) ? customLightColor : THEMES[boardColorIndex][0];
    }

    public Color getDarkColor() {
        return (customDarkColor != null) ? customDarkColor : THEMES[boardColorIndex][1];
    }

    // === Color Setters ===
    public void setLightColor(Color c) {
        if (c != null) this.customLightColor = c;
    }

    public void setDarkColor(Color c) {
        if (c != null) this.customDarkColor = c;
    }

    // ✅ Aliases for compatibility
    public void setCustomLightColor(Color c) { setLightColor(c); }
    public void setCustomDarkColor(Color c) { setDarkColor(c); }

    public void resetCustomColors() {
        this.customLightColor = null;
        this.customDarkColor = null;
    }

    // === Theme selection ===
    public int getBoardColorIndex() {
        return boardColorIndex;
    }

    public void setBoardColorIndex(int idx) {
        if (idx >= 0 && idx < THEMES.length) {
            this.boardColorIndex = idx;
        }
    }

    // === Piece style ===
    public String getPieceStyle() {
        return pieceStyle;
    }

    public void setPieceStyle(String style) {
        if (style != null && !style.isEmpty()) {
            style = style.toLowerCase();
            for (String valid : PIECE_STYLES) {
                if (valid.equals(style)) {
                    this.pieceStyle = style;
                    return;
                }
            }
        }
    }

    /** ✅ NEW: Instance method for SettingsWindow (fixes your error) */
    public String[] getPieceStyles() {
        return PIECE_STYLES.clone();
    }

    /** Static helper for other parts of the program */
    public static String[] getAvailablePieceStyles() {
        return PIECE_STYLES.clone();
    }

    // === Board size ===
    public int getSquareSize() {
        return squareSize;
    }

    public void setSquareSize(int size) {
        if (size >= 40 && size <= 120) {
            this.squareSize = size;
        }
    }

    public void setBoardSizePreset(String preset) {
        switch (preset.toLowerCase()) {
            case "small":
                this.squareSize = 60;
                break;
            case "medium":
                this.squareSize = 80;
                break;
            case "large":
                this.squareSize = 100;
                break;
            default:
                // no change
        }
    }

    // === Theme info for UI ===
    public int getThemeCount() {
        return THEMES.length;
    }

    public static String getThemeName(int idx) {
        switch (idx) {
            case 0: return "Classic Wood";
            case 1: return "Deep Wood";
            case 2: return "Blue";
            case 3: return "Green";
            case 4: return "Modern Gray";
            case 5: return "Tan";
            case 6: return "Gold";
            case 7: return "Sky Blue";
            case 8: return "Rose";
            case 9: return "Soft Green";
            default: return "Custom";
        }
    }

    // === Copy from another instance ===
    public void copyFrom(SettingsManager other) {
        if (other == null) return;
        this.boardColorIndex = other.boardColorIndex;
        this.pieceStyle = other.pieceStyle;
        this.squareSize = other.squareSize;
        this.customLightColor = other.customLightColor;
        this.customDarkColor = other.customDarkColor;
    }
}
