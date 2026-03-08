package com.martinatanasov.user;

public interface UserService {

    int login(String email, String password);

    void getInfo();

    void logout();

    int register(String email, String fullName, String password);

}
