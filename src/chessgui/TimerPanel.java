package chessgui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * A Swing panel that displays and manages timers for both White and Black players.
 * Includes Start, Stop, and Reset controls. The timer increments the active player's
 * time based on the current turn stored in {@link GameState}.
 */
public class TimerPanel extends JPanel {

    private final JLabel whiteLabel = new JLabel("White: 00:00");
    private final JLabel blackLabel = new JLabel("Black: 00:00");

    private final GameState state;
    private final Timer swingTimer;

    private int whiteSec = 0;
    private int blackSec = 0;

    public TimerPanel(GameState state) {
        this.state = state;

        setLayout(new BorderLayout());

        JPanel labels = new JPanel(new GridLayout(1, 2));
        labels.add(whiteLabel);
        labels.add(blackLabel);

        JButton startBtn = new JButton("Start");
        JButton stopBtn  = new JButton("Stop");
        JButton resetBtn = new JButton("Reset");

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controls.add(startBtn);
        controls.add(stopBtn);
        controls.add(resetBtn);

        add(labels, BorderLayout.CENTER);
        add(controls, BorderLayout.EAST);

        whiteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        blackLabel.setHorizontalAlignment(SwingConstants.CENTER);

        swingTimer = new Timer(1000, (ActionEvent e) -> {
            if (state != null && !state.isGameOver()) {
                if (state.getCurrentPlayer() == PieceColor.WHITE) {
                    whiteSec++;
                } else {
                    blackSec++;
                }
                updateLabels();
            }
        });

        startBtn.addActionListener(e -> startTimers());
        stopBtn.addActionListener(e -> stopTimers());
        resetBtn.addActionListener(e -> reset());
    }

    private void updateLabels() {
        whiteLabel.setText("White: " + formatTime(whiteSec));
        blackLabel.setText("Black: " + formatTime(blackSec));
    }

    private String formatTime(int sec) {
        int m = sec / 60;
        int s = sec % 60;
        return String.format("%02d:%02d", m, s);
    }

    /** Starts or resumes the timer. */
    public void startTimers() {
        swingTimer.start();
    }

    /** Stops the timer without resetting the time. */
    public void stopTimers() {
        swingTimer.stop();
    }

    /** Resets both timers to 0:00 and stops counting. */
    public void reset() {
        swingTimer.stop();
        whiteSec = 0;
        blackSec = 0;
        updateLabels();
    }
}
