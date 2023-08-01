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
    public ResponseEntity<Object> insert(@RequestBody User user) {
        Map<String, Object> res = new HashMap<>();
        Map<String, String> errors = user.hasErrors();

        if (errors.size() > 0) {
            res.put("success", false);
            res.put("errors", errors);

            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }

        res.put("success", true);
        res.put("created_user", userService.insertUser(user));
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/")
    public ResponseEntity<User> update(@RequestBody User user) {
        return new ResponseEntity<>(userService.updateUser(user), HttpStatus.OK);
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
