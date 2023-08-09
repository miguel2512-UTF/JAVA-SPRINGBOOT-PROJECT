package com.project.javaproject.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.javaproject.models.Loan;
import com.project.javaproject.services.ILoanService;

@RestController
@RequestMapping("/loan")
public class LoanController {

    @Autowired
    private ILoanService loanService;

    @GetMapping("/")
    public List<Loan> getLoans() {
        return loanService.getAll();
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> createLoan(@RequestBody Loan loan) {
        System.out.println(loan.getDebtorName());
        Map<String, Object> res = new HashMap<>();
        Loan newLoan = loanService.save(loan);

        res.put("success", true);
        res.put("created_loan", newLoan);

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @DeleteMapping("/{idLoan}")
    public String deleteLoan(@PathVariable Long idLoan) {
        loanService.deleteLoan(idLoan);
        return "testing";
    }
}
