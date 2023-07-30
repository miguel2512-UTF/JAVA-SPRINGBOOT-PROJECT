package com.project.javaproject.services;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.javaproject.models.User;

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

    public User insertUser(User user) {
        User newUser = entityManager.merge(user);
        return newUser;
    }
    
    public User updateUser(User user) {
        User updateUser = entityManager.merge(user);
        return updateUser;
    }

    public User deleteUser(Long id) {
        User user = getUserById(id);
        entityManager.remove(user);
        return user;
    }
}
