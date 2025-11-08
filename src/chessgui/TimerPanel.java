package chessgui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * A Swing panel that displays and manages timers for both White and Black players.
 * <p>
 * Includes Start, Stop, and Reset controls. The timer increments the active player's
 * time based on the current turn stored in {@link GameState}.
 */
public class TimerPanel extends JPanel {
    private final JLabel whiteLabel = new JLabel("White: 00:00");
    private final JLabel blackLabel = new JLabel("Black: 00:00");
    private final GameState state;
    private final Timer swingTimer;
    private int whiteSec = 0, blackSec = 0;

    /**
     * Constructs a timer panel for the given game state.
     *
     * @param state the shared game state containing the current player information
     */
    public TimerPanel(GameState state) {
        this.state = state;
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 6));
        add(whiteLabel);
        add(blackLabel);

        JButton start = new JButton("Start");
        JButton stop = new JButton("Stop");
        JButton reset = new JButton("Reset");
        add(start);
        add(stop);
        add(reset);

        swingTimer = new Timer(1000, (ActionEvent e) -> {
            if (state.getCurrentPlayer() == PieceColor.WHITE) whiteSec++;
            else blackSec++;
            updateLabels();
        });

        start.addActionListener(e -> swingTimer.start());
        stop.addActionListener(e -> swingTimer.stop());
        reset.addActionListener(e -> {
            swingTimer.stop();
            whiteSec = blackSec = 0;
            updateLabels();
        });
        updateLabels();
    }

    /** Updates both timer labels to reflect the current elapsed time. */
    private void updateLabels() {
        whiteLabel.setText("White: " + format(whiteSec));
        blackLabel.setText("Black: " + format(blackSec));
    }

    /**
     * Formats seconds into MM:SS format.
     *
     * @param sec total seconds
     * @return formatted time string in minutes:seconds
     */
    private String format(int sec) {
        int m = sec / 60;
        int s = sec % 60;
        return String.format("%02d:%02d", m, s);
    }

    /** Stops both player timers. */
    public void stopTimers() {
        swingTimer.stop();
    }

    /** Resets both timers to 0:00 and stops counting. */
    public void reset() {
        swingTimer.stop();
        whiteSec = blackSec = 0;
        updateLabels();
    }
}
