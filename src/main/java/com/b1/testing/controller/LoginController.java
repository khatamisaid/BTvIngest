package com.b1.testing.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.b1.testing.entity.Person;
import com.b1.testing.entity.Role;
import com.b1.testing.repository.PersonRepository;
import com.b1.testing.repository.RoleRepository;

@Controller
public class LoginController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) throws Exception {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        return "login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ResponseEntity<Map> register(Model model) {
        Map data = new HashMap<>();
        personRepository.save(new Person(0, "said", encoder.encode("said123"), "khatamisaid@gmail.com",
                roleRepository.findById(1).get()));
        data.put("message", "Berhasil create user");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @RequestMapping(value = "/insertRole", method = RequestMethod.GET)
    public ResponseEntity<Map> insertRole(Model model) {
        Map data = new HashMap<>();
        roleRepository.save(new Role(0, "Administrator", "Hak Akses Menyeluruh"));
        roleRepository.save(new Role(0, "User", "Hak Akses Dibatasi"));
        data.put("message", "Berhasil create user");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }
}
