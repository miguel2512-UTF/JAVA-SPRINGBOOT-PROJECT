package com.project.javaproject.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.javaproject.interfaces.ILoanService;
import com.project.javaproject.models.Loan;
import com.project.javaproject.models.User;
import com.project.javaproject.services.LoginService;
import com.project.javaproject.utils.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/loan")
public class LoanController {

    @Autowired
    private ILoanService loanService;

    @Autowired
    private LoginService loginService;

    @GetMapping({ "", "/" })
    public ApiResponse getLoans(HttpServletRequest request) {
        Map<String, Object> data = new HashMap<>();
        User currentUser = loginService.getUserSession(request);
        List<Loan> loans = new ArrayList<>();

        if (loginService.hasPermission(currentUser)) {
            loans = loanService.getAll();
        } else {
            loans = loanService.getAllByUser(currentUser);
        }

        data.put("data", loans);
        return ApiResponse.response(true, data, HttpStatus.OK);
    }

    @GetMapping("/{idLoan}")
    public ApiResponse getLoan(@PathVariable Long idLoan, HttpServletRequest request) {
        User currentUser = loginService.getUserSession(request);
        Loan loan = loanService.getLoan(idLoan);
        Map<String, Object> data = new HashMap<>();
        if (loan == null) {
            data.put("message", "Loan not found");
            return ApiResponse.response(false, data, HttpStatus.NOT_FOUND);
        }

        if (loan.getUserId() != currentUser.getId() && !loginService.hasPermission(currentUser)) {
            data.put("message", "Loan not found");
            return ApiResponse.response(false, data, HttpStatus.NOT_FOUND);
        }

        data.put("data", loan);
        return ApiResponse.response(true, data, HttpStatus.OK);
    }

    @PostMapping({ "", "/" })
    public ApiResponse createLoan(@RequestBody Loan loan, HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        User currentUser = loginService.getUserSession(request);
        
        Long userId = loan.getUserId();
        loan.setUserId(currentUser.getId());
        
        if (userId != null && loginService.hasPermission(currentUser)) {
            loan.setUserId(userId);
        }
        
        Map<String, Object> errors = loanService.checkLoanHasErrors(loan);

        if (errors.size() > 0) {
            body.put("errors", errors);
            return ApiResponse.response(false, body, HttpStatus.BAD_REQUEST);
        }

        loan.setIdLoan(null);
        loan.setDebtValue(loan.getLoanValue());
        loan.setIsPayment(false);
        Loan newLoan = loanService.save(loan);

        body.put("created_loan", newLoan);
        return ApiResponse.response(true, body, HttpStatus.CREATED);
    }

    @PutMapping("/{idLoan}")
    public ApiResponse update(@PathVariable Long idLoan, @RequestBody Loan requestLoan, HttpServletRequest request) {
        Map<String, Object> data = new HashMap<>();
        Loan isLoanFound = loanService.getLoan(idLoan);
        User currentUser = loginService.getUserSession(request);

        if (isLoanFound == null || (isLoanFound.getUserId() != currentUser.getId() && !loginService.hasPermission(currentUser))) {
            data.put("message", "Loan not found");
            return ApiResponse.response(false, data, HttpStatus.NOT_FOUND);
        }

        Long userId = requestLoan.getUserId();
        requestLoan.setUserId(currentUser.getId());
        
        if (userId != null && loginService.hasPermission(currentUser)) {
            requestLoan.setUserId(userId);
        }

        Map<String, Object> errors = loanService.checkLoanHasErrors(requestLoan);
        if (errors.size() > 0) {
            data.put("errors", errors);
            return ApiResponse.response(false, data, HttpStatus.BAD_REQUEST);
        }

        requestLoan.setIdLoan(idLoan);

        Double debtValue = isLoanFound.getDebtValue() + (requestLoan.getLoanValue() - isLoanFound.getLoanValue());
        if (debtValue >= 0) {
            requestLoan.setDebtValue(debtValue);
        } else {
            errors.put("value", "The value of the loan cannot be less than the values of the payments");
            data.put("errors", errors);
            
            return ApiResponse.response(false, data, HttpStatus.BAD_REQUEST);
        }

        changePaymentState(requestLoan);

        requestLoan.setPayments(isLoanFound.getPayments());

        data.put("updated_loan", loanService.save(requestLoan));

        return ApiResponse.response(true, data, HttpStatus.OK);
    }

    @DeleteMapping("/{idLoan}")
    public ApiResponse delete(@PathVariable Long idLoan, HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        User currentUser = loginService.getUserSession(request);
        Loan isLoanFound = loanService.getLoan(idLoan);
        
        if (isLoanFound == null || (isLoanFound.getUserId() != currentUser.getId() && !loginService.hasPermission(currentUser))) {
            body.put("message", "Loan not found");
            return ApiResponse.response(false, body, HttpStatus.NOT_FOUND);
        }

        loanService.deleteLoan(idLoan);

        body.put("message", "Loan deleted successfully");
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    private void changePaymentState(Loan loan) {
        if (loan.getDebtValue() == 0) {
            loan.setIsPayment(true);
        } else {
            loan.setIsPayment(false);
        }
    }
}
