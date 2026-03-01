package com.martinatanasov;

import com.martinatanasov.utils.SwingTestUtils;
import com.martinatanasov.view.panels.LoginPanel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.*;
import java.awt.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
class LoginPanelE2E {

    @Autowired
    private LoginPanel loginPanel;
    @Autowired
    private SwingTestUtils utils;

    //Disable Headless mode (Show UI in tests)
    @BeforeAll
    public static void saveProperty() {
        System.setProperty("java.awt.headless", "false");
    }

    @AfterEach
    void closeApp() {
        for (Frame frame : Frame.getFrames()) {
            frame.dispose();
        }
    }

    @Test
    void loginScreenDetailsShouldAppear() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            //Init view
            JPanel view = loginPanel.getView();
            JLabel header = utils.findLabelWithText(view, "Login Form");
            JLabel emailLabel = utils.findLabelWithText(view, "Email");
            JLabel passwordLabel = utils.findLabelWithText(view, "Password");
            //Check names of labels
            assertThat(header).as("Login Form header should be present").isNotNull();

            assertThat(header.getText()).isEqualTo("Login Form");

            assertThat(emailLabel).as("Email label should be present").isNotNull();

            assertThat(emailLabel.getText()).isEqualTo("Email");

            assertThat(passwordLabel).as("Password label should be present").isNotNull();

            assertThat(passwordLabel.getText()).isEqualTo("Password");
            //Check email field
            JTextField emailField = utils.findComponentByName(loginPanel.getView(), "email-field", JTextField.class);

            assertThat(emailField).isNotNull();
            assertThat(emailField.isEnabled()).isTrue();
            //Set text to email field
            SwingUtilities.invokeLater(() -> emailField.setText("test@example.com"));
            //Check password field
            JPasswordField passwordField = utils.findComponentByName(loginPanel.getView(), "password-field", JPasswordField.class);

            assertThat(passwordField).isNotNull();
            assertThat(passwordField.isEnabled()).isTrue();
            //Set text to password field
            SwingUtilities.invokeLater(() -> passwordField.setText("testPassword"));
            //Check login button
            JButton loginButton = utils.findComponentByName(loginPanel.getView(), "login-button", JButton.class);

            assertThat(loginButton).isNotNull();
            assertThat(loginButton.isEnabled()).isTrue();
            assertThat(loginButton.isVisible()).isTrue();
            assertThat(loginButton.getText()).isEqualTo("Login");

            loginButton.doClick();
        });
    }

}
