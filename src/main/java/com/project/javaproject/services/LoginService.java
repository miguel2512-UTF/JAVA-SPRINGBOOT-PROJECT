package com.project.javaproject.services;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.javaproject.interfaces.IUserService;
import com.project.javaproject.models.User;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class LoginService {

    public static final String KEY = "SECRET_KEY";

    @Autowired
    private IUserService userService;
    
    public Boolean authenticateUser(User user) {
        User isUserFound = userService.getUserByEmail(user.getEmail());
        if (isUserFound == null) {
            return false;
        }

        if (user.getPassword().equals(isUserFound.getPassword())) {
            return true;
        }

        return false;
    }

    public String validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(KEY).parseClaimsJws(token);
            return null;
        } catch (ExpiredJwtException e) {
            return "The token has expired";
        } catch (Exception e) {
            return "The token is invalid";
        }
    }

    public void isAuthenticated(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader("authorization").split(" ")[1];
        String tokenError = validateToken(token);

        System.out.println(tokenError);

        if (tokenError != null) {
            PrintWriter out = response.getWriter();
            response.setStatus(403);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.println(tokenError);
            out.close();
        }
    }

    public Boolean isAuthenticated(HttpServletRequest request) {
        String token = request.getHeader("authorization").split(" ")[1];
        String tokenError = validateToken(token);

        if (tokenError != null) {
            return false;
        }

        return true;
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("authorization");

        if (authHeader == null) {
            return null;
        }

        return authHeader;
    }
}
