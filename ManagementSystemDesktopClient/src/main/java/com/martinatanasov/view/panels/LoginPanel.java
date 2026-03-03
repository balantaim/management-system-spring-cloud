package com.martinatanasov.view.panels;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.martinatanasov.user.UserController;
import com.martinatanasov.view.Theme;
import com.martinatanasov.view.router.Router;
import com.martinatanasov.view.router.Routes;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Slf4j
@Component
public class LoginPanel implements Theme {

    @Getter
    private final JPanel view;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JLabel emailErrorLabel;
    private final JLabel passwordErrorLabel;
    private final Router router;
    private final UserController userController;
    private final JLabel registerLink;
    private boolean isLoading = false;

    public LoginPanel(@Value("${app.theme-variant}") String themeVariant,
            @Value("${app.theme-name}") String themeName,
            @Value("${flat.linux-decorations.enabled}") Boolean enableDecorations,
            Router router,
            UserController userController) {
        this.router = router;
        this.userController = userController;
        setAppTheme(themeVariant, themeName);
        enableDecorations(enableDecorations);
        view = new JPanel();
        view.setName("login-panel");
        view.setLayout(new MigLayout("insets 40 60 40 60, fillx, wrap, alignx center, aligny center", "[350!]"));
        view.setPreferredSize(new Dimension(600, 600));

        // Header
        JLabel header = new JLabel("Login Form");
        header.setFont(new Font("SansSerif", Font.BOLD, 32));

        view.add(header, "alignx center, gapbottom 35, wrap");
        // Email
        JLabel emailLabel = new JLabel("Email");
        // Email field
        emailField = new JTextField();
        emailField.setName("email-field");
        emailField.setPreferredSize(new Dimension(350, 45));
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your email");
        emailField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
                new FlatSVGIcon("static/images/mail.svg", 0.55f));
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
        // Password field
        passwordField = new JPasswordField();
        passwordField.setName("password-field");
        passwordField.setPreferredSize(new Dimension(350, 45));
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        passwordField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
                new FlatSVGIcon("static/images/lock.svg", 0.55f));
        // Password error label
        passwordErrorLabel = new JLabel("Password is too short");
        passwordErrorLabel.setName("error-password");
        passwordErrorLabel.setForeground(errorColor());
        passwordErrorLabel.setFont(passwordErrorLabel.getFont().deriveFont(12f));
        passwordErrorLabel.setVisible(false);

        view.add(passwordLabel, "alignx left");
        view.add(passwordField, "w 350!, height 45!, alignx center");
        view.add(passwordErrorLabel, "alignx left, gapbottom 25");

        // Login button
        loginButton = new JButton("Login");
        loginButton.setName("login-button");
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginButton.setPreferredSize(new Dimension(350, 45));

        view.add(loginButton, "growx, height 45!, pushx, gapbottom 25");

        registerLink = new JLabel("<html><a href='#'>Don't have an account? Register</a></html>");
        registerLink.setName("register-link");
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        view.add(registerLink, "alignx center, gapbottom 10");

        //Set the view to the controller
        userController.setLoginPanel(view);
        addListeners();
    }

    private void addListeners() {
        loginButton.addActionListener(e -> {
            if (!isLoading) {
                tryLoginInTheBackground();
            }
        });

        emailField.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && emailField.isShowing()) {
                SwingUtilities.invokeLater(emailField::requestFocusInWindow);
            }
        });

        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isLoading) {
                    router.navigateTo(Routes.REGISTER);
                }
            }
        });
    }

    private void tryLoginInTheBackground() {
        //                 //isLoading = true;
//                setOrResetBusyness();
//                boolean successLogin = userController.login(emailField.getText(), passwordField.getPassword());
//                if (successLogin) {
//                    setOrResetBusyness();
//                    router.navigateTo(Routes.HOME);
//                }
//                setOrResetBusyness();

        new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() {
                setOrResetBusyness();
                // Runs in background thread
                return userController.login(emailField.getText(), passwordField.getPassword());
            }

            @Override
            protected void done() {
                // Back on EDT (safe to update UI)
                try {
                    boolean successLogin = get();
                    if (successLogin) {
                        router.navigateTo(Routes.HOME);
                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                } finally {
                    setOrResetBusyness();
                }
            }
        }.execute();
    }

    private void setOrResetBusyness() {
        if (isLoading) {
            isLoading = false;
            loginButton.setText("Login");
            loginButton.setEnabled(true);
        } else {
            isLoading = true;
            loginButton.setText("Loading");
            loginButton.setEnabled(false);
        }
    }

}
