package com.b1.testing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import com.b1.testing.service.VideoStreamService;

@RestController
@RequestMapping(value = "/video")
public class VideoStreamController {

    @Autowired
    private VideoStreamService videoStreamService;

    @RequestMapping(value = "/stream/{fileType}/{fileName}", method = RequestMethod.GET)
    public Mono<ResponseEntity<byte[]>> streamVideo(
            @RequestHeader(value = "Range", required = false) String httpRangeList,
            @PathVariable("fileType") String fileType,
            // @PathVariable("path") String path,
            @PathVariable("fileName") String fileName) {
        return Mono.just(videoStreamService.prepareContent(fileName, fileType, httpRangeList));
    }
}
