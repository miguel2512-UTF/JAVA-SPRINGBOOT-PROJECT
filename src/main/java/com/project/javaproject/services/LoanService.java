package com.project.javaproject.services;

import java.time.LocalDate;
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

        Pattern patternDate = Pattern.compile("^([0-9]{4})-([0-9]{2})-([0-9]{2})$");
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

        if (loan.getLoanDate() == null || loan.getLoanDate().isEmpty()) {
            loan.setLoanDate("");

            dateErrors.add("Loan date is required");
            errors.put("loanDate", dateErrors);
        }

        Matcher isValidDate = patternDate.matcher(loan.getLoanDate());
        if (isValidDate.find() == false) {
            dateErrors.add("Enter a valid date. Format: YYYY-MM-DD");
            errors.put("loanDate", dateErrors);

            return errors;
        }

        int year = Integer.parseInt(loan.getLoanDate().split("-")[0]);
        int month = Integer.parseInt(loan.getLoanDate().split("-")[1]);
        int day = Integer.parseInt(loan.getLoanDate().split("-")[2]);

        int minimumYear = Integer.parseInt(LocalDate.now().minusYears(1).toString().split("-")[0]);
        int currentYear = Integer.parseInt(LocalDate.now().toString().split("-")[0]);

        if (year < minimumYear || year > currentYear) {
            dateErrors.add(String.format("Year must be between %1$d and %2$d", minimumYear, currentYear));
            errors.put("loanDate", dateErrors);
        }

        if (month == 0 || month > 12) {
            dateErrors.add("Month must be between 1 and 12");
            errors.put("loanDate", dateErrors);
        }

        if (day == 0 || day > 31) {
            dateErrors.add("Day must be between 1 and 31");
            errors.put("loanDate", dateErrors);
        }

        return errors;
    }
}
