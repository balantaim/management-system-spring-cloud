package com.martinatanasov.view;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class LoginPanel implements Theme {

    JPanel card;
    JTextField emailField;
    JPasswordField passwordField;
    JButton loginButton;

    public LoginPanel(@Value("${app.theme-variant}") String themeVariant, @Value("${app.theme-name}") String themeName) {
        setAppTheme(themeVariant, themeName);
        card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        card.setPreferredSize(new Dimension(600, 420));

        // Header
        JLabel header = new JLabel("Management System");
        header.setFont(new Font("SansSerif", Font.BOLD, 32));
        header.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        card.add(header);
        card.add(Box.createVerticalStrut(35));

        // Email
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        // Email field
        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        emailField.setPreferredSize(new Dimension(350, 45));
        emailField.putClientProperty("JTextField.placeholderText", "Enter your email");

        card.add(emailLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(emailField);
        card.add(Box.createVerticalStrut(25));

        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        // Password field
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        passwordField.setPreferredSize(new Dimension(350, 45));
        passwordField.putClientProperty("JTextField.placeholderText", "Enter your password");

        card.add(passwordLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(35));

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

        card.add(loginButton);

        // Login click
        //loginButton.addActionListener(e -> toast.showToast("You are logged in", this));
    }

    public JPanel getLoginView() {
        return card;
    }

}
