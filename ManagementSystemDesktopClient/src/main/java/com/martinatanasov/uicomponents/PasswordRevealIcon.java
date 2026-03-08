package com.martinatanasov.uicomponents;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.AnimatedIcon;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class PasswordRevealIcon implements AnimatedIcon {

    private final Icon icon;
    private final int space;
    private final Color iconColor;

    public PasswordRevealIcon() {
        this(new FlatSVGIcon("static/images/eye-36.svg", 0.65f).setColorFilter(
                new FlatSVGIcon.ColorFilter(color -> UIManager.getColor("Label.foreground"))
        ), 3, UIManager.getColor("Label.foreground"));
    }

    public PasswordRevealIcon(Icon icon, int space, Color iconColor) {
        this.icon = icon;
        this.space = space;
        this.iconColor = iconColor;
    }

    @Override
    public void paintIconAnimated(Component component, Graphics graphics, int x, int y, float animatedValue) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        int s = UIScale.scale(space);
        icon.paintIcon(component, graphics2D, x, y);
        if (animatedValue > 0) {
            float startX = x + s;
            float startY = y + getIconHeight() - s;
            float endX = x + getIconWidth() - s;
            float endY = y + s;

            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            Shape shape = new Line2D.Float(startX, startY, startX + (endX - startX) * animatedValue, startY + (endY - startY) * animatedValue);

            drawLine(graphics2D, shape, component.getParent().getBackground(), 4f);
            drawLine(graphics2D,
                    shape,
                    iconColor != null ? iconColor : new Color(150, 150, 150),
                    1.5f);
        }
        graphics2D.dispose();
    }

    @Override
    public float getValue(Component component) {
        return ((AbstractButton) component).isSelected() ? 0 : 1;
    }

    @Override
    public int getIconWidth() {
        return icon.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return icon.getIconHeight();
    }

    private void drawLine(Graphics2D graphics2D, Shape shape, Color color, float size) {
        graphics2D.setColor(color);
        graphics2D.setStroke(new BasicStroke(UIScale.scale(size), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics2D.draw(shape);
    }

}
