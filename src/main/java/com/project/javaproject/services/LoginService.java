package com.project.javaproject.services;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.javaproject.interfaces.IUserService;
import com.project.javaproject.models.User;
import com.project.javaproject.models.UserResponse;
import com.project.javaproject.security.PasswordEncoder;
import com.project.javaproject.security.PermissionsConfig;
import com.project.javaproject.utils.UserMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (TOKEN_DURATION_HOURS * 3600000)))
                .compact();
    }

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
        HttpSession session = request.getSession(true);
        String token = getToken(request).split(" ")[1];
        String email = (String) decode(token.split("\\.")[1]).get("email");

        if (session.getAttribute("user") == null) {
            System.out.println("Se crea la sesión");
            session.setAttribute("user", UserMapper.mapUser(userService.getUserByEmail(email)));
        }
        
        User user = UserMapper.mapUser((UserResponse) session.getAttribute("user"));

        if (!user.getEmail().equals(email)) {
            session.setAttribute("user", UserMapper.mapUser(userService.getUserByEmail(email)));
            user = UserMapper.mapUser((UserResponse) session.getAttribute("user"));
        }
        
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
