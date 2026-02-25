package com.martinatanasov.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

	public boolean login(String email, char[] password) {
        log.info("User: {}, and pass: {}", email, password);
		return true;
	}

}
