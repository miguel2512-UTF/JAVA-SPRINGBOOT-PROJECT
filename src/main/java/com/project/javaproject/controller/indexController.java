package com.project.javaproject.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class indexController {
    
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return new ResponseEntity<>("Hello World!", HttpStatusCode.valueOf(200));
    }
}
