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

import com.project.javaproject.models.Role;
import com.project.javaproject.services.RoleService;
import com.project.javaproject.utils.ApiResponse;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/")
    public ApiResponse getRoles() {
        Map<String, Object> body = new HashMap<>();
        body.put("data", roleService.getAll());
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ApiResponse getRole(@PathVariable Long id) {
        Map<String, Object> body = new HashMap<String, Object>();
        Role isRoleFound = roleService.getRoleById(id);

        if (isRoleFound == null) {
            body.put("message", "Role not found");
            return ApiResponse.response(false, body, HttpStatus.NOT_FOUND);
        }

        body.put("data", isRoleFound);
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @PostMapping("/")
    public ApiResponse createRole(@RequestBody Role role) {
        Map<String, Object> body = new HashMap<String, Object>();
        role.setId(null);
        Map<String, Object> errors = roleService.checkRoleHasErrors(role);

        if (errors.size() > 0) {
            body.put("errors", errors);
            return ApiResponse.response(false, body, HttpStatus.BAD_REQUEST);
        }

        role.setName(role.getName().toLowerCase());

        body.put("created_role", roleService.save(role));
        return ApiResponse.response(true, body, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ApiResponse updateRole(@PathVariable Long id, @RequestBody Role role) {
        Map<String, Object> body = new HashMap<String, Object>();
        Role isRoleFound = roleService.getRoleById(id);

        if (isRoleFound == null) {
            body.put("message", "Role not found");
            return ApiResponse.response(false, body, HttpStatus.NOT_FOUND);
        }

        role.setId(id);
        Map<String, Object> errors = roleService.checkRoleHasErrors(role);
        if (errors.size() > 0) {
            body.put("errors", errors);
            return ApiResponse.response(false, body, HttpStatus.BAD_REQUEST);
        }

        role.setName(role.getName().toLowerCase());
        body.put("updated_role", roleService.save(role));
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteRole(@PathVariable Long id) {
        Map<String, Object> body = new HashMap<String, Object>();
        Boolean isDeletedRole = roleService.delete(id);

        if (isDeletedRole == false) {
            body.put("message", "Role not found");
            return ApiResponse.response(false, body, HttpStatus.NOT_FOUND);
        }

        body.put("message", "Role deleted successfully");
        return ApiResponse.response(true, body, HttpStatus.OK);
    }
}
