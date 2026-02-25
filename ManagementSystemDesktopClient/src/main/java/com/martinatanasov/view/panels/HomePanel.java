package com.martinatanasov.view.panels;

import com.martinatanasov.view.Theme;
import com.martinatanasov.view.router.Router;
import com.martinatanasov.view.router.Routes;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class HomePanel implements Theme {

    @Getter
    private JPanel view;
    private final Router router;

    public HomePanel(@Value("${app.theme-variant}") String themeVariant,
            @Value("${app.theme-name}") String themeName,
            Router router) {
        this.router = router;
        setAppTheme(themeVariant, themeName);
        view = new JPanel();
        JLabel header = new JLabel("Management System - Home");
        view.add(header);

        JButton logoutButton = new JButton("Logout");

        view.add(logoutButton);

        logoutButton.addActionListener(e -> {
            router.navigateTo(Routes.LOGIN);
        });
    }

}
