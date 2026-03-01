package com.martinatanasov.view.panels;

import com.martinatanasov.user.UserService;
import com.martinatanasov.user.UserServiceImpl;
import com.martinatanasov.view.Theme;
import com.martinatanasov.view.router.Router;
import com.martinatanasov.view.router.Routes;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;

@Lazy
@Component
public class LoginPanel implements Theme {

    @Getter
    private JPanel view;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final Router router;
    private final UserService userService;


    public LoginPanel(@Value("${app.theme-variant}") String themeVariant,
            @Value("${app.theme-name}") String themeName,
            @Value("${flat.linux-decorations.enabled}") Boolean enableDecorations,
            Router router,
            UserServiceImpl userService) {
        this.router = router;
        this.userService = userService;
        setAppTheme(themeVariant, themeName);
        enableDecorations(enableDecorations);
        view = new JPanel();
        view.setName("login-panel");
        view.setLayout(new BoxLayout(view, BoxLayout.Y_AXIS));
        view.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        view.setPreferredSize(new Dimension(600, 420));

        // Header
        JLabel header = new JLabel("Login Form");
        header.setFont(new Font("SansSerif", Font.BOLD, 32));
        header.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        view.add(header);
        view.add(Box.createVerticalStrut(35));

        // Email
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        // Email field
        emailField = new JTextField();
        emailField.setName("email-field");
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
        passwordField.setName("password-field");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        passwordField.setPreferredSize(new Dimension(350, 45));
        passwordField.putClientProperty("JTextField.placeholderText", "Enter your password");

        view.add(passwordLabel);
        view.add(Box.createVerticalStrut(6));
        view.add(passwordField);
        view.add(Box.createVerticalStrut(35));

        // Login button
        loginButton = new JButton("Login");
        loginButton.setName("login-button");
        loginButton.setAlignmentX(0.5f);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        loginButton.setPreferredSize(new Dimension(350, 45));

        // Dark Purple Accent
        //Color darkPurple = new Color(88, 28, 135);
        //loginButton.setBackground(darkPurple);
        //loginButton.setForeground(Color.WHITE);

        view.add(loginButton);
        // Login click
        // loginButton.addActionListener(e -> toast.showToast("You are logged in", this));
        addListeners();
    }

    private void addListeners() {
        loginButton.addActionListener(e -> {
            int responseCode = userService.login(emailField.getText(), passwordField.getPassword());
            switch (responseCode) {
                case 200 ->  router.navigateTo(Routes.HOME);
                case 401 -> {} //No role
                case 403 -> {} //bad credentials
                case 500, 504 -> {} //server problem
                default -> {}
            }
        });

        emailField.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && emailField.isShowing()) {
                SwingUtilities.invokeLater(emailField::requestFocusInWindow);
            }
        });
    }

}
