package com.project.javaproject.services;

import java.util.List;

import com.project.javaproject.models.User;

public interface IUserService {
    public List<User> getUsers();
    public User getUserById(Long id);
    public User getUserByEmail(String email);
    public User insertUser(User user) throws Exception;
    public User updateUser(User user);
    public void deleteUser(User user);
}
