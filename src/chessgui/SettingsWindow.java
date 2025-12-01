package chessgui;

import javax.swing.*;
import java.awt.*;

/**
 * Simple settings dialog that allows changing:
 *  - board light/dark colors
 *  - piece theme ("Classic", "3D")
 *  - square (tile) size
 */
public class SettingsWindow extends JDialog {

    public SettingsWindow(ChessFrame frame) {
        super(frame, "Settings", true);

        SettingsManager settings = frame.getSettings();

        // Piece theme
        JComboBox<String> themeBox = new JComboBox<>(new String[]{"Classic", "3D"});
        themeBox.setSelectedItem(settings.getPieceTheme());

        // Board colors
        JButton lightBtn = new JButton("Light Square Color");
        JButton darkBtn  = new JButton("Dark Square Color");

        lightBtn.setBackground(settings.getLightSquare());
        darkBtn.setBackground(settings.getDarkSquare());

        lightBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Choose Light Square Color", settings.getLightSquare());
            if (chosen != null) {
                settings.setLightSquare(chosen);
                lightBtn.setBackground(chosen);
            }
        });

        darkBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Choose Dark Square Color", settings.getDarkSquare());
            if (chosen != null) {
                settings.setDarkSquare(chosen);
                darkBtn.setBackground(chosen);
            }
        });

        // Square size
        JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(settings.getSquareSize(), 40, 120, 4));

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.add(new JLabel("Piece Theme:"));
        form.add(themeBox);
        form.add(lightBtn);
        form.add(darkBtn);
        form.add(new JLabel("Square Size (px):"));
        form.add(sizeSpinner);

        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");

        ok.addActionListener(e -> {
            settings.setPieceTheme((String) themeBox.getSelectedItem());
            settings.setSquareSize((Integer) sizeSpinner.getValue());
            frame.applySettingsChanged();
            dispose();
        });

        cancel.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(ok);
        buttons.add(cancel);

        getContentPane().setLayout(new BorderLayout(8, 8));
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(frame);
    }
}
