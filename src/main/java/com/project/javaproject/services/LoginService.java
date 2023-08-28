package com.project.javaproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.javaproject.interfaces.IUserService;
import com.project.javaproject.models.User;

@Service
public class LoginService {

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
}
