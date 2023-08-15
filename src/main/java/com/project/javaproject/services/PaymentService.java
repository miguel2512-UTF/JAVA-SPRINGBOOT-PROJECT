package com.project.javaproject.services;

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

        if (payment.getLoanId() == null) {
            errors.put("loan", "Loan id must be provided");
        } else {
            if (loanService.getLoan(payment.getLoanId()) == null) {
                errors.put("loan", "Loan doesn't exist");
            }
        }

        return errors;
    }
}
