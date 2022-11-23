package com.b1.testing.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.b1.testing.entity.Role;
import com.b1.testing.repository.RoleRepository;

@Controller
@RequestMapping(value = "/role")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<Map> all() {
        Map data = new HashMap<>();
        data.put("data", roleRepository.findAll());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @RequestMapping(value = "/by", method = RequestMethod.GET)
    public ResponseEntity<Map> by(@RequestParam String key, @RequestParam String value) {
        Map data = new HashMap<>();
        Role role = new Role();
        if(key.equalsIgnoreCase("id")){
            Integer id = 0;
            try{
                id = Integer.valueOf(value);
            }catch(NumberFormatException  e){
                data.put("message", "Invalid key or value");
                return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
            }
            role.setIdRole(id);
        }else if(key.equalsIgnoreCase("rolename")){
            role.setRoleName(value);
        }
        data.put("data", roleRepository.findAll(Example.of(role)));
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<Map> post(@RequestBody Role role) {
        Map data = new HashMap<>();
        roleRepository.save(role);
        data.put("message", "Sukses Insert Role");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @RequestMapping(value = "/put", method = RequestMethod.PUT)
    public ResponseEntity<Map> put(@RequestBody Role role) {
        Map data = new HashMap<>();
        if (!roleRepository.existsById(role.getIdRole())) {
            data.put("message", "Data Tidak ada");
            return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
        }
        roleRepository.save(role);
        data.put("message", "Sukses Insert Role");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @RequestMapping(value = "/del", method = RequestMethod.DELETE)
    public ResponseEntity<Map> delById(@RequestParam Integer id) {
        Map data = new HashMap<>();
        if (!roleRepository.existsById(id)) {
            data.put("message", "Data Tidak ada");
            return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
        }
        roleRepository.deleteById(id);
        data.put("message", "Sukses Menghapus Role");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
