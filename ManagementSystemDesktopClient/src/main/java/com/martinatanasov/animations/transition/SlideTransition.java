package com.martinatanasov.animations.transition;

import javax.swing.*;
import java.awt.*;

public class SlideTransition implements PanelTransition {

    private final int directionX;
    private final int directionY;

    public SlideTransition(int directionX, int directionY) {
        this.directionX = directionX;
        this.directionY = directionY;
    }

    @Override
    public void animate(JPanel root, JPanel oldPanel, JPanel newPanel, Runnable onComplete) {
        JPanel container = new JPanel(null);
        Dimension size = root.getSize();

        //Position old panel at (0, 0)
        oldPanel.setBounds(0, 0, size.width, size.height);

        //Position new panel offscreen based on direction
        int startX = directionX * size.width;
        int startY = directionY * size.height;
        newPanel.setBounds(startX, startY, size.width, size.height);

        container.add(oldPanel);
        container.add(newPanel);

        root.removeAll();
        root.setLayout(new BorderLayout());
        root.add(container);
        root.revalidate();
        root.repaint();

        //Animate sliding
        Timer timer = new Timer(10, null);
        final int ANIMATION_STEPS = 25;
        final int[] step = {0};

        timer.addActionListener(e -> {
            step[0]++;

            if (step[0] >= ANIMATION_STEPS) {
                timer.stop();
                onComplete.run();
            } else {
                //Ease-out cubic
                double progress = (double) step[0] / ANIMATION_STEPS;
                double easedProgress = 1 - Math.pow(1 - progress, 3);

                int currentX = (int) (startX - (startX * easedProgress));
                int currentY = (int) (startY - (startY * easedProgress));

                int oldX = (int) (-directionX * size.width * easedProgress);
                int oldY = (int) (-directionY * size.height * easedProgress);

                oldPanel.setBounds(oldX, oldY, size.width, size.height);
                newPanel.setBounds(currentX, currentY, size.width, size.height);

                container.revalidate();
                container.repaint();
            }
        });

        timer.start();
    }

}
