package com.project.javaproject.controller;

import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.javaproject.models.User;
import com.project.javaproject.services.LoginService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping("/login")
public class LoginController {

    public static final String KEY = "SECRET_KEY";

    @Autowired
    private LoginService loginService;

    @PostMapping("")
    public ResponseEntity<Object> login(@RequestBody User user) {
        if (!loginService.authenticateUser(user)) {
            return new ResponseEntity<Object>("Username or password incorrects", HttpStatus.UNAUTHORIZED);
        }

        String token = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, KEY)
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 900000))
                .compact();

        System.out.println(decode(token.split("\\.")[1]));
        System.out.println(validateToken(token));
        
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

    private static Boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // private static Claims getClaimsFromToken(String token) {
    //     return Jwts.parser().setSigningKey(KEY).parseClaimsJws(token).getBody();
    // }
}
