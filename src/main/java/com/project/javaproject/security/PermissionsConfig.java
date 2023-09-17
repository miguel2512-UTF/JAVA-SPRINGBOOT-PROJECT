package com.project.javaproject.security;

import java.util.List;

import com.project.javaproject.utils.RoleTypes;

public class PermissionsConfig {
    private PermissionRegistry registry = new PermissionRegistry();

    public PermissionsConfig() {
        this.addRoles();
    }

    public void addRoles() {
        this.registry.addRole(RoleTypes.ADMIN);
    }

    public List<String> getRegistryRoles() {
        return this.registry.registryRoles;
    }
}
