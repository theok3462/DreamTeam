package chessgui;

import javax.swing.*;
import java.awt.*;

/**
 * A settings dialog allowing the user to change board color themes, piece styles, and square size.
 */
public class SettingsWindow extends JDialog {
    private final SettingsManager settings;
    private final ChessFrame frame;

    private JComboBox<String> themeCombo;
    private JComboBox<String> pieceStyleCombo;
    private JSlider sizeSlider;

    /**
     * Constructor (called from MenuBar)
     */
    public SettingsWindow(ChessFrame frame, SettingsManager settings) {
        super(frame, "Settings", true);
        this.settings = settings;
        this.frame = frame;
        initUI();
    }

    /**
     * Initializes the settings window UI.
     */
    private void initUI() {
        setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // === BOARD COLOR THEME ===
        panel.add(new JLabel("Board Color Theme:"));
        String[] themeNames = new String[settings.getThemeCount()];
        for (int i = 0; i < settings.getThemeCount(); i++) {
            themeNames[i] = SettingsManager.getThemeName(i);
        }
        themeCombo = new JComboBox<>(themeNames);
        themeCombo.setSelectedIndex(settings.getBoardColorIndex());
        panel.add(themeCombo);

        // === PIECE STYLE ===
        panel.add(new JLabel("Piece Style:"));
        // ✅ Use styles from SettingsManager directly
        pieceStyleCombo = new JComboBox<>(settings.getPieceStyles());
        pieceStyleCombo.setSelectedItem(settings.getPieceStyle());
        panel.add(pieceStyleCombo);

        // === SQUARE SIZE ===
        panel.add(new JLabel("Square Size:"));
        sizeSlider = new JSlider(40, 120, settings.getSquareSize());
        sizeSlider.setMajorTickSpacing(20);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        panel.add(sizeSlider);

        add(panel, BorderLayout.CENTER);

        // === BUTTONS ===
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyBtn = new JButton("Apply");
        JButton cancelBtn = new JButton("Cancel");

        buttons.add(applyBtn);
        buttons.add(cancelBtn);
        add(buttons, BorderLayout.SOUTH);

        // === Apply button logic ===
        applyBtn.addActionListener(e -> {
            applySettings();
            setVisible(false);
        });

        cancelBtn.addActionListener(e -> setVisible(false));

        pack();
        setLocationRelativeTo(frame);
    }

    /**
     * Apply changes to settings and refresh the board.
     */
    private void applySettings() {
        settings.setBoardColorIndex(themeCombo.getSelectedIndex());
        settings.setPieceStyle((String) pieceStyleCombo.getSelectedItem());
        settings.setSquareSize(sizeSlider.getValue());
        frame.getBoardPanel().reload(); // refresh the board visuals
    }
}
