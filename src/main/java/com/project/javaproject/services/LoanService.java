package com.project.javaproject.services;

import java.util.List;

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
        System.out.println("SUCCESS: "+success);

        return true;
    }

    public Loan deleteLoan(Loan loan) {
        entityManager.remove(loan);
        return loan;
    }
    
}
