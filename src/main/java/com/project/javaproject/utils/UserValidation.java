package com.project.javaproject.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    final String emailRequiredMsg = "email is required";
    final String emailNotAvailableMsg = "email is already in use";
    final String emailIsInvalidMsg = "the entered email is invalid";
    final String passwordRequiredMsg = "password is required";

    public Map<String, Object> checkUserHasErrors(User user) {
        Map<String, Object> errors = new HashMap<>();
        List<String> emailErrors = new ArrayList<>();
        List<String> passwordErrors = new ArrayList<>();

        Pattern patternEmail = Pattern.compile("^\\w+([.-_+]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,10})+$");
        Matcher isValidEmail = patternEmail.matcher(user.getEmail());

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            emailErrors.add(emailRequiredMsg);
            errors.put("email", emailErrors);
        }

        if (isValidEmail.find() == false) {
            emailErrors.add(emailIsInvalidMsg);
            errors.put("email", emailErrors);
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            passwordErrors.add(passwordRequiredMsg);
            errors.put("password", passwordErrors);
        }

        if (user.getId() == null) {
            if (checkEmailAvailable(user.getEmail()) != true) {
                emailErrors.add(emailNotAvailableMsg);
                errors.put("email", emailErrors);
            }
        } else {
            User isSameUserEmail = userService.getUserById(user.getId());
            if (user.getEmail().equals(isSameUserEmail.getEmail()) == false) {
                if (checkEmailAvailable(user.getEmail()) == false) {
                    emailErrors.add(emailNotAvailableMsg);
                    errors.put("email", emailErrors);
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
