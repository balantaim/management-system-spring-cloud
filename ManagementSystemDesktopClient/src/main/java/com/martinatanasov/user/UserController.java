package com.martinatanasov.user;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class UserController {

    @Setter
    private JPanel loginPanel;
    @Setter
    private JPanel registerPanel;
    private final UserService userService;

    public boolean login(String email, char[] password) {
        return switch (userService.login(email, password)) {
            case 200 -> true;
            case 401 -> false; //No role
            case 403 -> false; //bad credentials
            case 500, 504 -> false; //server problem
            default -> false;
        };
    }

    public void logout() {
        userService.logout();
    }

    private boolean validateFields(JTextField emailField,
            JPasswordField passwordField,
            JPasswordField rePasswordField) {
        boolean valid = true;

        if (emailField.getText().isBlank() || !emailField.getText().contains("@")) {
            //emailError.setVisible(true);
            valid = false;
        } else {
            //emailError.setVisible(false);
        }

        if (new String(passwordField.getPassword()).length() < 8) {
            //passwordError.setVisible(true);
            valid = false;
        } else {
            //passwordError.setVisible(false);
        }

        if (!Arrays.equals(passwordField.getPassword(), rePasswordField.getPassword())) {
            //rePasswordError.setVisible(true);
            valid = false;
        } else {
            //rePasswordError.setVisible(false);
        }

//        view.revalidate();
//        view.repaint();
        return valid;
    }

}
