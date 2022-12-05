package com.b1.testing.controller;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.b1.testing.entity.Kontri;
import com.b1.testing.entity.Log;
import com.b1.testing.entity.Video;
import com.b1.testing.repository.KontriRepository;
import com.b1.testing.repository.LogRepository;
import com.b1.testing.repository.VideoRepository;
import com.b1.testing.util.FTPClientConnection;
import com.b1.testing.util.FileManager;
import com.b1.testing.viewmodel.KontriViewModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/kontri")
public class KontriController {

    @Autowired
    private KontriRepository kontriRepository;

    @Autowired
    private Environment env;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private FileManager fileManager;

    @Autowired
    private FTPClientConnection ftpClientConnection;

    @RequestMapping(value = "/postVideo", consumes = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE }, method = RequestMethod.POST)
    public Mono<String> postVideo(@RequestPart KontriViewModel request, @RequestPart Flux<FilePart> files) {
        Map data = new HashMap<>();
        data.put("request", request);
        return files.flatMap(it -> it.transferTo(Paths.get(env.getProperty("URL.FILE_IN") + it.filename())))
                .then(Mono.just("OK"));
    }

    @RequestMapping(value = "/postListVideo", consumes = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE }, method = RequestMethod.POST)
    public ResponseEntity<Map> postListVideo(@RequestPart("files") MultipartFile[] files) {
        Map data = new HashMap<>();
        Arrays.asList(files).forEach(file -> System.out.println(file.getOriginalFilename()));
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    private void saveVideo(Kontri kontri, MultipartFile file) {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        String ddMMyyyy = sdf.format(new Date());
        String namafile = "";
        String originalExtension = "";
        String arrSplit[] = file.getOriginalFilename().split("\\.");
        originalExtension = arrSplit[arrSplit.length - 1];
        namafile = ddMMyyyy + "_" + kontri.getJudul() + "_" + kontri.getReporter() + "_" + kontri.getLokLiputan() + "_"
                + (new Random().nextInt(99999))
                + "."
                + originalExtension;
        try {
            file.transferTo(new File(env.getProperty("URL.FILE_IN") + "/" + namafile));
            Video video = new Video();
            video.setIdKontri(kontri.getIdKontri());
            video.setIpLocation(env.getProperty("FTP.REMOTE_HOST"));
            video.setFilename(namafile);
            video.setOriginalExtension(originalExtension);
            video.setTranscodeExtension("mp4");
            videoRepository.save(video);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @RequestMapping(value = "/post", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE }, produces = APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map> postMateri(@RequestParam String judul,
            @RequestParam String reporter, @RequestParam String lok_liputan,
            @RequestParam String deskripsi, @RequestPart("files") MultipartFile[] files)
            throws IllegalStateException {
        Map data = new HashMap<>();

        Kontri kontri = new Kontri();
        kontri.setDeskripsi(deskripsi);
        kontri.setJudul(judul);
        kontri.setLokLiputan(lok_liputan);
        kontri.setReporter(reporter);
        kontriRepository.save(kontri);
        try {
            //ftpClientConnection.uploadFile(files[i], namafile);
            Arrays.asList(files)
                    .forEach(file -> saveVideo(kontri, file));
        } catch (NullPointerException e) {
            data.put("icon", "error");
            data.put("message", e.getMessage());
            return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
        }
        // ObjectMapper mapper = new ObjectMapper();
        // logRepository.save(new Log(null, "upload",
        // httpSession.getAttribute("username").toString(),
        // mapper.writeValueAsString(kontri.getListVideo())));
        data.put("icon", "success");
        data.put("message", "data berhasil di insert");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
