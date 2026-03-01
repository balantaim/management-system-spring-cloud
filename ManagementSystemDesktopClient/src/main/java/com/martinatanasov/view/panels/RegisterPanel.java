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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Component
public class RegisterPanel implements Theme {

    @Getter
    private final JPanel view;
    private final Router router;
    private final UserController userController;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JPasswordField rePasswordField;
    private final JLabel emailErrorLabel;
    private final JLabel passwordErrorLabel;
    private final JLabel rePasswordErrorLabel;
    private final JButton registerButton;
    private final JLabel loginLink;

    public RegisterPanel(@Value("${app.theme-variant}") String themeVariant,
            @Value("${app.theme-name}") String themeName,
            @Value("${flat.linux-decorations.enabled}") Boolean enableDecorations,
            Router router,
            UserController userController) {
        this.router = router;
        this.userController = userController;
        setAppTheme(themeVariant, themeName);
        enableDecorations(enableDecorations);
        view = new JPanel();
        view.setName("register-panel");
        view.setLayout(new MigLayout("insets 40 60 40 60, fillx, wrap, alignx center, aligny center", "[grow]"));
        view.setMinimumSize(new Dimension(600, 600));

        // Header
        JLabel header = new JLabel("Register Form");
        header.setFont(new Font("SansSerif", Font.BOLD, 32));
        view.add(header, "alignx center, gapbottom 25, wrap");

        // Email
        JLabel emailLabel = new JLabel("User email");
        emailField = new JTextField();
        emailField.setName("email-field");
        emailField.setPreferredSize(new Dimension(350, 45));
        emailField.putClientProperty("JTextField.placeholderText", "Enter your email");
        // Email error label
        emailErrorLabel = new JLabel("Invalid email address");
        emailErrorLabel.setName("error-email");
        emailErrorLabel.setForeground(errorColor());
        emailErrorLabel.setFont(emailErrorLabel.getFont().deriveFont(12f));
        emailErrorLabel.setVisible(false);

        view.add(emailLabel, "alignx left");
        view.add(emailField, "growx, height 45!");
        view.add(emailErrorLabel, "alignx left, gapbottom 15");

        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordField = new JPasswordField();
        passwordField.setName("password-field");
        passwordField.setPreferredSize(new Dimension(350, 45));
        passwordField.putClientProperty("JTextField.placeholderText", "Enter your password");
        // Password error label
        passwordErrorLabel = new JLabel("Password is too short");
        passwordErrorLabel.setName("error-password");
        passwordErrorLabel.setForeground(errorColor());
        passwordErrorLabel.setFont(passwordErrorLabel.getFont().deriveFont(12f));
        passwordErrorLabel.setVisible(false);

        view.add(passwordLabel, "alignx left");
        view.add(passwordField, "growx, height 45!");
        view.add(passwordErrorLabel, "alignx left, gapbottom 15");

        // Re-password
        JLabel rePasswordLabel = new JLabel("Re-password");
        rePasswordField = new JPasswordField();
        rePasswordField.setName("re-password-field");
        rePasswordField.setPreferredSize(new Dimension(350, 45));
        rePasswordField.putClientProperty("JTextField.placeholderText", "Confirm your password");
        // Re-password error label
        rePasswordErrorLabel = new JLabel("Passwords do not match");
        rePasswordErrorLabel.setName("error-re-pass");
        rePasswordErrorLabel.setForeground(errorColor());
        rePasswordErrorLabel.setFont(rePasswordErrorLabel.getFont().deriveFont(12f));
        rePasswordErrorLabel.setVisible(false);

        view.add(rePasswordLabel, "alignx left");
        view.add(rePasswordField, "growx, height 45!");
        view.add(rePasswordErrorLabel, "alignx left, gapbottom 25");

        // Register button
        registerButton = new JButton("Register");
        registerButton.setName("register-button");
        registerButton.setPreferredSize(new Dimension(350, 45));
        view.add(registerButton, "growx, height 45!, gapbottom 25");

        // Login link
        loginLink = new JLabel("<html><a href='#'>Already have an account? Go to Login</a></html>");
        loginLink.setName("login-link");
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        view.add(loginLink, "alignx center");
        addListeners();
    }

    private void addListeners() {
        emailField.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && emailField.isShowing()) {
                SwingUtilities.invokeLater(emailField::requestFocusInWindow);
            }
        });

        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                router.navigateTo(Routes.LOGIN);
            }
        });
    }

}
