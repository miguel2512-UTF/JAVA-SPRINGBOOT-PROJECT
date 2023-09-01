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
import com.project.javaproject.models.UserResponse;
import com.project.javaproject.services.UserService;
import com.project.javaproject.utils.UserMapper;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<List<UserResponse>> list() {
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
        res.put("data", UserMapper.mapUser(user));
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Object> insert(@RequestBody User user) throws Exception {
        Map<String, Object> res = new HashMap<>();

        user.setId(null);
        Map<String, Object> errors = userService.checkUserHasErrors(user);

        if (errors.size() > 0) {
            res.put("success", false);
            res.put("errors", errors);

            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        res.put("success", true);
        res.put("created_user", UserMapper.mapUser(userService.save(user)));

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody User requestUser) {
        Map<String, Object> res = new HashMap<>();
        User isUserFound = userService.getUserById(id);

        if (isUserFound == null) {
            res.put("success", false);
            res.put("message", "User not found");
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }

        requestUser.setId(id);

        Map<String, Object> errors = userService.checkUserHasErrors(requestUser);

        if (errors.size() > 0) {
            res.put("success", false);
            res.put("errors", errors);

            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        requestUser.setLoans(isUserFound.getLoans());

        if (userService.hasChanges(requestUser)) {
            userService.save(requestUser);
            res.put("updated_user", requestUser);
        }
        
        res.put("success", true);
        res.put("updated_user", UserMapper.mapUser(requestUser));
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
        res.put("data", UserMapper.mapUser(isUserExist));
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
