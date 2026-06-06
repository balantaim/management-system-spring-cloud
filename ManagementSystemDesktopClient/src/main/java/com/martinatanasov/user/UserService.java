package com.martinatanasov.user;

public interface UserService {

    int login(String email, String password);

    int refreshAccessToken();

    void logout();

    int register(String email, String fullName, String password);

}
