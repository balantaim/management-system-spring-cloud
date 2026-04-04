package com.martinatanasov.view.router;

import com.martinatanasov.animations.transition.PanelTransition;
import com.martinatanasov.animations.transition.TransitionFactory;
import com.martinatanasov.animations.transition.TransitionType;
import com.martinatanasov.view.MainFrame;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Router {

    @Getter
    private JFrame mainFrame;
    private JPanel root;
    private JPanel currentPanel;
    private static final TransitionType transitionType = TransitionType.SLIDE_RIGHT;
    private final Map<Routes, JPanel> routes = new ConcurrentHashMap<>();

    public void init(MainFrame mainFrame, JPanel currentPanel) {
        this.mainFrame = mainFrame;
        root = mainFrame.getRoot();
        this.currentPanel = currentPanel;
    }

    public void registerRoutes(Routes name, JPanel panel) {
        routes.put(name, panel);
    }

    public void navigateTo(Routes viewName) {
        JPanel panel = routes.get(viewName);
        if (panel == null) return;

        if (transitionType == TransitionType.NONE) {
            showNewPanel(panel);
        } else {
            showTransitionAnimation(panel, transitionType);
        }
        currentPanel = panel;
    }

    public void navigateBackTo(Routes viewName) {
        TransitionType reverseTransitionType = getReverseTransitionType();
        JPanel panel = routes.get(viewName);
        if (panel == null) return;

        if (transitionType == TransitionType.NONE) {
            showNewPanel(panel);
        } else {
            showTransitionAnimation(panel, reverseTransitionType);
        }
        currentPanel = panel;
    }

    private @NonNull TransitionType getReverseTransitionType() {
        return switch (transitionType) {
            case TransitionType.SLIDE_RIGHT -> TransitionType.SLIDE_LEFT;
            case TransitionType.SLIDE_LEFT -> TransitionType.SLIDE_RIGHT;
            case TransitionType.SLIDE_UP -> TransitionType.SLIDE_DOWN;
            case TransitionType.SLIDE_DOWN -> TransitionType.SLIDE_UP;
            default -> TransitionType.CROSSFADE;
        };
    }

    private void showTransitionAnimation(JPanel panel, TransitionType transition) {
        PanelTransition panelTransition = TransitionFactory.getTransition(transition);
        if (panelTransition != null) {
            panelTransition.animate(root, currentPanel, panel, () -> showNewPanel(panel));
        }
    }

    private void showNewPanel(JPanel panel) {
        root.removeAll();
        root.add(panel);

        root.revalidate();
        root.repaint();
    }

    private GridBagConstraints createGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        return gbc;
    }

}
