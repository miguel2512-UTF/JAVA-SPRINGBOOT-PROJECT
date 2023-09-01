package com.project.javaproject.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.project.javaproject.utils.ApiResponse;

@RestController
public class indexController {

    @GetMapping("/")
    public ApiResponse home(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("message", "Hello World!");
        return ApiResponse.response(true, body, HttpStatusCode.valueOf(200));
    }
}
