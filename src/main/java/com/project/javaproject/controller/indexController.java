package com.project.javaproject.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class indexController {

    @GetMapping("/")
    public ResponseEntity<String> home(HttpServletRequest request, HttpServletResponse response) {
        return new ResponseEntity<>("Hello World!", HttpStatusCode.valueOf(200));
    }
}
