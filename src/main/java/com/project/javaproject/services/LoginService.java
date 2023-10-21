package com.project.javaproject.services;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.javaproject.interfaces.IUserService;
import com.project.javaproject.models.User;
import com.project.javaproject.security.PasswordEncoder;
import com.project.javaproject.security.PermissionsConfig;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class LoginService {

    public static final String KEY = System.getenv("SECRET_KEY");
    public static final int TOKEN_DURATION_HOURS = 3; 

    @Autowired
    private IUserService userService;

    public String generateToken(User user) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, KEY)
                .claim("email", user.getEmail())
                .claim("role", user.getRoleName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (TOKEN_DURATION_HOURS * 3600000)))
                .compact();
    }

    public String authenticateUser(User user) {
        User isUserFound = userService.getUserByEmail(user.getEmail());
        if (isUserFound == null) {
            return null;
        }

        if (!PasswordEncoder.match(user.getPassword(), isUserFound.getPassword())) {
            return null;
        }

        return generateToken(isUserFound);
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
        String token = getToken(request).replace("Bearer ", "");
        String email = decode(token.split("\\.")[1]).getString("email");

        return userService.getUser("email", email, new String[] {"id","email","isActive","role"});
    }

    public User getUserSessionAsToken(HttpServletRequest request) {
        String token = getToken(request).replace("Bearer ", "");
        JSONObject payload = decode(token.split("\\.")[1]);

        User user = new User();
        user.setEmail(payload.getString("email"));
        user.getRole().setName(payload.getString("role"));
        
        return user;
    }

    public boolean hasPermission(User user) {
        List<String> rolesWithPermissions = new PermissionsConfig().getRegistryRoles();
        
        if (rolesWithPermissions.contains(user.getRoleName())) {
            return true;
        }

        return false;
    }

    private static JSONObject decode(String encodedString) {
        String decodedString = new String(Base64.getUrlDecoder().decode(encodedString));
        return new JSONObject(decodedString);
    }
}
