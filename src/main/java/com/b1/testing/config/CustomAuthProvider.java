package com.b1.testing.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.b1.testing.entity.Person;
import com.b1.testing.repository.PersonRepository;

@Component
public class CustomAuthProvider implements AuthenticationProvider {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private PersonRepository personRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        Person personTemp = new Person();
        personTemp.setUsername(username);
        Person peson = personRepository.findOne(Example.of(personTemp)).get();
        System.out.println(encoder.matches(password, peson.getPassword()));
        System.out.println("username: "+username+" password: "+password);
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // TODO Auto-generated method stub
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
