package com.martinatanasov.view.panels;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.martinatanasov.requests.AsyncExecutor;
import com.martinatanasov.uicomponents.toast.Toast;
import com.martinatanasov.user.UserController;
import com.martinatanasov.view.Theme;
import com.martinatanasov.view.router.Router;
import com.martinatanasov.view.router.Routes;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@Slf4j
@Singleton
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
    private final Toast toast;
    private boolean isLoading = false;

    public LoginPanel(@Value("${app.theme-variant}") String themeVariant,
            @Value("${app.theme-name}") String themeName,
            @Value("${flat.linux-decorations.enabled}") Boolean enableDecorations,
            Router router,
            UserController userController, Toast toast) {
        this.toast = toast;
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
        FlatSVGIcon emailIcon = new FlatSVGIcon("static/images/mail-36.svg", 0.55f);
        emailIcon.setColorFilter(getColorFilter(getLabelColor()));
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your email");
        emailField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, emailIcon);
        // Email error label
        emailErrorLabel = new JLabel("Invalid email address");
        emailErrorLabel.setName("error-email");
        emailErrorLabel.setForeground(getErrorColor());
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
        FlatSVGIcon passIcon = new FlatSVGIcon("static/images/lock-36.svg", 0.55f);
        passIcon.setColorFilter(getColorFilter(getLabelColor()));
        passwordField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, passIcon);
        passwordField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true;");
        // Password error label
        passwordErrorLabel = new JLabel("Min 8 or more uppercase, lowercase, number, and special characters");
        passwordErrorLabel.setName("error-password");
        passwordErrorLabel.setForeground(getErrorColor());
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
        makeLabelFocusable(registerLink);
        view.add(registerLink, "alignx center, gapbottom 10");

        addListeners();
    }

    private void makeLabelFocusable(JLabel label) {
        //Enable focusable element
        label.setFocusable(true);
        //Add transparent border
        label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        //Add mouse listener
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                label.requestFocusInWindow();
            }
        });
        //Add focus listener
        label.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                //Replace empty border with default accent color
                label.setBorder(BorderFactory.createLineBorder(getAccentColor(), 2));
            }

            @Override
            public void focusLost(FocusEvent e) {
                //Back to empty border
                label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            }
        });
        //Add keyboard support
        label.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    navigateToRegisterForm();
                }
            }
        });
    }

    private void addListeners() {
        loginButton.addActionListener(e -> {
            if (!isLoading) {
                tryLoginInTheBackground();
            }
        });
        //Focus email field on view load
        emailField.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && emailField.isShowing()) {
                SwingUtilities.invokeLater(() -> {
                    if (emailField.isFocusable() && !emailField.isFocusOwner()) {
                        emailField.requestFocusInWindow();
                    }
                });
            }
        });

        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isLoading) {
                    navigateToRegisterForm();
                }
            }
        });
    }

    private void tryLoginInTheBackground() {
        boolean isEmailValid = userController.isEmailValid(emailField.getText());
        boolean isPasswordValid = userController.isPasswordValid(new String(passwordField.getPassword()));
        SwingUtilities.invokeLater(() -> {
            emailErrorLabel.setVisible(!isEmailValid);
            passwordErrorLabel.setVisible(!isPasswordValid);
        });

        if (isEmailValid && isPasswordValid) {
            AsyncExecutor.run(
                    loginButton,
                    this::setOrResetBusyness,
                    () -> userController.login(emailField.getText(), passwordField.getPassword()),
                    status -> {
                        switch (status) {
                            case SUCCESS -> router.navigateTo(Routes.HOME);
                            case INVALID_CREDENTIALS, BAD_REQUEST ->
                                    toast.showErrorToast("Invalid email or password", router.getMainFrame());
                            case ACCOUNT_LOCKED ->
                                    toast.showErrorToast("Your account has been locked", router.getMainFrame());
                            case TIMEOUT -> toast.showErrorToast("Timeout has been reached", router.getMainFrame());
                            case TOO_MANY_REQUESTS ->
                                    toast.showErrorToast("Server is busy. Please try again later", router.getMainFrame());
                            case SERVER_ERROR ->
                                    toast.showErrorToast("Server is unavailable. Please try again later", router.getMainFrame());
                            default ->
                                    toast.showErrorToast("Unknown error. Please try again later", router.getMainFrame());
                        }
                    }
            );
        }
    }

    private void navigateToRegisterForm() {
        router.navigateTo(Routes.REGISTER);
    }

    private void setOrResetBusyness() {
        SwingUtilities.invokeLater(() -> {
            if (isLoading) {
                isLoading = false;
                loginButton.setText("Login");
            } else {
                isLoading = true;
                loginButton.setText("Loading");
            }
        });
    }

}
