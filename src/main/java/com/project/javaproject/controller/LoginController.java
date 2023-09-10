package com.project.javaproject.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.javaproject.models.User;
import com.project.javaproject.services.LoginService;
import com.project.javaproject.utils.ApiResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class LoginController {

    public static final String KEY = "SECRET_KEY";

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ApiResponse login(@RequestBody User user) {
        Map<String, Object> body = new HashMap<String, Object>();
        if (!loginService.authenticateUser(user)) {
            body.put("message", "Username or password incorrects");
            return ApiResponse.response(false, body, HttpStatus.UNAUTHORIZED);
        }

        String token = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, KEY)
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 900000))
                .compact();
        
        body.put("auth_token", token);
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ApiResponse logout(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> body = new HashMap<String, Object>();

        request.getSession().invalidate();

        body.put("message", "logout successfully");
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    // private static String decode(String encodedString) {
    //     return new String(Base64.getUrlDecoder().decode(encodedString));
    // }

    // private static Claims getClaimsFromToken(String token) {
    //     return Jwts.parser().setSigningKey(KEY).parseClaimsJws(token).getBody();
    // }
}
