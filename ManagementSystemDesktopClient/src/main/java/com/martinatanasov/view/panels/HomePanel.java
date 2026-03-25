package com.martinatanasov.view.panels;

import com.martinatanasov.user.UserController;
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
    private final JPanel view;
    private final JButton logoutButton;
    private final Router router;
    private final UserController userController;

    public HomePanel(@Value("${app.theme-variant}") String themeVariant,
            @Value("${app.theme-name}") String themeName,
            Router router, UserController userController) {
        this.userController = userController;
        this.router = router;
        setAppTheme(themeVariant, themeName);
        view = new JPanel(new MigLayout("insets 40 60 40 60, wrap, alignx center, aligny center"));
        view.setName("home-panel");
        JLabel header = new JLabel("Management System - Home");
        header.setFont(new Font("SansSerif", Font.BOLD, 32));
        view.add(header, "alignx center, gapbottom 35, wrap");

        logoutButton = new JButton("Logout");
        logoutButton.setName("logout-button");
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        logoutButton.setPreferredSize(new Dimension(350, 45));

        view.add(logoutButton, "alignx center, w 350!, h 45!, wrap");

        addListeners();
    }

    private void addListeners() {
        logoutButton.addActionListener(e -> {
            userController.logout();
            router.navigateBackTo(Routes.LOGIN);
        });

        logoutButton.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && logoutButton.isShowing()) {
                SwingUtilities.invokeLater(() -> {
                    if (logoutButton.isFocusable() && !logoutButton.isFocusOwner()) {
                        logoutButton.requestFocusInWindow();
                    }
                });
            }
        });
    }

}
