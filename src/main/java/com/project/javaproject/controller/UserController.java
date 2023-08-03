package com.project.javaproject.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.javaproject.models.User;
import com.project.javaproject.services.UserService;
import com.project.javaproject.utils.ValidationException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<List<User>> list() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        Map<String, Object> res = new HashMap<>();
        if (user == null) {
            res.put("success", false);
            res.put("message", "User not found");
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
        res.put("success", true);
        res.put("data", user);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Object> insert(@RequestBody User user) throws Exception {
        Map<String, Object> res = new HashMap<>();

        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }

        user.setId(null);

        try {
            res.put("success", true);
            res.put("created_user", userService.insertUser(user));
        } catch (ValidationException e) {
            res.put("success", false);
            res.put("errors", e.getErrors());
        }

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/")
    public ResponseEntity<Object> update(@RequestBody User requestUser) {
        Map<String, Object> res = new HashMap<>();

        if (requestUser.getId() == null) {
            res.put("success", false);
            res.put("message", "id is required");
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        User user = userService.getUserById(requestUser.getId());
        
        if (user == null) {
            res.put("success", false);
            res.put("message", "User not found");
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }

        if (requestUser.getEmail() != null) {
            user.setEmail(requestUser.getEmail());
        }

        if (requestUser.getPassword() != null) {
            user.setPassword(requestUser.getPassword());
        }

        if (requestUser.getIsActive() != null) {
            user.setIsActive(requestUser.getIsActive());
        }

        res.put("success", true);
        res.put("updated_user", userService.updateUser(user));
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        User isUserExist = userService.getUserById(id);

        if (isUserExist == null) {
            res.put("success", false);
            res.put("message", "User with id " + id + " dont exist");
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }

        userService.deleteUser(isUserExist);
        res.put("success", true);
        res.put("data", isUserExist);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
