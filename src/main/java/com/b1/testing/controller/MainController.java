package com.b1.testing.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.b1.testing.entity.DbIngest;
import com.b1.testing.entity.Log;
import com.b1.testing.entity.Person;
import com.b1.testing.entity.Role;
import com.b1.testing.repository.DbIngestRepository;
import com.b1.testing.repository.LogRepository;
import com.b1.testing.repository.PersonRepository;
import com.b1.testing.repository.RoleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class MainController {

    @Autowired
    private Environment env;

    @Autowired
    private DbIngestRepository dbIngestRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private HttpSession httpSession;

    @GetMapping(value = "/")
    public String index(Model model) {
        List<Role> roles = roleRepository.findAll();
        roles.remove(0);
        model.addAttribute("roles", roles);
        return "index";
    }

    @GetMapping(value = "/findAll")
    public ResponseEntity<Map> findAll(@RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "5") Integer length) {
        Map data = new HashMap<>();
        Pageable pageable = PageRequest.of(start, length);
        Page<DbIngest> dataPaging = dbIngestRepository.findAll(pageable);
        data.put("data", dataPaging);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping(value = "/find")
    public ResponseEntity<Map> find(@RequestParam(required = true) Integer id) {
        Map data = new HashMap<>();
        if (!dbIngestRepository.existsById(id)) {
            data.put("message", "Data Tidak Ditemukan");
            return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
        }
        data.put("data", dbIngestRepository.findById(id).get());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping(value = "/download/{namafile}")
    @ResponseBody
    public ResponseEntity<byte[]> download(@PathVariable(required = true) String namafile) throws IOException {
        File file = new File(env.getProperty("URL.DOWNLOAD_HI_RES") + "/" + namafile + ".mxf");
        byte[] fileContent = null;
        try {
            fileContent = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + namafile + ".mxf" + "\"")
                .body(fileContent);
    }

    @DeleteMapping(value = "/materi")
    public ResponseEntity<Map> deleteMateri(@RequestParam(required = true) Integer id) {
        Map data = new HashMap<>();
        if (!dbIngestRepository.existsById(id)) {
            data.put("message", "Data Tidak Ditemukan");
            return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
        }
        dbIngestRepository.deleteById(id);
        data.put("message", "Data Materi Berhasil di hapus");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @RequestMapping(value = "/postMateri", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE }, produces = APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map> postMateri(@RequestParam String judul, @RequestParam Integer no_tape,
            @RequestParam String reporter, @RequestParam String tim_liputan, @RequestParam String lok_liputan,
            @RequestParam String deskripsi, @RequestParam MultipartFile file) throws JsonProcessingException {
        Map data = new HashMap<>();
        String namafile = "";
        String originalExtension = "";
        try {
            String[] arrSplit = file.getOriginalFilename().split("\\.");
            originalExtension = arrSplit[arrSplit.length - 1];
            namafile = judul + "." + originalExtension;
            file.transferTo(new File(env.getProperty("URL.FILE_IN") + "/" + namafile));
        } catch (IOException | NullPointerException e) {
            data.put("icon", "error");
            data.put("message", e.getMessage());
            return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
        }
        DbIngest dbIngest = new DbIngest();
        dbIngest.setDeskripsi(deskripsi);
        dbIngest.setFiles(namafile);
        dbIngest.setJudul(judul);
        dbIngest.setLokLiputan(lok_liputan);
        dbIngest.setNoTape(no_tape);
        dbIngest.setReporter(reporter);
        dbIngest.setTimLiputan(tim_liputan);
        dbIngest.setTranscodeExtension("mp4");
        dbIngest.setOriginalExtension(originalExtension);
        dbIngestRepository.save(dbIngest);
        String json = new ObjectMapper().writeValueAsString(dbIngest);
        System.out.println(json);
        Person person = personRepository.findById(Integer.parseInt(httpSession.getAttribute("id").toString())).get();
        logRepository.save(new Log(null, "upload", json, person));
        data.put("icon", "success");
        data.put("message", "data berhasil di insert");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping(value = "/log")
    public ResponseEntity<Map> log(@RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "5") Integer length) {
        Map data = new HashMap<>();
        Pageable pageable = PageRequest.of(start, length);
        Page<Log> dataPaging = logRepository.findAll(pageable);
        data.put("data", dataPaging);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @RequestMapping(value = "/insertRole", method = RequestMethod.GET)
    public ResponseEntity<Map> insertRole(Model model) {
        Map data = new HashMap<>();
        roleRepository.save(new Role(0, "Administrator", "Hak Akses Menyeluruh"));
        roleRepository.save(new Role(0, "User", "Hak Akses Dibatasi"));
        data.put("message", "Berhasil create role");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
