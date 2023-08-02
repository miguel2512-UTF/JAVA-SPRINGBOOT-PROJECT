package com.project.javaproject.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.javaproject.models.User;
import com.project.javaproject.utils.ValidationException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Repository
@Transactional
public class UserService implements IUserService {

    @PersistenceContext
    private EntityManager entityManager;

    // static List<User> users = new ArrayList<User>();

    // static {
    //     users.add(new User(Long.valueOf(1), "John@gmail.com", "password", true));
    //     users.set(0, new User(Long.valueOf(2), "example@gmail.com", "password", false));
    // }

    @SuppressWarnings("all")
    public List<User> getUsers() {
        String query = "SELECT * FROM User";
        List<User> users = entityManager.createNativeQuery(query, User.class).getResultList();
        return users;
    }

    public User getUserById(Long id) {
        User user = entityManager.find(User.class, id);
        return user;
    }

    @SuppressWarnings("all")
    public User getUserByEmail(String email) {
        String query = "SELECT * FROM User WHERE email = :email";
        List<User> result = entityManager.createNativeQuery(query, User.class).setParameter("email", email).setMaxResults(1).getResultList();

        if (result.size() > 0) {
            return result.get(0);
        }

        return null;
    }

    public User insertUser(User user) throws ValidationException {
        Map<String, String> errors = checkUserHasErrors(user);
        if (errors.size() > 0) {
            throw new ValidationException(errors);
        }

        User newUser = entityManager.merge(user);
        return newUser;
    }
    
    public User updateUser(User user) {
        User updateUser = entityManager.merge(user);
        return updateUser;
    }

    public void deleteUser(User user) {
        entityManager.remove(user);
    }

    private Boolean checkEmailAvailable(String email) {
        User user = getUserByEmail(email);
        
        if (user != null) {
            return false;
        }

        return true;
    }

    private Map<String, String> checkUserHasErrors(User user) {
        Map<String, String> errors = new HashMap<>();

        if (user.getEmail() == null) {
            errors.put("email", "email is required");
        }

        if (user.getPassword() == null) {
            errors.put("password", "password is required");
        }

        if (checkEmailAvailable(user.getEmail()) != true) {
            errors.put("email", "email is already in use");
        }

        return errors;
    }
}
