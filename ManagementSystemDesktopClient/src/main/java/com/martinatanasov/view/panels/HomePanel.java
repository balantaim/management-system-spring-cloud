package com.martinatanasov.view.panels;

import com.martinatanasov.view.Theme;
import com.martinatanasov.view.router.Router;
import com.martinatanasov.view.router.Routes;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;

@Component
public class HomePanel implements Theme {

    @Getter
    private JPanel view;
    private JButton logoutButton;
    private final Router router;

    public HomePanel(@Value("${app.theme-variant}") String themeVariant,
            @Value("${app.theme-name}") String themeName, Router router) {
        this.router = router;
        setAppTheme(themeVariant, themeName);
        view = new JPanel(new MigLayout("wrap"));
        view.setName("home-panel");
        JLabel header = new JLabel("Management System - Home");
        view.add(header);

        logoutButton = new JButton("Logout");
        logoutButton.setName("logout-button");
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        logoutButton.setPreferredSize(new Dimension(350, 45));

        view.add(logoutButton);

        addListeners();
    }

    private void addListeners() {
        logoutButton.addActionListener(e -> {
            router.navigateTo(Routes.LOGIN);
        });

        logoutButton.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && logoutButton.isShowing()) {
                SwingUtilities.invokeLater(() -> logoutButton.requestFocusInWindow());
            }
        });
    }

}
