package chessgui;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog window allowing users to customize board colors and piece style.
 * <p>
 * Users can adjust light/dark square colors, square size, and piece theme.
 * Includes a reset option to restore theme defaults.
 */
public class BoardColorSettingsDialog extends JDialog {
    private final SettingsManager settings;
    private Runnable applyCallback;

    private JComboBox<String> themeBox;
    private JButton lightButton, darkButton, resetButton;
    private JSpinner squareSizeSpinner;

    /**
     * Constructs a dialog for board color and style customization.
     *
     * @param owner    the parent frame
     * @param settings the settings manager for saving user preferences
     */
    public BoardColorSettingsDialog(Frame owner, SettingsManager settings) {
        super(owner, "Board Color Settings", true);
        this.settings = settings;
        setLayout(new BorderLayout());
        setSize(400, 250);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Piece Style
        panel.add(new JLabel("Piece Style:"));
        themeBox = new JComboBox<>(new String[]{"Default", "Vibrant", "Ocean"});
        themeBox.setSelectedItem(capitalize(settings.getPieceStyle()));
        panel.add(themeBox);

        // Light Square
        panel.add(new JLabel("Light Square Color:"));
        lightButton = new JButton("Pick...");
        lightButton.setBackground(settings.getLightColor());
        lightButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Light Square Color", settings.getLightColor());
            if (newColor != null) lightButton.setBackground(newColor);
        });
        panel.add(lightButton);

        // Dark Square
        panel.add(new JLabel("Dark Square Color:"));
        darkButton = new JButton("Pick...");
        darkButton.setBackground(settings.getDarkColor());
        darkButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Dark Square Color", settings.getDarkColor());
            if (newColor != null) darkButton.setBackground(newColor);
        });
        panel.add(darkButton);

        // Square size
        panel.add(new JLabel("Square Size:"));
        squareSizeSpinner = new JSpinner(new SpinnerNumberModel(settings.getSquareSize(), 40, 120, 4));
        panel.add(squareSizeSpinner);

        // Reset button
        panel.add(new JLabel("Reset to Theme Colors:"));
        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetToThemeDefaults());
        panel.add(resetButton);

        add(panel, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton applyBtn = new JButton("Apply");
        JButton cancelBtn = new JButton("Cancel");

        applyBtn.addActionListener(e -> applyChanges());
        cancelBtn.addActionListener(e -> dispose());

        bottom.add(applyBtn);
        bottom.add(cancelBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    /**
     * Capitalizes the first letter of the given string.
     *
     * @param s the input string
     * @return a capitalized version of the input string
     */
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /** Applies user-selected settings and updates the UI immediately. */
    private void applyChanges() {
        settings.setPieceStyle(((String) themeBox.getSelectedItem()).toLowerCase());
        settings.setLightColor(lightButton.getBackground());
        settings.setDarkColor(darkButton.getBackground());
        settings.setSquareSize((Integer) squareSizeSpinner.getValue());

        if (applyCallback != null) applyCallback.run();
        dispose();
    }

    /** Resets light/dark colors to their defaults based on the selected theme. */
    private void resetToThemeDefaults() {
        String selected = ((String) themeBox.getSelectedItem()).toLowerCase();
        switch (selected) {
            case "vibrant":
                settings.setLightColor(new Color(240, 217, 181));
                settings.setDarkColor(new Color(181, 136, 99));
                break;
            case "ocean":
                settings.setLightColor(new Color(220, 235, 245));
                settings.setDarkColor(new Color(64, 124, 173));
                break;
            default:
                settings.setLightColor(new Color(240, 240, 240));
                settings.setDarkColor(new Color(100, 100, 100));
                break;
        }
        lightButton.setBackground(settings.getLightColor());
        darkButton.setBackground(settings.getDarkColor());
    }

    /**
     * Sets a callback function that runs after the user applies changes.
     *
     * @param r a runnable function to execute after applying settings
     */
    public void setApplyCallback(Runnable r) {
        this.applyCallback = r;
    }
}
