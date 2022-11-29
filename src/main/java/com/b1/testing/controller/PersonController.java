package com.b1.testing.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.b1.testing.entity.Person;
import com.b1.testing.repository.PersonRepository;
import com.b1.testing.repository.RoleRepository;
import com.b1.testing.viewmodel.PersonViewModel;

@Controller
@RequestMapping(value = "/person")
public class PersonController {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<Map> all() {
        Map data = new HashMap<>();
        data.put("data", personRepository.findAll());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @RequestMapping(value = "/paging", method = RequestMethod.GET)
    public ResponseEntity<Map> paging(@RequestParam(defaultValue = "0") Integer start,
    @RequestParam(defaultValue = "5") Integer length) {
        Map data = new HashMap<>();
        Pageable pageable = PageRequest.of(start, length);
        Page<Person> dataPaging = personRepository.findAll(pageable);
        data.put("data", dataPaging);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @RequestMapping(value = "/by", method = RequestMethod.GET)
    public ResponseEntity<Map> by(@RequestParam Integer id) {
        Map data = new HashMap<>();
        data.put("data", personRepository.findById(id));
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<Map> post(@RequestBody PersonViewModel person) {
        Map data = new HashMap<>();
        if (personRepository.findByUsername(person.getUsername()).size() > 0) {
            data.put("message", "username sudah ada");
            data.put("icon", "warning");
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }
        Person personTemp = new Person(0, person.getUsername(), encoder.encode(person.getPassword()), person.getEmail(),
                roleRepository.findById(person.getRole()).get());
        personRepository.save(personTemp);
        data.put("icon", "success");
        data.put("message", "Sukses Insert Person");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @RequestMapping(value = "/put", method = RequestMethod.PUT)
    public ResponseEntity<Map> put(@RequestBody Person person) {
        Map data = new HashMap<>();
        if (!personRepository.existsById(person.getIdPerson())) {
            data.put("message", "Data Tidak ada");
            return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
        }
        personRepository.save(person);
        data.put("message", "Sukses Insert Person");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @RequestMapping(value = "/del", method = RequestMethod.DELETE)
    public ResponseEntity<Map> delById(@RequestParam Integer id) {
        Map data = new HashMap<>();
        if (!personRepository.existsById(id)) {
            data.put("message", "Data Tidak ada");
            return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
        }
        personRepository.deleteById(id);
        data.put("message", "Sukses Menghapus Person");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
