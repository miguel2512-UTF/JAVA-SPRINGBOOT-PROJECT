package com.project.javaproject.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.javaproject.interfaces.IUserService;
import com.project.javaproject.models.User;
import com.project.javaproject.models.UserResponse;

import static com.project.javaproject.security.PasswordEncoder.encode;
import static com.project.javaproject.security.PasswordEncoder.match;

import com.project.javaproject.utils.UserMapper;
import com.project.javaproject.utils.ValidationMessages;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Repository
@Transactional
public class UserService implements IUserService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<UserResponse> getUsers() {
        String query = "FROM User";
        List<User> users = entityManager.createQuery(query, User.class).getResultList();
        List<UserResponse> usersResponse = UserMapper.mapUsers(users);

        return usersResponse;
    }

    public User getUserById(Long id) {
        User user = entityManager.find(User.class, id);
        return user;
    }

    public User getUserByEmail(String email) {
        String query = "SELECT u FROM User u WHERE email = :email";
        List<User> result = entityManager.createQuery(query, User.class).setParameter("email", email)
                .setMaxResults(1).getResultList();

        if (result.size() > 0) {
            return result.get(0);
        }

        return null;
    }

    public User save(User user) {
        user.setPassword(encode(user.getPassword()));
        User saveUser = entityManager.merge(user);
        return saveUser;
    }

    public void deleteUser(User user) {
        entityManager.remove(user);
    }

    private Boolean checkEmailIsBusy(User user) {
        User isEmailFound = getUserByEmail(user.getEmail());

        if (isEmailFound == null) {
            return false;
        }

        if (user.getId() != null) {
            if (isEmailFound.getId() == user.getId()) {
                return false;
            }
        }

        return true;
    }

    public Map<String, Object> checkUserHasErrors(User user) {
        Map<String, Object> errors = new HashMap<>();
        List<String> emailErrors = new ArrayList<>();
        List<String> passwordErrors = new ArrayList<>();

        Pattern patternEmail = Pattern.compile("^\\w+([.-_+]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,10})+$");

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            user.setEmail("");

            emailErrors.add(ValidationMessages.emailRequiredMsg);
            errors.put("email", emailErrors);
        }

        Matcher isValidEmail = patternEmail.matcher(user.getEmail());

        if (isValidEmail.find() == false) {
            emailErrors.add(ValidationMessages.emailIsInvalidMsg);
            errors.put("email", emailErrors);
        }

        if (checkEmailIsBusy(user)) {
            emailErrors.add(ValidationMessages.emailNotAvailableMsg);
            errors.put("email", emailErrors);
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            passwordErrors.add(ValidationMessages.passwordRequiredMsg);
            errors.put("password", passwordErrors);
        }

        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }

        return errors;
    }

    public Boolean hasChanges(User user) {
        User userCompare = getUserById(user.getId());

        if (userCompare.getEmail().equals(user.getEmail()) && match(user.getPassword(), userCompare.getPassword())
                && userCompare.getIsActive() == user.getIsActive()) {
            user.setPassword(userCompare.getPassword());
            return false;
        }

        return true;
    }
}
