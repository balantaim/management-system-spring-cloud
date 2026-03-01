package com.martinatanasov.user;

public interface UserService {

    Integer login(String email, char[] password);

    void getInfo();

    void logout();

}
