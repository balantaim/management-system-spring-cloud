package com.martinatanasov;

import com.martinatanasov.utils.SwingTestUtils;
import com.martinatanasov.view.panels.LoginPanel;
import lombok.extern.slf4j.Slf4j;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.Robot;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.*;
import java.awt.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
class LoginPanelIT {

    @Autowired
    private LoginPanel loginPanel;
    @Autowired
    private SwingTestUtils utils;
    private FrameFixture window;
    private Robot robot;

    //Disable Headless mode (Show UI in tests)
    @BeforeAll
    public static void saveProperty() {
        System.setProperty("java.awt.headless", "false");
    }

    @BeforeEach
    public void setUp() {
        //Get the robot from current thread
        robot = BasicRobot.robotWithCurrentAwtHierarchy();
        //Print Frame that we get
        robot.finder().findAll(new GenericTypeMatcher<JFrame>(JFrame.class, true) {
            @Override protected boolean isMatching(JFrame f) {
                log.info("Found Frame: Name = {} Title = {}", f.getName(), f.getTitle());
                return true;
            }
        });
        //Get the main frame by name
        window = WindowFinder.findFrame("main-frame")
                .withTimeout(15000)
                .using(robot);
        printAllComponentsFromTheFrame();
    }

    @Test
    void loginScreenDetailsShouldAppear() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            //Init view
            JPanel view = loginPanel.getView();
            JLabel header = utils.findLabelWithText(view, "Login Form");
            JLabel emailLabel = utils.findLabelWithText(view, "Email");
            JLabel passwordLabel = utils.findLabelWithText(view, "Password");

            assertThat(header)
                    .as("Login Form header should be present")
                    .isNotNull();

            assertThat(header.getText()).isEqualTo("Login Form");

            assertThat(emailLabel)
                    .as("Email label should be present")
                    .isNotNull();

            assertThat(emailLabel.getText()).isEqualTo("Email");

            assertThat(passwordLabel)
                    .as("Password label should be present")
                    .isNotNull();

            assertThat(passwordLabel.getText()).isEqualTo("Password");


//            FrameFixture loginPanel = WindowFinder.findFrame("login-panel").using(robot);
//
//
//            loginPanel.textBox("email-field").enterText("test@example.com");
            window.textBox("email-field").enterText("Username");
//            window.textBox("password-field").enterText("Password");

        });
    }

    @Test
    void loginButtonShouldNavigateYouToHomeView() throws Exception {

        //Init view
        JPanel view = loginPanel.getView();
        JButton loginButton = utils.getButton("Login", loginPanel.getView());
//            JTextField emailField = utils.findLabelWithText(view, "email");
//            JPasswordField passwordField = utils.findLabelWithText(view, "password");
//
//            assertThat(emailField)
//                    .as("Email label should be present")
//                    .isNotNull();
//            assertThat(passwordLabel)
//                    .as("Password label should be present")
//                    .isNotNull();
//
//            emailLabel.setText("Icarus");
//            passwordLabel.setText("Password");

        assertThat(loginButton)
                .as("Login button should be present")
                .isNotNull();

        assertThat(loginButton.getText()).isEqualTo("Login");

        loginButton.doClick();

    }

    private void printAllComponentsFromTheFrame() {
        for (Component c : robot.finder().findAll(window.target(), c -> c.getName() != null)) {
            log.info("Name: {} Type: {}", c.getName(), c.getClass().getName());
        }
    }

}
