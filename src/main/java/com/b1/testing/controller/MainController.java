package com.b1.testing.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.b1.testing.entity.Ingest;
import com.b1.testing.entity.Kontri;
import com.b1.testing.entity.Log;
import com.b1.testing.entity.Role;
import com.b1.testing.entity.Video;
import com.b1.testing.repository.IngestRepository;
import com.b1.testing.repository.KontriRepository;
import com.b1.testing.repository.LogRepository;
import com.b1.testing.repository.PersonRepository;
import com.b1.testing.repository.RoleRepository;
import com.b1.testing.repository.VideoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class MainController {

    @Autowired
    private Environment env;

    @Autowired
    private IngestRepository ingestRepository;

    @Autowired
    private KontriRepository kontriRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private HttpSession httpSession;

    @GetMapping(value = "/")
    public String index(Model model) {
        List<Role> roles = roleRepository.findAll();
        roles.remove(0);
        model.addAttribute("roles", roles);
        if (httpSession.getAttribute("role").toString().equalsIgnoreCase("kontri")) {
            return "indexUserKontri";
        }
        return "index";
    }

    @GetMapping(value = "/findAll/{path}")
    public ResponseEntity<Map> findAll(@PathVariable(required = true) String path, @RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "5") Integer length) {
        Map data = new HashMap<>();
        if(path.equalsIgnoreCase("ingest")){
            Pageable pageable = PageRequest.of(start, length);
            Page<Ingest> dataPaging = ingestRepository.findAll(pageable);
            data.put("data", dataPaging);
        }else if(path.equalsIgnoreCase("kontri")){
            Pageable pageable = PageRequest.of(start, length);
            Page<Kontri> dataPaging = kontriRepository.findAll(pageable);
            data.put("data", dataPaging);
        }
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping(value = "/find/{path}")
    public ResponseEntity<Map> find(@PathVariable(required = true) String path, @RequestParam(required = true) Integer id) throws JsonProcessingException {
        Map data = new HashMap<>();
        if(path.equalsIgnoreCase("ingest")){
            if (!ingestRepository.existsById(id)) {
                data.put("message", "Data Tidak Ditemukan");
                return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
            }
            Ingest ingest = ingestRepository.findById(id).get();
            data.put("data", ingest);
            ObjectMapper mapper = new ObjectMapper();
            logRepository
                    .save(new Log(null, "priview", httpSession.getAttribute("username").toString(),
                            mapper.writeValueAsString(ingest.getListVideo())));
        }else if(path.equalsIgnoreCase("kontri")){
            if (!kontriRepository.existsById(id)) {
                data.put("message", "Data Tidak Ditemukan");
                return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
            }
            Kontri kontri = kontriRepository.findById(id).get();
            data.put("data", kontri);
            ObjectMapper mapper = new ObjectMapper();
            logRepository
                    .save(new Log(null, "priview", httpSession.getAttribute("username").toString(),
                            mapper.writeValueAsString(kontri.getListVideo())));
        }
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping(value = "/download/{namafile}")
    @ResponseBody
    public ResponseEntity<byte[]> download(@PathVariable(required = true) String namafile) throws IOException {
        String fileName = namafile + env.getProperty("EXTENSION.FILE_DOWNLOAD");
        File file = new File(env.getProperty("URL.DOWNLOAD_HI_RES") + "/" + fileName);
        byte[] fileContent = null;
        try {
            fileContent = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
        logRepository
                .save(new Log(null, "download", httpSession.getAttribute("username").toString(), fileName));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(fileContent);
    }

    @DeleteMapping(value = "/materi")
    public ResponseEntity<Map> deleteMateri(@RequestParam(required = true) Integer id) {
        Map data = new HashMap<>();
        if (!ingestRepository.existsById(id)) {
            data.put("message", "Data Tidak Ditemukan");
            return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
        }
        ingestRepository.deleteById(id);
        data.put("message", "Data Materi Berhasil di hapus");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @RequestMapping(value = "/postMateri", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE }, produces = APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map> postMateri(@RequestParam String judul, @RequestParam Integer no_tape,
            @RequestParam String reporter, @RequestParam String tim_liputan, @RequestParam String lok_liputan,
            @RequestParam String deskripsi, @RequestParam MultipartFile files)
            throws JsonProcessingException {
        Map data = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        String ddMMyyyy = sdf.format(new Date());
        String namafile = "";
        String originalExtension = "";
        Ingest dbIngest = new Ingest();
        dbIngest.setDeskripsi(deskripsi);
        dbIngest.setJudul(judul);
        dbIngest.setLokLiputan(lok_liputan);
        dbIngest.setNoTape(no_tape);
        dbIngest.setReporter(reporter);
        dbIngest.setTimLiputan(tim_liputan);
        ingestRepository.save(dbIngest);
        try {
            String[] arrSplit = files.getOriginalFilename().split("\\.");
            originalExtension = arrSplit[arrSplit.length - 1];
            namafile = ddMMyyyy + "_" + judul + "_" + reporter + "_" + tim_liputan + "_" + lok_liputan + "_"
                    + "."
                    + originalExtension;
            // String base64Folder = Base64.encodeBase64(httpSession.getAttribute("username").toString().getBytes())
            //         .toString();
            // String fullPathFile = env.getProperty("URL.FILE_IN") + "/"
            //         + base64Folder;
            // File dir = new File(fullPathFile);
            // if (!dir.exists())
            //     dir.mkdirs();
            files.transferTo(new File(env.getProperty("URL.FILE_IN") + "/" + namafile));
            Video video = new Video();
            video.setIdIngest(dbIngest.getIdIngest());
            video.setIpLocation("192.168.100.90");
            video.setFilename(namafile);
            video.setFoldername(null);
            video.setOriginalExtension(originalExtension);
            video.setTranscodeExtension("mp4");
            videoRepository.save(video);
        } catch (IOException | NullPointerException e) {
            data.put("icon", "error");
            data.put("message", e.getMessage());
            return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
        }
        ObjectMapper mapper = new ObjectMapper();
        logRepository.save(new Log(null, "upload", httpSession.getAttribute("username").toString(),
                mapper.writeValueAsString(dbIngest.getListVideo())));
        data.put("icon", "success");
        data.put("message", "data berhasil di insert");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping(value = "/log")
    public ResponseEntity<Map> log(@RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "5") Integer length) {
        Map data = new HashMap<>();
        Pageable pageable = PageRequest.of(start, length, Sort.by("createdAt").descending());
        Page<Log> dataPaging = logRepository.findAll(pageable);
        data.put("data", dataPaging);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
