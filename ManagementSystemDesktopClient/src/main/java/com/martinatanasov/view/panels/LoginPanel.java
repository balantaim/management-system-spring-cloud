package com.martinatanasov.view.panels;

import com.martinatanasov.services.UserService;
import com.martinatanasov.view.Theme;
import com.martinatanasov.view.router.Router;
import com.martinatanasov.view.router.Routes;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class LoginPanel implements Theme {

    @Getter
    private JPanel view;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private final Router router;
    private final UserService userService;


    public LoginPanel(@Value("${app.theme-variant}") String themeVariant,
            @Value("${app.theme-name}") String themeName,
            Router router,
            UserService userService) {
        this.router = router;
        this.userService = userService;

        setAppTheme(themeVariant, themeName);
        view = new JPanel();
        view.setLayout(new BoxLayout(view, BoxLayout.Y_AXIS));
        view.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        view.setPreferredSize(new Dimension(600, 420));

        // Header
        JLabel header = new JLabel("Management System - Login");
        header.setFont(new Font("SansSerif", Font.BOLD, 32));
        header.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        view.add(header);
        view.add(Box.createVerticalStrut(35));

        // Email
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        // Email field
        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        emailField.setPreferredSize(new Dimension(350, 45));
        emailField.putClientProperty("JTextField.placeholderText", "Enter your email");

        view.add(emailLabel);
        view.add(Box.createVerticalStrut(6));
        view.add(emailField);
        view.add(Box.createVerticalStrut(25));

        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        // Password field
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        passwordField.setPreferredSize(new Dimension(350, 45));
        passwordField.putClientProperty("JTextField.placeholderText", "Enter your password");

        view.add(passwordLabel);
        view.add(Box.createVerticalStrut(6));
        view.add(passwordField);
        view.add(Box.createVerticalStrut(35));

        // Login button
        loginButton = new JButton("Login");
        loginButton.setAlignmentX(0.5f);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        loginButton.setPreferredSize(new Dimension(350, 45));

        // Dark Purple Accent
        //Color darkPurple = new Color(88, 28, 135);
        //loginButton.setBackground(darkPurple);
        //loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        view.add(loginButton);

        // Login click
        // loginButton.addActionListener(e -> toast.showToast("You are logged in", this));
        loginButton.addActionListener(e -> {
            if (userService.login(emailField.getText(), passwordField.getPassword())) {
                router.navigateTo(Routes.HOME);
            }
        });
    }

}
