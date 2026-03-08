package com.martinatanasov.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class UserController {

    private final UserService userService;

    public boolean login(String email, char[] password) {
        return switch (userService.login(email, new String(password))) {
            case 200 -> true;
            case 401 -> false; //No role
            case 403 -> false; //bad credentials
            case 500, 504 -> false; //server problem
            default -> false;
        };
    }

    public boolean register(String email, String fullName, String password) {
        return switch (userService.register(email, fullName, password)) {
            case 201 -> true;
            case 401 -> false; //No role
            case 403 -> false; //bad credentials
            case 500, 504 -> false; //server problem
            default -> false;
        };
    }

    public boolean isFullNameValid(String fullName) {
        Pattern FULL_NAME_PATTERN = Pattern.compile("^[A-Za-z]+(?: [A-Za-z]+)*$");
        return fullName != null && FULL_NAME_PATTERN.matcher(fullName).matches() && fullName.length() >= 2 && fullName.length() <= 150;
    }

    public boolean isPasswordsEqual(String userPassword, String rePassword) {
        return userPassword.equals(rePassword);
    }

    public boolean isEmailValid(String email) {
        Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isPasswordValid(String userPassword) {
        Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,50}$");
        return userPassword != null && PASSWORD_PATTERN.matcher(userPassword).matches();
    }

    public void logout() {
        userService.logout();
    }

}
