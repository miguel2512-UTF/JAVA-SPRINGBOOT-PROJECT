package com.project.javaproject.utils;

import java.util.ArrayList;
import java.util.List;

import com.project.javaproject.models.User;
import com.project.javaproject.models.UserResponse;

public class UserMapper {

    public static UserResponse mapUser(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setIsActive(user.getIsActive());
        userResponse.setLoans(user.getLoans());
        userResponse.setRole(user.getRoleName());

        return userResponse;
    }

    public static User mapUser(UserResponse userResponse) {
        User user = new User();
        user.setId(userResponse.getId());
        user.setEmail(userResponse.getEmail());
        user.setIsActive(userResponse.getIsActive());
        user.setLoans(userResponse.getLoans());
        user.getRole().setName(userResponse.getRole());

        return user;
    }

    public static List<UserResponse> mapUsers(List<User> users) {
        List<UserResponse> usersResponse = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            usersResponse.add(mapUser(users.get(i)));
        }

        return usersResponse;
    }
}
