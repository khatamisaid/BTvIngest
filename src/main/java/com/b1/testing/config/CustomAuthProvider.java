package com.b1.testing.config;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.b1.testing.entity.Person;
import com.b1.testing.entity.Role;
import com.b1.testing.repository.PersonRepository;
import com.b1.testing.repository.RoleRepository;

@Component
public class CustomAuthProvider implements AuthenticationProvider {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private HttpSession httpSession;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        Person personTemp = new Person();
        personTemp.setUsername(username);
        Person person = new Person();
        try{
            person = personRepository.findOne(Example.of(personTemp)).get();
        }catch(NoSuchElementException e){
            throw new BadCredentialsException("Username Tidak ditemukan");
        }
        
        if (encoder.matches(password, person.getPassword())) {
            httpSession.setAttribute("id", person.getIdPerson());
            httpSession.setAttribute("username", person.getUsername());
            httpSession.setAttribute("email", person.getEmail());
            httpSession.setAttribute("role", person.getRole().getRoleName());
        } else {
            throw new BadCredentialsException("Username/Password Salah");
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList();
        grantedAuthorities.add(new SimpleGrantedAuthority((String) httpSession.getAttribute("role")));
        return new UsernamePasswordAuthenticationToken(username, password, grantedAuthorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
