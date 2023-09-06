package com.project.javaproject.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.project.javaproject.interfaces.IRoleService;
import com.project.javaproject.models.Role;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
@Repository
@Transactional
public class RoleService implements IRoleService {

    @Autowired
    private EntityManager entityManager;

    public List<Role> getAll() {
        String query = "FROM Role";
        return entityManager.createQuery(query, Role.class).getResultList();
    }

    public Role getRoleById(Long id) {
        Role role = entityManager.find(Role.class, id);
        return role;
    }

    public Role getRoleByName(String name) {
        String query = "SELECT r FROM Role r WHERE name = :name";
        List<Role> result = entityManager.createQuery(query, Role.class).setParameter("name", name).getResultList();

        if (result.size() == 0) {
            return null;
        }

        return result.get(0);
    }

    public Role save(Role role) {
        Role saveRole = entityManager.merge(role);
        return saveRole;
    }

    public Boolean delete(Long id) {
        String query = "DELETE FROM Role r WHERE id = :id";
        int success = entityManager.createQuery(query).setParameter("id", id).executeUpdate();

        if (success == 0) {
            return false;
        }

        return true;
    }

    public Map<String, Object> checkRoleHasErrors(Role role) {
        Map<String, Object> errors = new HashMap<String, Object>();

        if (role.getName() == null || role.getName().isEmpty()) {
            role.setName("");
            errors.put("name", "Name is required");
        }

        if (checkNameIsBusy(role)) {
            errors.put("name", "Name is already in use");
        }

        if (role.getDescription() == null || role.getDescription().isEmpty()) {
            errors.put("description", "Description is required");
        }

        return errors;
    }

    private boolean checkNameIsBusy(Role role) {
        Role isRoleFound = getRoleByName(role.getName());

        if (isRoleFound == null) {
            return false;
        }

        if (isRoleFound.getId() == role.getId()) {
            return false;
        }

        return true;
    }

}
