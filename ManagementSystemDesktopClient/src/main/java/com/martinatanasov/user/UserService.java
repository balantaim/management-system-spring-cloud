package com.martinatanasov.user;

public interface UserService {

    int login(String email, char[] password);

    void getInfo();

    void logout();

}
