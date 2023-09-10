package com.project.javaproject.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.javaproject.models.Loan;
import com.project.javaproject.models.User;
import com.project.javaproject.services.LoanService;
import com.project.javaproject.services.LoginService;
import com.project.javaproject.services.UserService;
import com.project.javaproject.utils.ApiResponse;
import com.project.javaproject.utils.UserMapper;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/me")
public class MeController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @Autowired
    private LoanService loanService;

    @GetMapping(path = { "", "/" })
    public ApiResponse getMe(HttpServletRequest req) {
        Map<String, Object> body = new HashMap<String, Object>();
        User currentUser = loginService.getUserSession(req);
        body.put("data", UserMapper.mapUser(currentUser));

        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @PutMapping(path = { "", "/" })
    public ApiResponse updateMe(@RequestBody User requestUser, HttpServletRequest req) {
        Map<String, Object> body = new HashMap<String, Object>();
        User currentUser = loginService.getUserSession(req);

        requestUser.setId(currentUser.getId());
        Map<String, Object> errors = userService.checkUserHasErrors(requestUser);

        if (requestUser.getRole().getId() != currentUser.getRole().getId()
                && !currentUser.getRoleName().equals("administrator")) {
            errors.put("role", "You do not have permission to change your role.");
            body.put("errors", errors);
            return ApiResponse.response(false, body, HttpStatus.BAD_REQUEST);
        }

        if (errors.size() > 0) {
            body.put("errors", errors);

            return ApiResponse.response(false, body, HttpStatus.BAD_REQUEST);
        }

        if (userService.hasChanges(requestUser)) {
            userService.save(requestUser);
        }

        requestUser.setRole(currentUser.getRole());

        body.put("updated_user", UserMapper.mapUser(requestUser));
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @DeleteMapping({ "", "/" })
    public ApiResponse deleteMe(HttpServletRequest req) {
        Map<String, Object> body = new HashMap<String, Object>();

        User currentUser = loginService.getUserSession(req);
        currentUser.setIsActive(false);
        userService.save(currentUser);

        body.put("message", "The account has been deactivated successfully");
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @GetMapping({ "/loan", "/loan/", "/loans" })
    public ApiResponse getLoans(HttpServletRequest request) {
        Map<String, Object> body = new HashMap<String, Object>();

        User user = loginService.getUserSession(request);

        body.put("data", loanService.getAllByUserId(user));

        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @GetMapping("/loan/{id}")
    public ApiResponse getLoan(@PathVariable Long id, HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        Loan loan = loanService.getLoan(id);
        User currentUser = loginService.getUserSession(request);
        if (loan == null || loan.getUserId() != currentUser.getId()) {
            body.put("message", "Loan not found");
            return ApiResponse.response(false, body, HttpStatus.NOT_FOUND);
        }

        System.out.println(loan.getUser().getEmail());
        body.put("data", loan);
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @PutMapping("/loan/{id}")
    public ApiResponse updateLoan(@PathVariable Long id, @RequestBody Loan requestLoan, HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        Loan isLoanFound = loanService.getLoan(id);
        User currentUser = loginService.getUserSession(request);

        if (isLoanFound == null || isLoanFound.getUserId() != currentUser.getId()) {
            body.put("message", "Loan not found");
            return ApiResponse.response(false, body, HttpStatus.NOT_FOUND);
        }

        if (requestLoan.getUserId() != isLoanFound.getUserId() && !currentUser.getRoleName().equals("administrator")) {
            body.put("message", "You do not have permission to make this.");
            return ApiResponse.response(false, body, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> errors = loanService.checkLoanHasErrors(requestLoan);
        if (errors.size() > 0) {
            body.put("errors", errors);
            return ApiResponse.response(false, body, HttpStatus.BAD_REQUEST);
        }

        requestLoan.setIdLoan(id);

        Double debtValue = isLoanFound.getDebtValue() + (requestLoan.getLoanValue() - isLoanFound.getLoanValue());
        if (debtValue >= 0) {
            requestLoan.setDebtValue(debtValue);
        } else {
            errors.put("value", "The value of the loan cannot be less than the values of the payments");
            body.put("errors", errors);

            return ApiResponse.response(false, body, HttpStatus.BAD_REQUEST);
        }

        changePaymentState(requestLoan);
        requestLoan.setPayments(isLoanFound.getPayments());
        body.put("updated_loan", loanService.save(requestLoan));

        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @DeleteMapping("/loan/{idLoan}")
    public ApiResponse delete(@PathVariable Long idLoan, HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        User currentUser = loginService.getUserSession(request);
        Loan isLoanFound = loanService.getLoan(idLoan);
        
        if (isLoanFound == null || isLoanFound.getUserId() != currentUser.getId()) {
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
