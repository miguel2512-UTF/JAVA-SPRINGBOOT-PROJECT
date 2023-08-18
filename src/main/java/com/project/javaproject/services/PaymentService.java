package com.project.javaproject.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.javaproject.interfaces.ILoanService;
import com.project.javaproject.interfaces.IPaymentService;
import com.project.javaproject.models.Loan;
import com.project.javaproject.models.Payment;
import com.project.javaproject.utils.Validation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Repository
@Transactional
public class PaymentService implements IPaymentService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ILoanService loanService;

    public List<Payment> getAll() {
        String query = "FROM Payment";
        return entityManager.createQuery(query, Payment.class).getResultList();
    }

    public Payment getPayment(Long id) {
        return entityManager.find(Payment.class, id);
    }

    public Payment save(Payment payment) {
        return entityManager.merge(payment);
    }

    public Boolean deletePayment(Long id) {
        String sql = "DELETE FROM Payment WHERE id = :id";
        int success = entityManager.createNativeQuery(sql, Loan.class).setParameter("id", id).executeUpdate();

        if (success == 0) {
            return false;
        }

        return true;
    }

    public Map<String, Object> checkPaymentHasErrors(Payment payment) {
        Map<String, Object> errors = new HashMap<>();
        
        if (payment.getDate() == null) {
            payment.setDate("");
        }

        List<String> dateErrors = Validation.checkIsDate(payment.getDate());

        if (dateErrors.size() > 0) {
            errors.put("date", dateErrors);
        }

        if (payment.getValue() == null || payment.getValue() == 0) {
            errors.put("value", "Value of payment is required");
        }

        if (payment.getLoanId() == null) {
            errors.put("loan", "Loan id must be provided");
            return errors;
        }

        Loan isLoanFound = loanService.getLoan(payment.getLoanId());
        if (isLoanFound == null) {
            errors.put("loan", "Loan doesn't exist");
            return errors;
        }

        if (payment.getDate().isEmpty() == false) {
            LocalDate datePayment = Validation.parseToLocalDate(payment.getDate());
            LocalDate dateLoan = Validation.parseToLocalDate(isLoanFound.getLoanDate());
            if (datePayment.isBefore(dateLoan)) {
                dateErrors.add("Payment date must be after the loan date");
                errors.put("date", dateErrors);
            }
        }
   
        if (payment.getId() == null) {
            if (payment.getValue() > isLoanFound.getDebtValue()) {
                errors.put("value", "Value of payment cannot be grater than the value of the debt");
            }
        }

        return errors;
    }
}
