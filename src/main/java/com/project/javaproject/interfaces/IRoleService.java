package com.project.javaproject.interfaces;

import java.util.List;
import java.util.Map;

import com.project.javaproject.models.Role;

public interface IRoleService {
    public List<Role> getAll();

    public Role getRoleById(Long id);

    public Role getRoleByName(String name);

    public Role save(Role role);

    public Boolean delete(Long id);

    public Map<String, Object> checkRoleHasErrors(Role role);
}
