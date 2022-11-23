package com.b1.testing.config;

import org.springframework.security.crypto.password.PasswordEncoder;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordEncode implements PasswordEncoder{

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toString().toCharArray(), encodedPassword);
        return result.verified;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return BCrypt.withDefaults().hashToString(12, rawPassword.toString().toCharArray());
    }
    
}
