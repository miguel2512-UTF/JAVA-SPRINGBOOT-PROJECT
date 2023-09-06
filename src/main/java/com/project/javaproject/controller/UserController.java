package com.project.javaproject.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.javaproject.models.User;
import com.project.javaproject.services.RoleService;
import com.project.javaproject.services.UserService;
import com.project.javaproject.utils.ApiResponse;
import com.project.javaproject.utils.UserMapper;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/")
    public ApiResponse list() {
        Map<String, Object> body = new HashMap<>();
        body.put("data", userService.getUsers());
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ApiResponse getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        Map<String, Object> body = new HashMap<>();
        if (user == null) {
            body.put("message", "User not found");
            return ApiResponse.response(false, body, HttpStatus.NOT_FOUND);
        }
        body.put("data", UserMapper.mapUser(user));
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @PostMapping("/")
    public ApiResponse insert(@RequestBody User user) throws Exception {
        Map<String, Object> body = new HashMap<>();

        user.setId(null);
        Map<String, Object> errors = userService.checkUserHasErrors(user);

        if (errors.size() > 0) {
            body.put("errors", errors);

            return ApiResponse.response(false, body, HttpStatus.BAD_REQUEST);
        }

        body.put("created_user", UserMapper.mapUser(userService.save(user)));

        return ApiResponse.response(true, body, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable Long id, @RequestBody User requestUser) {
        Map<String, Object> body = new HashMap<>();
        User isUserFound = userService.getUserById(id);

        if (isUserFound == null) {
            body.put("message", "User not found");
            return ApiResponse.response(false, body, HttpStatus.NOT_FOUND);
        }

        requestUser.setId(id);

        Map<String, Object> errors = userService.checkUserHasErrors(requestUser);

        if (errors.size() > 0) {
            body.put("errors", errors);

            return ApiResponse.response(false, body, HttpStatus.BAD_REQUEST);
        }

        requestUser.setLoans(isUserFound.getLoans());
        requestUser.setRole(roleService.getRoleById(requestUser.getRole().getId()));

        if (userService.hasChanges(requestUser)) {
            userService.save(requestUser);
        }
        
        body.put("updated_user", UserMapper.mapUser(requestUser));
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Long id) {
        Map<String, Object> body = new HashMap<>();
        User isUserExist = userService.getUserById(id);

        if (isUserExist == null) {
            body.put("message", "User with id " + id + " dont exist");
            return ApiResponse.response(false, body, HttpStatus.NOT_FOUND);
        }

        userService.deleteUser(isUserExist);
        body.put("data", UserMapper.mapUser(isUserExist));
        return ApiResponse.response(true, body, HttpStatus.OK);
    }
}
