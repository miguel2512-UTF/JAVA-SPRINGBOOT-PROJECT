package com.project.javaproject.services;

import java.util.List;
import java.util.Map;

import com.project.javaproject.models.Loan;

public interface ILoanService {
    public List<Loan> getAll();
    public Loan getLoan(Long id);
    public Loan save(Loan loan);
    public Boolean deleteLoan(Long id);
    public Loan deleteLoan(Loan loan);
    public Map<String, Object> checkLoanHasErrors(Loan loan);
}
