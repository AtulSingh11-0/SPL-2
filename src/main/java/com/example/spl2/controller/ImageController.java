package com.example.spl2.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class ImageController {

    @GetMapping(value = "/images/img.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> getImage() throws IOException {
        Resource img = new ClassPathResource("templates/img.png");
        if (!img.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentLength(img.contentLength()).contentType(MediaType.IMAGE_PNG).body(img);
    }
}

