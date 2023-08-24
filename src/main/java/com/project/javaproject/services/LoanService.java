package com.project.javaproject.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.project.javaproject.interfaces.ILoanService;
import com.project.javaproject.models.Loan;
import com.project.javaproject.utils.Validation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
@Repository
@Transactional
public class LoanService implements ILoanService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    public List<Loan> getAll() {
        String query = "FROM Loan";
        return entityManager.createQuery(query, Loan.class).getResultList();
    }

    public Loan getLoan(Long id) {
        return entityManager.find(Loan.class, id);
    }

    public Loan save(Loan loan) {
        return entityManager.merge(loan);
    }

    public Boolean deleteLoan(Long id) {
        String sql = "DELETE FROM Loan WHERE id_loan = :id";
        int success = entityManager.createNativeQuery(sql, Loan.class).setParameter("id", id).executeUpdate();

        if (success == 0) {
            return false;
        }

        return true;
    }

    public Loan deleteLoan(Loan loan) {
        entityManager.remove(loan);
        return loan;
    }

    public Map<String, Object> checkLoanHasErrors(Loan loan) {
        Map<String, Object> errors = new HashMap<>();
        List<String> dateErrors = new ArrayList<>();
        List<String> nameErrors = new ArrayList<>();

        Pattern patternName = Pattern.compile("^([a-zA-Z]{2,10})(\s[a-zA-Z]{1,15})?$");

        if (loan.getDebtorName() == null || loan.getDebtorName().isEmpty()) {
            loan.setDebtorName("");

            nameErrors.add("Debtor name is required");
            errors.put("debtorName", nameErrors);
        }

        Matcher isValidName = patternName.matcher(loan.getDebtorName());
        if (isValidName.find() == false) {
            nameErrors.add("Debtor must be a valid name. Format: Name Lastname(Optional)");
            errors.put("debtorName", nameErrors);
        }

        if (loan.getUserId() == null) {
            errors.put("user", "User id must be provided");
        } else {
            if (userService.getUserById(loan.getUserId()) == null) {
                errors.put("user", "User doesn't exist");
            }
        }

        if (loan.getLoanDate() == null) {
            loan.setLoanDate("");
        }

        dateErrors = Validation.checkIsDate(loan.getLoanDate());

        if (dateErrors.size() > 0) {
            errors.put("date", dateErrors);
        }

        if (loan.getLoanValue() == null || loan.getLoanValue() == 0) {
            errors.put("value", "Loan value is required");
        }

        return errors;
    }
}
