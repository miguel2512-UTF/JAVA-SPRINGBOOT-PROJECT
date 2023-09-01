package com.project.javaproject.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class ApiResponse extends ResponseEntity<Map<String, Object>> {

    public ApiResponse(Map<String, Object> body, HttpStatusCode status) {
        super(body, status);
    }

    public static ApiResponse response(Boolean success, Map<String, Object> body, HttpStatusCode status) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", success);
        res.put("body", body);

        return new ApiResponse(res, status);
    }
}
