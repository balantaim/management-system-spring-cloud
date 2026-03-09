package com.martinatanasov.view.router;

import com.martinatanasov.view.MainFrame;
import lombok.Getter;
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
    private final Map<Routes, JPanel> routes = new ConcurrentHashMap<>();

    public void init(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        root = mainFrame.getRoot();
    }

    public void registerRoutes(Routes name, JPanel panel) {
        routes.put(name, panel);
    }

    public void navigateTo(Routes viewName) {
        JPanel panel = routes.get(viewName);
        if (panel == null) return;

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
