package com.martinatanasov.view.panels;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.martinatanasov.uicomponents.Toast;
import com.martinatanasov.user.UserController;
import com.martinatanasov.utils.AsyncExecutor;
import com.martinatanasov.view.Theme;
import com.martinatanasov.view.router.Router;
import com.martinatanasov.view.router.Routes;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@Component
public class RegisterPanel implements Theme {

    @Getter
    private final JPanel view;
    private final Router router;
    private final UserController userController;
    private final JTextField emailField;
    private final JTextField fullNameField;
    private final JPasswordField passwordField;
    private final JPasswordField rePasswordField;
    private final JLabel emailErrorLabel;
    private final JLabel fullNameErrorLabel;
    private final JLabel passwordErrorLabel;
    private final JLabel rePasswordErrorLabel;
    private final JButton registerButton;
    private final JLabel loginLink;
    private final Toast toast;
    private boolean isLoading = false;

    public RegisterPanel(@Value("${app.theme-variant}") String themeVariant,
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
        view.setName("register-panel");
        view.setLayout(new MigLayout("insets 40 60 40 60, fillx, wrap, alignx center, aligny center", "[350!]"));
        view.setMinimumSize(new Dimension(600, 600));

        // Header
        JLabel header = new JLabel("Register Form");
        header.setFont(new Font("SansSerif", Font.BOLD, 32));
        view.add(header, "alignx center, gapbottom 25, wrap");

        // Email
        JLabel emailLabel = new JLabel("Email (required)");
        emailField = createTextField("email-field", "Enter your email",
                "Enter valid email", "static/images/mail-36.svg");
        // Email error label
        emailErrorLabel = new JLabel("Invalid email address");
        emailErrorLabel.setName("error-email");
        emailErrorLabel.setForeground(getErrorColor());
        emailErrorLabel.setFont(emailErrorLabel.getFont().deriveFont(12f));
        emailErrorLabel.setVisible(false);

        view.add(emailLabel, "alignx left");
        view.add(emailField, "growx, height 45!");
        view.add(emailErrorLabel, "alignx left, gapbottom 8");

        // FullName
        JLabel fullNameLabel = new JLabel("Full name (required)");
        fullNameField = createTextField("full-name-field", "Enter full name",
                "Full name must be between 2 and 150 characters", "static/images/user-36.svg");
        // Email error label
        fullNameErrorLabel = new JLabel("Full name must be between 2 and 150 characters");
        fullNameErrorLabel.setName("error-full-name");
        fullNameErrorLabel.setForeground(getErrorColor());
        fullNameErrorLabel.setFont(fullNameErrorLabel.getFont().deriveFont(12f));
        fullNameErrorLabel.setVisible(false);

        view.add(fullNameLabel, "alignx left");
        view.add(fullNameField, "growx, height 45!");
        view.add(fullNameErrorLabel, "alignx left, gapbottom 15");

        // Password
        JLabel passwordLabel = new JLabel("Password (required)");
        passwordField = createPasswordField("password-field", "Enter your password",
                "Password must contain at least 8 characters with uppercase, lowercase, number, and special characters");
        // Password error label
        passwordErrorLabel = new JLabel("Password 8 or more uppercase, lowercase, number, and special characters");
        passwordErrorLabel.setName("error-password");
        passwordErrorLabel.setForeground(getErrorColor());
        passwordErrorLabel.setFont(passwordErrorLabel.getFont().deriveFont(12f));
        passwordErrorLabel.setVisible(false);

        view.add(passwordLabel, "alignx left");
        view.add(passwordField, "growx, height 45!");
        view.add(passwordErrorLabel, "alignx left, gapbottom 15");

        // Re-password
        JLabel rePasswordLabel = new JLabel("Re-password (required)");
        rePasswordField = createPasswordField("re-password-field", "Repeat your password", "Enter the password again");
        // Re-password error label
        rePasswordErrorLabel = new JLabel("Passwords do not match");
        rePasswordErrorLabel.setName("error-re-pass");
        rePasswordErrorLabel.setForeground(getErrorColor());
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
        makeLabelFocusable(loginLink);

        view.add(loginLink, "alignx center");
        addListeners();
    }

    private JPasswordField createPasswordField(String id, String placeholder, String tooltip) {
        JPasswordField customField = new JPasswordField();
        customField.setName(id);
        customField.setPreferredSize(new Dimension(350, 45));
        FlatSVGIcon passIcon = new FlatSVGIcon("static/images/lock-36.svg", 0.55f);
        passIcon.setColorFilter(getColorFilter(getLabelColor()));
        customField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        customField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, passIcon);
        customField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true;");
        customField.setToolTipText(tooltip);
        return customField;
    }

    private JTextField createTextField(String id, String placeholder, String tooltip, String svgIconPath) {
        JTextField textField = new JTextField();
        textField.setName(id);
        textField.setPreferredSize(new Dimension(350, 45));
        FlatSVGIcon emailIcon = new FlatSVGIcon(svgIconPath, 0.55f);
        emailIcon.setColorFilter(getColorFilter(getLabelColor()));
        textField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        textField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, emailIcon);
        textField.setToolTipText(tooltip);
        return textField;
    }

    private void addListeners() {
        registerButton.addActionListener(e -> {
            if (!isLoading) {
                tryRegisterInTheBackground();
            }
        });

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

    private void tryRegisterInTheBackground() {
        boolean isPassValid = userController.isPasswordValid(new String(passwordField.getPassword()));
        boolean isRePassValid = userController.isPasswordsEqual(new String(passwordField.getPassword()), new String(rePasswordField.getPassword()));
        boolean isEmailValid = userController.isEmailValid(emailField.getText());
        boolean isFullNameValid = userController.isFullNameValid(fullNameField.getText());

        SwingUtilities.invokeLater(() -> {
            emailErrorLabel.setVisible(!isEmailValid);
            fullNameErrorLabel.setVisible(!isFullNameValid);
            passwordErrorLabel.setVisible(!isPassValid);
            rePasswordErrorLabel.setVisible(!isRePassValid);
        });

        if (isPassValid && isRePassValid && isEmailValid && isFullNameValid) {
            AsyncExecutor.run(
                    registerButton,
                    this::setOrResetBusyness,
                    () -> userController.register(emailField.getText(), fullNameField.getText(), new String(passwordField.getPassword())),
                    success -> {
                        if (success) {
                            toast.showToast("User " + emailField.getText() + " is registered!", router.getMainFrame());
                        }
                    }
            );
        }
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
                if ((e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) && !isLoading) {
                    navigateToLoginForm();
                }
            }
        });
    }

    private void navigateToLoginForm() {
        router.navigateTo(Routes.LOGIN);
    }

    private void setOrResetBusyness() {
        SwingUtilities.invokeLater(() -> {
            if (isLoading) {
                isLoading = false;
                registerButton.setText("Register");
            } else {
                isLoading = true;
                registerButton.setText("Loading");
            }
        });
    }

}
