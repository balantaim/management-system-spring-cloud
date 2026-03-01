package com.martinatanasov.utils;

import javax.swing.*;
import java.awt.*;

@org.springframework.stereotype.Component
public class SwingTestUtils {

    public JLabel findLabelWithText(Container container, String text) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel label) {
                if (text.equals(label.getText())) {
                    return label;
                }
            }
            if (component instanceof Container child) {
                JLabel result = findLabelWithText(child, text);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public JButton getButton(String text, JPanel jpanel) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) {
            return findButtonByText(jpanel, text);
        }

        final JButton[] result = new JButton[1];

        SwingUtilities.invokeAndWait(() ->
                result[0] = findButtonByText(jpanel, text)
        );
        return result[0];
    }

    public JButton findButtonByText(Container container, String text) {
        if (!SwingUtilities.isEventDispatchThread()) {
            final JButton[] result = new JButton[1];
            try {
                SwingUtilities.invokeAndWait(() ->
                        result[0] = findButtonByText(container, text)
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return result[0];
        }

        for (Component component : container.getComponents()) {
            if (component instanceof JButton button) {
                if (text.equals(button.getText())) {
                    return button;
                }
            }
            if (component instanceof Container child) {
                JButton result = findButtonByText(child, text);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public <T extends Component> T findComponentByName(Container container, String name, Class<T> type) {
        if (!SwingUtilities.isEventDispatchThread()) {
            final Object[] result = new Object[1];
            try {
                SwingUtilities.invokeAndWait(() ->
                        result[0] = findComponentByName(container, name, type)
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return type.cast(result[0]);
        }
        for (Component component : container.getComponents()) {

            if (type.isInstance(component) && name.equals(component.getName())) {
                return type.cast(component);
            }

            if (component instanceof Container child) {
                T result = findComponentByName(child, name, type);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

}
