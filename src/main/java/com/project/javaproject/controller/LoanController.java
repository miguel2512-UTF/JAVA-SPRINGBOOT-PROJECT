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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.javaproject.models.Loan;
import com.project.javaproject.services.ILoanService;
import com.project.javaproject.services.IUserService;

@RestController
@RequestMapping("/loan")
public class LoanController {

    @Autowired
    private ILoanService loanService;

    @Autowired
    private IUserService userService;

    @GetMapping("/")
    public List<Loan> getLoans() {
        return loanService.getAll();
    }

    @GetMapping("/{idLoan}")
    public ResponseEntity<Object> getLoan(@PathVariable Long idLoan) {
        Loan loan = loanService.getLoan(idLoan);
        Map<String, Object> res = new HashMap<>();
        if (loan == null) {
            res.put("success", false);
            res.put("message", "Loan not found");
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
        res.put("success", true);
        res.put("data", loan);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> createLoan(@RequestBody Loan loan) {
        Map<String, Object> res = new HashMap<>();
        Map<String, Object> errors = loanService.checkLoanHasErrors(loan);

        if (errors.size() > 0) {
            res.put("success", false);
            res.put("errors", errors);
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
        
        Long userId = loan.getUser().getId();
        loan.setUser(userService.getUserById(userId));
        Loan newLoan = loanService.save(loan);

        res.put("success", true);
        res.put("created_loan", newLoan);

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PutMapping("/{idLoan}")
    public ResponseEntity<Object> update(@PathVariable Long idLoan, @RequestBody Loan requestLoan) {
        Map<String, Object> res = new HashMap<>();
        Loan isLoanFound = loanService.getLoan(idLoan);

        if (isLoanFound == null) {
            res.put("success", false);
            res.put("message", "Loan not found");
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }

        requestLoan.setIdLoan(idLoan);

        res.put("success", true);
        res.put("updated_loan", loanService.save(requestLoan));

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{idLoan}")
    public ResponseEntity<Object> delete(@PathVariable Long idLoan) {
        Map<String, Object> res = new HashMap<>();
        Boolean isDeletedLoan = loanService.deleteLoan(idLoan);

        if (isDeletedLoan == false) {
            res.put("success", false);
            res.put("message", "Loan not found");
            return new ResponseEntity<Object>(res, HttpStatus.NOT_FOUND);
        }
        
        res.put("success", true);
        res.put("message", "Loan deleted successfully");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
