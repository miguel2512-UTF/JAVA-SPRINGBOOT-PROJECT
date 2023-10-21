package com.project.javaproject.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.javaproject.models.User;
import com.project.javaproject.services.LoanService;
import com.project.javaproject.services.LoginService;
import com.project.javaproject.services.UserService;
import com.project.javaproject.utils.ApiResponse;
import com.project.javaproject.utils.UserMapper;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/me")
public class MeController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @Autowired
    private LoanService loanService;

    @GetMapping(path = { "", "/" })
    public ApiResponse getMe(HttpServletRequest req) {
        Map<String, Object> body = new HashMap<String, Object>();
        User currentUser = loginService.getUserSession(req);
        currentUser.setLoans(loanService.getAllByUser(currentUser));
        body.put("data", UserMapper.mapUser(currentUser));

        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @PutMapping(path = { "", "/" })
    public ApiResponse updateMe(@RequestBody User requestUser, HttpServletRequest req) {
        Map<String, Object> body = new HashMap<String, Object>();
        User currentUser = loginService.getUserSession(req);

        requestUser.setId(currentUser.getId());
        Map<String, Object> errors = userService.checkUserHasErrors(requestUser);

        if (requestUser.getRole().getId() != currentUser.getRole().getId()
                && !currentUser.getRoleName().equals("administrator")) {
            errors.put("role", "You do not have permission to change your role.");
            body.put("errors", errors);
            return ApiResponse.response(false, body, HttpStatus.BAD_REQUEST);
        }

        if (errors.size() > 0) {
            body.put("errors", errors);

            return ApiResponse.response(false, body, HttpStatus.BAD_REQUEST);
        }

        if (userService.hasChanges(requestUser)) {
            userService.save(requestUser);
        }

        requestUser.setRole(currentUser.getRole());

        body.put("updated_user", UserMapper.mapUser(requestUser));
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @DeleteMapping({ "", "/" })
    public ApiResponse deleteMe(HttpServletRequest req) {
        Map<String, Object> body = new HashMap<String, Object>();

        User currentUser = loginService.getUserSession(req);
        currentUser.setIsActive(false);
        userService.save(currentUser);

        body.put("message", "The account has been deactivated successfully");
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @GetMapping({ "/loan", "/loan/", "/loans" })
    public ApiResponse getLoans(HttpServletRequest request) {
        Map<String, Object> body = new HashMap<String, Object>();

        User user = loginService.getUserSession(request);

        body.put("data", loanService.getAllByUser(user));

        return ApiResponse.response(true, body, HttpStatus.OK);
    }
}
