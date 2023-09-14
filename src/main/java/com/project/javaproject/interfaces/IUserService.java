package com.project.javaproject.interfaces;

import java.util.List;
import java.util.Map;

import com.project.javaproject.models.User;
import com.project.javaproject.models.UserResponse;

public interface IUserService {
    public List<UserResponse> getUsers();

    public User getUserById(Long id);

    public User getUserByEmail(String email);

    public User save(User user);

    public void deleteUser(User user);

    public Map<String, Object> checkUserHasErrors(User user);

    public Boolean hasChanges(User user);

    public boolean checkUserExist(Long id);
}
