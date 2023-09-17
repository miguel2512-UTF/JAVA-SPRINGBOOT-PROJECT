package com.project.javaproject.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.javaproject.models.User;
import com.project.javaproject.services.LoginService;
import com.project.javaproject.utils.ApiResponse;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ApiResponse login(@RequestBody User user) {
        Map<String, Object> body = new HashMap<String, Object>();
        if (!loginService.authenticateUser(user)) {
            body.put("message", "Username or password incorrects");
            return ApiResponse.response(false, body, HttpStatus.UNAUTHORIZED);
        }

        String token = loginService.generateToken(user);
        
        body.put("auth_token", token);
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    // private static String decode(String encodedString) {
    //     return new String(Base64.getUrlDecoder().decode(encodedString));
    // }

    // private static Claims getClaimsFromToken(String token) {
    //     return Jwts.parser().setSigningKey(KEY).parseClaimsJws(token).getBody();
    // }
}
