package com.martinatanasov.uicomponents.toast;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Toast {

    public static final int TOAST_WIDTH = 350;
    public static final int TOAST_HEIGHT = 64;
    public static final int TOAST_SPACING = 10;
    public static final int DISPLAY_DURATION = 3500;
    public static final int MARGIN = 30;
    public static final int MAX_LINES = 3;
    public static final int MAX_TOAST_COUNT = 3;
    public static final int MAX_CHARS_PER_LINE = 52;
    public static final ToastPosition DEFAULT_TOAST_POSITION = ToastPosition.TOP_RIGHT;
    private final List<JWindow> activeToasts = new ArrayList<>();
    // Track the currently registered frame and its listener to avoid duplicates
    private Frame trackedFrame = null;
    private ComponentAdapter frameComponentListener = null;
    private ToastPosition trackedPosition = null;

    public void showToast(String message, Frame frame, ToastType type, ToastPosition position) {
        SwingUtilities.invokeLater(() -> {
            // Enforce maximum toast count - remove the oldest toasts immediately
            while (activeToasts.size() >= MAX_TOAST_COUNT) {
                JWindow oldestToast = activeToasts.removeFirst();
                oldestToast.setVisible(false);
                oldestToast.dispose();
            }

            // Register frame movement/resize tracking (only once per frame)
            registerFrameTracking(frame, position);

            JWindow toast = createToast(message, frame, type, position);
            //Add to list first
            activeToasts.add(toast);
            //Position the new toast
            positionToast(toast, frame, position);
            //Make visible
            toast.setVisible(true);
            animateToastsToPosition(frame, position);

            Timer dismissTimer = new Timer(DISPLAY_DURATION, e -> fadeOutAndClose(toast, frame, position));
            dismissTimer.setRepeats(false);
            dismissTimer.start();
        });
    }

    /**
     * Registers a ComponentListener on the frame so toasts snap immediately
     * when the frame is moved or resized. Cleans up any previously registered
     * listener so we never stack duplicate listeners.
     */
    private void registerFrameTracking(Frame frame, ToastPosition position) {
        // Remove old listener if the frame or position changed
        if (trackedFrame != null && (trackedFrame != frame || trackedPosition != position)) {
            trackedFrame.removeComponentListener(frameComponentListener);
            trackedFrame = null;
            frameComponentListener = null;
            trackedPosition = null;
        }

        if (trackedFrame == null) {
            frameComponentListener = new ComponentAdapter() {
                @Override
                public void componentMoved(ComponentEvent e) {
                    snapAllToastsToPosition(frame, position);
                }

                @Override
                public void componentResized(ComponentEvent e) {
                    snapAllToastsToPosition(frame, position);
                }
            };
            frame.addComponentListener(frameComponentListener);
            trackedFrame = frame;
            trackedPosition = position;
        }
    }

    /**
     * Instantly moves all toasts to their correct positions — no animation.
     * Used when the frame itself moves so toasts never lag behind.
     */
    private void snapAllToastsToPosition(Frame frame, ToastPosition position) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < activeToasts.size(); i++) {
                Point target = calculateTargetPosition(frame, position, i);
                activeToasts.get(i).setLocation(target);
            }
        });
    }

    private JWindow createToast(String message, Frame frame, ToastType type, ToastPosition position) {
        JWindow toast = new JWindow(frame);
        toast.setSize(TOAST_WIDTH, TOAST_HEIGHT);
        toast.setOpacity(0.95f);

        JPanel panel = getAnimatedJPanel(type);

        //Icon using FlatLaf SVG
        JLabel iconLabel = createIconLabel(type);
        panel.add(iconLabel, BorderLayout.WEST);

        //Message label
        String wrappedMessage = wrapText(message);
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + wrappedMessage + "</div></html>");
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setVerticalAlignment(SwingConstants.CENTER);
        panel.add(messageLabel, BorderLayout.CENTER);

        //Close button
        JLabel closeButton = createCloseButton(toast, frame, position);
        panel.add(closeButton, BorderLayout.EAST);

        toast.add(panel);
        return toast;
    }

    private static @NonNull JPanel getAnimatedJPanel(ToastType type) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(type.getColor());
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        return panel;
    }

    private JLabel createIconLabel(ToastType type) {
        JLabel iconLabel = new JLabel();
        try {
            // Try to load FlatLaf built-in icons
            FlatSVGIcon icon = new FlatSVGIcon(type.getIconPath(), 28, 28);
            icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.WHITE));
            iconLabel.setIcon(icon);
        } catch (Exception e) {
            log.error("No toast icon found: {}", e.getMessage());
        }
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        return iconLabel;
    }

    private JLabel createCloseButton(JWindow toast, Frame frame, ToastPosition position) {
        JLabel closeButton = new JLabel("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 24));
        closeButton.setForeground(new Color(255, 255, 255, 200));
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                fadeOutAndClose(toast, frame, position);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                closeButton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                closeButton.setForeground(new Color(255, 255, 255, 200));
            }
        });

        return closeButton;
    }

    private void positionToast(JWindow toast, Frame frame, ToastPosition position) {
        int index = activeToasts.indexOf(toast);
        Point target = calculateTargetPosition(frame, position, index);
        toast.setLocation(target);
    }

    private void animateToastsToPosition(Frame frame, ToastPosition position) {
        for (int i = 0; i < activeToasts.size(); i++) {
            JWindow toast = activeToasts.get(i);
            Point targetPosition = calculateTargetPosition(frame, position, i);
            animateToastToPosition(toast, targetPosition);
        }
    }

    private Point calculateTargetPosition(Frame frame, ToastPosition position, int index) {
        Rectangle frameBounds = frame.getBounds();
        Point frameLocation = frame.getLocationOnScreen();

        int offset = index * (TOAST_HEIGHT + TOAST_SPACING);
        int x = 0, y = 0;

        switch (position) {
            case TOP_LEFT:
                x = frameLocation.x + MARGIN;
                y = frameLocation.y + MARGIN + offset;
                break;
            case TOP_RIGHT:
                x = frameLocation.x + frameBounds.width - TOAST_WIDTH - MARGIN;
                y = frameLocation.y + MARGIN + offset;
                break;
            case BOTTOM_LEFT:
                x = frameLocation.x + MARGIN;
                y = frameLocation.y + frameBounds.height - TOAST_HEIGHT - MARGIN - offset;
                break;
            case BOTTOM_RIGHT:
                x = frameLocation.x + frameBounds.width - TOAST_WIDTH - MARGIN;
                y = frameLocation.y + frameBounds.height - TOAST_HEIGHT - MARGIN - offset;
                break;
            case CENTER:
                x = frameLocation.x + (frameBounds.width - TOAST_WIDTH) / 2;
                y = frameLocation.y + (frameBounds.height - TOAST_HEIGHT) / 2 +
                        (index * (TOAST_HEIGHT + TOAST_SPACING)) -
                        ((activeToasts.size() - 1) * (TOAST_HEIGHT + TOAST_SPACING) / 2);
                break;
        }

        return new Point(x, y);
    }

    private void animateToastToPosition(JWindow toast, Point targetPosition) {
        Point currentPosition = toast.getLocation();
        //If already at target, don't animate
        if (currentPosition.equals(targetPosition)) {
            return;
        }

        Timer animationTimer = new Timer(10, null);
        final int ANIMATION_STEPS = 15;
        final int[] step = {0};

        animationTimer.addActionListener(e -> {
            step[0]++;
            if (step[0] >= ANIMATION_STEPS) {
                toast.setLocation(targetPosition);
                animationTimer.stop();
            } else {
                double progress = (double) step[0] / ANIMATION_STEPS;
                double easedProgress = 1 - Math.pow(1 - progress, 3);
                int newX = (int) (currentPosition.x + (targetPosition.x - currentPosition.x) * easedProgress);
                int newY = (int) (currentPosition.y + (targetPosition.y - currentPosition.y) * easedProgress);
                toast.setLocation(newX, newY);
            }
        });

        animationTimer.start();
    }

    private void fadeOutAndClose(JWindow toast, Frame frame, ToastPosition position) {
        Timer fadeTimer = new Timer(30, null);
        fadeTimer.addActionListener(e -> {
            float opacity = toast.getOpacity();
            opacity -= 0.1f;

            if (opacity <= 0) {
                fadeTimer.stop();
                toast.dispose();
                activeToasts.remove(toast);
                // Clean up the frame listener if no toasts remain
                if (activeToasts.isEmpty() && trackedFrame != null) {
                    trackedFrame.removeComponentListener(frameComponentListener);
                    trackedFrame = null;
                    frameComponentListener = null;
                    trackedPosition = null;
                }
                animateToastsToPosition(frame, position);
            } else {
                toast.setOpacity(opacity);
            }
        });
        fadeTimer.start();
    }

    private String wrapText(String text) {
        if (text.length() <= MAX_CHARS_PER_LINE) {
            return text;
        }

        StringBuilder wrapped = new StringBuilder();
        String[] words = text.split(" ");
        int currentLineLength = 0;
        int lineCount = 0;

        for (String word : words) {
            if (lineCount >= MAX_LINES) {
                wrapped.append("...");
                break;
            }
            if (currentLineLength + word.length() + 1 > 52) {
                wrapped.append("<br>");
                currentLineLength = 0;
                lineCount++;
            }
            if (currentLineLength > 0) {
                wrapped.append(" ");
                currentLineLength++;
            }
            wrapped.append(word);
            currentLineLength += word.length();
        }
        return wrapped.toString();
    }

    public void showSuccessToast(String message, Frame frame) {
        showToast(message, frame, ToastType.SUCCESS, DEFAULT_TOAST_POSITION);
    }

    public void showSuccessToast(String message, Frame frame, ToastPosition position) {
        showToast(message, frame, ToastType.SUCCESS, position);
    }

    public void showErrorToast(String message, Frame frame) {
        showToast(message, frame, ToastType.ERROR, DEFAULT_TOAST_POSITION);
    }

    public void showErrorToast(String message, Frame frame, ToastPosition position) {
        showToast(message, frame, ToastType.ERROR, position);
    }

    public void showInfoToast(String message, Frame frame) {
        showToast(message, frame, ToastType.INFO, DEFAULT_TOAST_POSITION);
    }

    public void showInfoToast(String message, Frame frame, ToastPosition position) {
        showToast(message, frame, ToastType.INFO, position);
    }

    public void showWarningToast(String message, Frame frame) {
        showToast(message, frame, ToastType.WARNING, DEFAULT_TOAST_POSITION);
    }

    public void showWarningToast(String message, Frame frame, ToastPosition position) {
        showToast(message, frame, ToastType.WARNING, position);
    }

}