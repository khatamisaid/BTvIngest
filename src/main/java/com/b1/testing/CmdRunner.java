package com.b1.testing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.b1.testing.entity.Person;
import com.b1.testing.entity.Role;
import com.b1.testing.repository.PersonRepository;
import com.b1.testing.repository.RoleRepository;

@Component
public class CmdRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CmdRunner.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        Role adminRole = new Role(1, "Administrator", "Hak Akses User Management");
        Role produserRole = new Role(2, "Produser", "Hak Akses Priview");
        Role editorRole = new Role(3, "Editor", "Hak Akses Download dan Priview");
        Role uploaderRole = new Role(4, "Uploader", "Hak Akses Upload dan Priview");
        Role libraryRole = new Role(5, "Library", "Hak Akses Upload, Download dan Priview");
        roleRepository.save(adminRole);
        roleRepository.save(produserRole);
        roleRepository.save(editorRole);
        roleRepository.save(uploaderRole);
        roleRepository.save(libraryRole);
        personRepository
                .save(new Person(1, "Administrator", encoder.encode("admin123"), "admin@it-berita1.com", adminRole));
        personRepository
                .save(new Person(2, "Produser", encoder.encode("produser123"), "produser@berita1.com", produserRole));
        personRepository.save(new Person(3, "Editor", encoder.encode("editor123"), "editor@berita1.com", editorRole));
        personRepository
                .save(new Person(4, "Uploader", encoder.encode("uploader123"), "uploader@berita1.com", uploaderRole));
        personRepository
                .save(new Person(5, "Library", encoder.encode("library123"), "library@berita1.com", libraryRole));
        logger.info("Person and Role Has been created");
    }
}