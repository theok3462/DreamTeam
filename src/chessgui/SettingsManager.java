package chessgui;

import java.awt.*;
import java.io.Serializable;

/**
 * Stores all user-adjustable settings:
 * - Board colors
 * - Square size
 * - Piece theme (default / ocean / vibrant)
 * - Dark UI mode
 * - Reset to theme defaults
 */
public class SettingsManager implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===================== STORED SETTINGS =====================
    private Color lightSquare = new Color(240, 217, 181); // default theme light
    private Color darkSquare  = new Color(181, 136, 99);  // default theme dark

    private int squareSize = 72;

    // piece theme: "default", "ocean", "vibrant"
    private String pieceTheme = "default";

    // NEW: dark UI mode flag
    private boolean darkMode = false;

    public SettingsManager() {}

    // ------------------ LIGHT SQUARE COLOR ---------------------

    /** New API used by some newer code */
    public Color getLightSquare() {
        return lightSquare;
    }

    /** Backwards-compatible alias (your old code used this) */
    public Color getLightColor() {
        return lightSquare;
    }

    public void setLightSquare(Color c) {
        if (c != null) lightSquare = c;
    }

    public void setLightColor(Color c) {
        setLightSquare(c);
    }

    // ------------------ DARK SQUARE COLOR ----------------------

    /** New API used by some newer code */
    public Color getDarkSquare() {
        return darkSquare;
    }

    /** Backwards-compatible alias (your old code used this) */
    public Color getDarkColor() {
        return darkSquare;
    }

    public void setDarkSquare(Color c) {
        if (c != null) darkSquare = c;
    }

    public void setDarkColor(Color c) {
        setDarkSquare(c);
    }

    // ------------------------ SQUARE SIZE ----------------------

    public int getSquareSize() {
        return squareSize;
    }

    public void setSquareSize(int size) {
        if (size >= 40 && size <= 120) {
            squareSize = size;
        }
    }

    // ---------------------- PIECE THEME ------------------------

    /** Main getter used by BoardColorSettingsDialog (old code). */
    public String getPieceTheme() {
        return pieceTheme;
    }

    /** Backwards compatible alias if some code calls getPieceStyle(). */
    public String getPieceStyle() {
        return pieceTheme;
    }

    public void setPieceTheme(String theme) {
        if (theme == null) return;
        pieceTheme = theme.toLowerCase();
    }

    public void setPieceStyle(String theme) {
        setPieceTheme(theme);
    }

    // ------------------------ DARK MODE ------------------------

    /** NEW: whether dark UI mode is enabled. */
    public boolean isDarkMode() {
        return darkMode;
    }

    /** NEW: enable / disable dark UI mode. */
    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    // ===========================================================
    // RESET TO THEME DEFAULTS (used for "Reset" and applyThemeName)
    // ===========================================================
    public void resetToThemeDefaults() {
        String name = pieceTheme;
        if (name == null) name = "default";
        name = name.toLowerCase();

        switch (name) {
            case "ocean" -> {
                // matches BoardColorSettingsDialog.themeColorsFor("ocean")
                lightSquare = new Color(220, 235, 245);
                darkSquare  = new Color(64, 124, 173);
            }
            case "vibrant" -> {
                // matches BoardColorSettingsDialog.themeColorsFor("vibrant")
                lightSquare = new Color(244, 233, 107);
                darkSquare  = new Color(205, 149, 12);
            }
            default -> {
                // matches BoardColorSettingsDialog.themeColorsFor("default")
                lightSquare = new Color(240, 217, 181);
                darkSquare  = new Color(181, 136, 99);
            }
        }
    }

    // ===========================================================
    // applyThemeName: this is what your OLD dialog calls
    // ===========================================================
    /**
     * Used by BoardColorSettingsDialog:
     * - sets the theme name
     * - resets board colors to that theme's defaults.
     */
    public void applyThemeName(String themeName) {
        if (themeName == null || themeName.isEmpty()) {
            themeName = "default";
        }
        setPieceTheme(themeName);
        resetToThemeDefaults();
    }

    // ===========================================================
    // COPY SETTINGS (used when loading saved game)
    // ===========================================================
    public void copyFrom(SettingsManager other) {
        if (other == null) return;

        this.lightSquare = other.lightSquare;
        this.darkSquare  = other.darkSquare;
        this.squareSize  = other.squareSize;
        this.pieceTheme  = other.pieceTheme;
        this.darkMode    = other.darkMode;   // keep dark-mode state too
    }
}
