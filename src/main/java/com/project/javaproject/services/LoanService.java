package com.project.javaproject.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

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

        Pattern patternDate = Pattern.compile("^([0-9]{4})-([0-9]{2})-([0-9]{2})$");

        if (loan.getLoanDate() == null || loan.getLoanDate().isEmpty()) {
            errors.put("loanDate", "Loan date is required");
        }

        if (loan.getLoanDate() != null) {
            Matcher isValidDate = patternDate.matcher(loan.getLoanDate());

            if (isValidDate.find() == false) {
                errors.put("loanDate", "Enter a valid date. Format: YYYY-MM-DD");
            } else {
                int year = Integer.parseInt(loan.getLoanDate().split("-")[0]);
                int month = Integer.parseInt(loan.getLoanDate().split("-")[1]);
                int day = Integer.parseInt(loan.getLoanDate().split("-")[2]);

                int currentYear = Integer.parseInt(LocalDate.now().toString().split("-")[0]);

                if (year < 1900 || year > currentYear) {
                    errors.put("loanDate", "Year must be between 1900 and "+currentYear);
                }
                
                if (month == 0 || month > 12) {
                    errors.put("loanDate", "Month must be between 1 and 12");
                }

                if (day == 0 || day > 31) {
                    errors.put("loanDate", "Day must be between 1 and 31");
                }
            }
        }

        return errors;
    }
}
