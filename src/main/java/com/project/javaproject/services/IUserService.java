package com.project.javaproject.services;

import java.util.List;

import com.project.javaproject.models.User;

public interface IUserService {
    public List<User> getUsers();
    public User insertUser(User user);
}
