package com.martinatanasov.animations.transition;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CrossfadeTransition implements PanelTransition {

    private final int duration;

    public CrossfadeTransition() {
        this(300);
    }

    public CrossfadeTransition(int duration) {
        this.duration = duration;
    }

    @Override
    public void animate(JPanel root, JPanel oldPanel, JPanel newPanel, Runnable onComplete) {
        Dimension size = root.getSize();

        if (size.width <= 0 || size.height <= 0) {
            onComplete.run();
            return;
        }

        //Capture old panel
        BufferedImage oldImage = capturePanel(oldPanel, size);

        //Add new panel and layout
        root.removeAll();
        root.setLayout(new BorderLayout());
        root.add(newPanel);
        root.revalidate();
        root.repaint();

        //Wait for layout
        SwingUtilities.invokeLater(() -> {
            //Capture new panel
            BufferedImage newImage = capturePanel(newPanel, size);

            //Create animation layer
            final float[] progress = {0.0f};

            JComponent animationLayer = new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - progress[0]));
                    g2d.drawImage(oldImage, 0, 0, null);

                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, progress[0]));
                    g2d.drawImage(newImage, 0, 0, null);

                    g2d.dispose();
                }
            };

            animationLayer.setOpaque(true);

            //Replace with animation layer
            root.removeAll();
            root.setLayout(new BorderLayout());
            root.add(animationLayer);
            root.revalidate();
            root.repaint();

            //Animate
            int frameDelay = 1000 / 60;
            float increment = (float) frameDelay / duration;

            Timer timer = new Timer(frameDelay, null);
            timer.addActionListener(e -> {
                progress[0] += increment;

                if (progress[0] >= 1.0f) {
                    progress[0] = 1.0f;
                    timer.stop();
                    onComplete.run();
                } else {
                    animationLayer.repaint();
                }
            });
            timer.start();
        });
    }

    private BufferedImage capturePanel(JPanel panel, Dimension size) {
        BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        panel.setSize(size);
        panel.setBounds(0, 0, size.width, size.height);
        panel.doLayout();
        panel.validate();
        panel.printAll(g2d);

        g2d.dispose();
        return image;
    }

}