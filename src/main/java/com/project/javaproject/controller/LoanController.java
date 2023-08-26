package com.project.javaproject.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.javaproject.interfaces.ILoanService;
import com.project.javaproject.interfaces.IUserService;
import com.project.javaproject.models.Loan;

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
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> errors = loanService.checkLoanHasErrors(loan);

        if (errors.size() > 0) {
            data.put("errors", errors);
            return APIResponse(false, data, HttpStatus.BAD_REQUEST);
        }

        Long userId = loan.getUser().getId();
        loan.setIdLoan(null);
        loan.setDebtValue(loan.getLoanValue());
        loan.setIsPayment(false);
        loan.setUser(userService.getUserById(userId));
        Loan newLoan = loanService.save(loan);

        data.put("created_loan", newLoan);
        return APIResponse(true, data, HttpStatus.CREATED);
    }

    @PutMapping("/{idLoan}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long idLoan, @RequestBody Loan requestLoan) {
        Map<String, Object> data = new HashMap<>();
        Loan isLoanFound = loanService.getLoan(idLoan);

        if (isLoanFound == null) {
            data.put("message", "Loan not found");
            return APIResponse(false, data, HttpStatus.NOT_FOUND);
        }
        
        Map<String, Object> errors = loanService.checkLoanHasErrors(requestLoan);
        if (errors.size() > 0) {
            data.put("errors", errors);
            return APIResponse(false, data, HttpStatus.BAD_REQUEST);
        }

        requestLoan.setIdLoan(idLoan);

        Double debtValue = isLoanFound.getDebtValue() + (requestLoan.getLoanValue() - isLoanFound.getLoanValue());
        if (debtValue >= 0) {
            requestLoan.setDebtValue(debtValue);
        } else {
            errors.put("value", "The value of the loan cannot be less than the values of the payments");
            data.put("errors", errors);
            
            return APIResponse(false, data, HttpStatus.BAD_REQUEST);
        }

        changePaymentState(requestLoan);

        requestLoan.setPayments(isLoanFound.getPayments());

        data.put("updated_loan", loanService.save(requestLoan));

        return APIResponse(true, data, HttpStatus.OK);
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

    private ResponseEntity<Map<String, Object>> APIResponse(Boolean success, Object data, HttpStatusCode status) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", success);
        res.put("data", data);
        return new ResponseEntity<>(res, status);
    }

    private void changePaymentState(Loan loan) {
        if (loan.getDebtValue() == 0) {
            loan.setIsPayment(true);
        } else {
            loan.setIsPayment(false);
        }
    }
}
