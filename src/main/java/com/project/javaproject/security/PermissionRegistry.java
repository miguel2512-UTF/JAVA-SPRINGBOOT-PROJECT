package com.project.javaproject.security;

import java.util.ArrayList;
import java.util.List;

public class PermissionRegistry {
    public List<String> registryRoles = new ArrayList<>();

    public void addRole(String role) {
        this.registryRoles.add(role);
    }

    public List<String> getRegistryRoles() {
        return this.registryRoles;
    }
}
