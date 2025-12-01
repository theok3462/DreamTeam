package chessgui;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog window allowing users to customize board colors, piece size,
 * piece theme (default / ocean / vibrant), and dark mode.
 *
 * Changes are only applied to {@link SettingsManager} when the user
 * presses the "Apply" button.
 */
public class BoardColorSettingsDialog extends JDialog {
    private final SettingsManager settings;
    private Runnable applyCallback;

    private JComboBox<String> themeBox;
    private JButton lightButton, darkButton;
    private JSpinner squareSizeSpinner;
    private JCheckBox darkModeCheckBox;   // dark mode toggle

    public BoardColorSettingsDialog(Frame owner, SettingsManager settings) {
        super(owner, "Board & Piece Settings", true);
        this.settings = settings;

        setLayout(new BorderLayout());
        setSize(420, 300);
        setLocationRelativeTo(owner);

        // 6 rows now because we added Dark Mode
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Theme combo box
        panel.add(new JLabel("Theme (Board & Pieces):"));
        themeBox = new JComboBox<>(new String[]{"default", "ocean", "vibrant"});
        themeBox.setSelectedItem(settings.getPieceTheme());
        panel.add(themeBox);

        // Light square color
        panel.add(new JLabel("Light Square Color:"));
        lightButton = new JButton("Pick...");
        lightButton.setBackground(settings.getLightColor());
        lightButton.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(
                    this,
                    "Choose Light Square Color",
                    lightButton.getBackground()
            );
            if (chosen != null) {
                lightButton.setBackground(chosen);
            }
        });
        panel.add(lightButton);

        // Dark square color
        panel.add(new JLabel("Dark Square Color:"));
        darkButton = new JButton("Pick...");
        darkButton.setBackground(settings.getDarkColor());
        darkButton.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(
                    this,
                    "Choose Dark Square Color",
                    darkButton.getBackground()
            );
            if (chosen != null) {
                darkButton.setBackground(chosen);
            }
        });
        panel.add(darkButton);

        // Piece size (inside each square)
        panel.add(new JLabel("Piece Size (inside square):"));
        squareSizeSpinner = new JSpinner(
                new SpinnerNumberModel(settings.getSquareSize(), 40, 120, 4));
        panel.add(squareSizeSpinner);

        // Dark mode toggle
        panel.add(new JLabel("Dark Mode (UI):"));
        darkModeCheckBox = new JCheckBox("Enable dark mode");
        darkModeCheckBox.setSelected(settings.isDarkMode());
        panel.add(darkModeCheckBox);

        // Reset
        panel.add(new JLabel("Reset to Original Theme:"));
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetToThemeDefaults());
        panel.add(resetButton);

        add(panel, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyBtn = new JButton("Apply");
        JButton cancelBtn = new JButton("Cancel");

        applyBtn.addActionListener(e -> applyChanges());
        cancelBtn.addActionListener(e -> dispose());

        bottom.add(applyBtn);
        bottom.add(cancelBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    /** Applies the current selections to SettingsManager and notifies the frame. */
    private void applyChanges() {
        String themeName = ((String) themeBox.getSelectedItem());
        // This updates both board + piece theme inside SettingsManager
        settings.applyThemeName(themeName);
        settings.setLightColor(lightButton.getBackground());
        settings.setDarkColor(darkButton.getBackground());
        // Interpreted by ChessBoardPanel as PIECE size
        settings.setSquareSize((Integer) squareSizeSpinner.getValue());
        // dark mode flag
        settings.setDarkMode(darkModeCheckBox.isSelected());

        if (applyCallback != null) {
            applyCallback.run();
        }
        dispose();
    }

    /**
     * Reset theme, colors AND size to the ORIGINAL look of the program:
     * - theme: "default"
     * - board colors: default wood colors
     * - piece size: 64
     * - dark mode: off
     *
     * The actual write to SettingsManager happens when the user clicks "Apply".
     */
    private void resetToThemeDefaults() {
        // Original theme
        themeBox.setSelectedItem("default");

        // Original "default" theme colors
        Color[] colors = themeColorsFor("default");
        lightButton.setBackground(colors[0]);
        darkButton.setBackground(colors[1]);

        // Original default piece size
        squareSizeSpinner.setValue(64);

        // Turn off dark mode when resetting
        darkModeCheckBox.setSelected(false);
    }

    private Color[] themeColorsFor(String name) {
        if (name == null) name = "default";
        name = name.toLowerCase();
        switch (name) {
            case "ocean" -> {
                return new Color[]{
                        new Color(220, 235, 245),
                        new Color(64, 124, 173)
                };
            }
            case "vibrant" -> {
                return new Color[]{
                        new Color(244, 233, 107),
                        new Color(205, 149, 12)
                };
            }
            default -> { // "default"
                return new Color[]{
                        new Color(240, 217, 181),
                        new Color(181, 136, 99)
                };
            }
        }
    }

    /** Sets a callback that runs after Apply (used by ChessFrame). */
    public void setApplyCallback(Runnable r) {
        this.applyCallback = r;
    }
}
