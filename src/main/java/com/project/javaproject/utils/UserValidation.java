package com.project.javaproject.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.javaproject.models.User;
import com.project.javaproject.services.UserService;

@Service
public class UserValidation {
    @Autowired
    private UserService userService;

    public void validateUser(User user, User body) {
        Map<String, Object> errors = new HashMap<>();

        if (body.getEmail() != null && body.getEmail().isEmpty() == false) {
            if (body.getEmail().equalsIgnoreCase(user.getEmail()) == false) {
                if (userService.getUserByEmail(body.getEmail()) != null) {
                    errors.put("success", false);
                    errors.put("message", "Email is already in use");
                }
            }

            user.setEmail(body.getEmail());
        }

        if (body.getPassword() != null && body.getPassword().isEmpty() == false) {
            user.setPassword(body.getPassword());
        }

        if (body.getIsActive() != null) {
            user.setIsActive(body.getIsActive());
        }
    }

    public Map<String, String> checkUserHasErrors(User user) {
        Map<String, String> errors = new HashMap<>();
        Pattern patternEmail = Pattern.compile("^\\w+([.-_+]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,10})+$");
        Matcher isValidEmail = patternEmail.matcher(user.getEmail());

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            errors.put("email", "email is required");
        }

        if (isValidEmail.find() == false) {
            errors.put("email", "the email entered is invalid");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            errors.put("password", "password is required");
        }

        if (user.getId() == null) {
            if (checkEmailAvailable(user.getEmail()) != true) {
                errors.put("email", "email is already in use");
            }
        } else {
            User isUserEmail = userService.getUserById(user.getId());
            if (user.getEmail().equals(isUserEmail.getEmail()) == false) {
                if (userService.getUserByEmail(user.getEmail()) != null) {
                    errors.put("email", "email is already in use");
                }
            }
        }

        return errors;
    }

    private Boolean checkEmailAvailable(String email) {
        User user = userService.getUserByEmail(email);

        if (user != null) {
            return false;
        }

        return true;
    }

}
