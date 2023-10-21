package com.project.javaproject.interfaces;

import java.util.List;
import java.util.Map;

import com.project.javaproject.models.Loan;
import com.project.javaproject.models.User;

public interface ILoanService {
    public List<Loan> getAll();

    public List<Loan> getAllByUser(User user);

    public Loan getLoan(Long id);

    public Loan save(Loan loan);

    public Boolean deleteLoan(Long id);

    public Loan deleteLoan(Loan loan);

    public Map<String, Object> checkLoanHasErrors(Loan loan);
}
