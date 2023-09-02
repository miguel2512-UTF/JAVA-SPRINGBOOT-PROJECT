package com.project.javaproject.services;

import java.util.Base64;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.javaproject.interfaces.IUserService;
import com.project.javaproject.models.User;
import com.project.javaproject.security.PasswordEncoder;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;

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

        if (PasswordEncoder.match(user.getPassword(), isUserFound.getPassword())) {
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

    public Boolean isAuthenticated(HttpServletRequest request) {
        String token = request.getHeader("authorization").split(" ")[1];
        String tokenError = validateToken(token);

        if (tokenError != null) {
            return false;
        }

        return true;
    }

    public String getToken(HttpServletRequest request) {
        return request.getHeader("authorization");
    }

    public User getUserSession(HttpServletRequest request) {
        String token = getToken(request).split(" ")[1];
        String email = (String) decode(token.split("\\.")[1]).get("email");
        
        User user = userService.getUserByEmail(email);
        return user;
    }

    private static JSONObject decode(String encodedString) {
        String decodedString = new String(Base64.getUrlDecoder().decode(encodedString));
        return new JSONObject(decodedString);
    }
}
