package com.martinatanasov.user;

import com.martinatanasov.requests.ResponseStatus;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.regex.Pattern;

@RequiredArgsConstructor
@Singleton
public class UserController {

    private final UserService userService;

    public ResponseStatus login(String email, char[] password) {
        return switch (userService.login(email, new String(password))) {
            case 200 -> ResponseStatus.SUCCESS; //Login success
            case 401 -> ResponseStatus.UNAUTHORIZED_ACCESS; //No role
            case 403 -> ResponseStatus.INVALID_CREDENTIALS; //Bad credentials
            case 408 -> ResponseStatus.TIMEOUT; //Timeout
            case 423 -> ResponseStatus.ACCOUNT_LOCKED; //Account locked
            case 429 -> ResponseStatus.TOO_MANY_REQUESTS; //Too many requests
            case 500, 503, 504 -> ResponseStatus.SERVER_ERROR; //Server problem
            default -> ResponseStatus.UNKNOWN_ERROR;
        };
    }

    public ResponseStatus register(String email, String fullName, String password) {
        return switch (userService.register(email, fullName, password)) {
            case 201 -> ResponseStatus.RESOURCE_CREATED; //User created
            case 400 -> ResponseStatus.BAD_REQUEST; //Bad request
            case 408 -> ResponseStatus.TIMEOUT; //Timeout
            case 409 -> ResponseStatus.RESOURCE_ALREADY_EXIST; //User already exists
            case 423 -> ResponseStatus.ACCOUNT_LOCKED; //Account locked
            case 429 -> ResponseStatus.TOO_MANY_REQUESTS; //Too many requests
            case 500, 503, 504 -> ResponseStatus.SERVER_ERROR; //Server problem
            default -> ResponseStatus.UNKNOWN_ERROR;
        };
    }

    public void logout() {
        userService.logout();
    }

    public boolean isFullNameValid(String fullName) {
        Pattern FULL_NAME_PATTERN = Pattern.compile("^[A-Za-z]+(?: [A-Za-z]+)*$");
        return fullName != null && FULL_NAME_PATTERN.matcher(fullName).matches() && fullName.length() >= 2 && fullName.length() <= 150;
    }

    public boolean isPasswordsEqual(String userPassword, String rePassword) {
        return userPassword.equals(rePassword);
    }

    public boolean isEmailValid(String email) {
        Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isPasswordValid(String userPassword) {
        Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,50}$");
        return userPassword != null && PASSWORD_PATTERN.matcher(userPassword).matches();
    }

}
