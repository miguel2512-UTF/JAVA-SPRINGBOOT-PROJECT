package com.project.javaproject.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.javaproject.services.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class indexController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/")
    public ResponseEntity<String> home(HttpServletRequest request, HttpServletResponse response) throws IOException {
        loginService.isAuthenticated(request, response);

        return new ResponseEntity<>("Hello World!", HttpStatusCode.valueOf(200));
    }
}
