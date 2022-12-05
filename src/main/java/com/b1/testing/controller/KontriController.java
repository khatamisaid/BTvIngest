package com.b1.testing.controller;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    @RequestMapping(value = "/post", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE }, produces = APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map> postMateri(@RequestParam String judul,
            @RequestParam String reporter, @RequestParam String lok_liputan,
            @RequestParam String deskripsi, @RequestPart("files") MultipartFile files)
            throws IllegalStateException, JsonProcessingException {
        Map data = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        String ddMMyyyy = sdf.format(new Date());
        String namafile = "";
        String originalExtension = "";
        Kontri kontri = new Kontri();
        kontri.setDeskripsi(deskripsi);
        kontri.setJudul(judul);
        kontri.setLokLiputan(lok_liputan);
        kontri.setReporter(reporter);
        kontriRepository.save(kontri);
        try {
            String[] arrSplit = files.getOriginalFilename().split("\\.");
            originalExtension = arrSplit[arrSplit.length - 1];
            namafile = ddMMyyyy + "_" + judul + "_" + reporter + "_" + lok_liputan
                    + "."
                    + originalExtension;
                    ftpClientConnection.uploadFile(files, "/kontri/" + namafile);
            // String fullPathFile = env.getProperty("URL.FILE_IN") + "/"
            //         + Base64.encodeBase64(httpSession.getAttribute("username").toString().getBytes());
            // File dir = new File(fullPathFile);
            // if (!dir.exists())
            //     dir.mkdirs();
            // files.transferTo(new File(fullPathFile + "/" + namafile));
            Video video = new Video();
            video.setIdKontri(kontri.getIdKontri());
            video.setIpLocation(env.getProperty("FTP.REMOTE_HOST"));
            video.setFilename(namafile);
            video.setOriginalExtension(originalExtension);
            video.setTranscodeExtension("mp4");
            videoRepository.save(video);
        } catch (NullPointerException e) {
            data.put("icon", "error");
            data.put("message", e.getMessage());
            return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
        }
        // System.out.println(files[0].getOriginalFilename());
        // for (int i = 0; i < files.length; i++) {

        // }
        ObjectMapper mapper = new ObjectMapper();
        logRepository.save(new Log(null, "upload", httpSession.getAttribute("username").toString(),
                mapper.writeValueAsString(kontri.getListVideo())));
        data.put("icon", "success");
        data.put("message", "data berhasil di insert");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
