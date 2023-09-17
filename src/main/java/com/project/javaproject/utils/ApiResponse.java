package com.project.javaproject.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletResponse;

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

    public static void responseUnauthorized(HttpServletResponse response, String message) throws IOException {
		response.setStatus(401);
        response.setContentType("application/json");
		
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("message", message);

		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("success", false);
		jsonResponse.put("body", body);
		response.getOutputStream().print(jsonResponse.toString());
        response.getOutputStream().close();
	}
}
