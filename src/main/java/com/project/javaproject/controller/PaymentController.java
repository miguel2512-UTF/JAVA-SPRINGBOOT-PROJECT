package com.project.javaproject.controller;

import java.time.LocalDate;
import java.util.HashMap;
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
import com.project.javaproject.interfaces.IPaymentService;
import com.project.javaproject.models.Loan;
import com.project.javaproject.models.Payment;
import com.project.javaproject.utils.ApiResponse;

@RestController
@RequestMapping("/loan/payment")
public class PaymentController {
    
    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private ILoanService loanService;

    @GetMapping("/")
    public ApiResponse list() {
        Map<String, Object> body = new HashMap<>();
        body.put("data", paymentService.getAll());
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ApiResponse getPayment(@PathVariable Long id) {
        Payment payment = paymentService.getPayment(id);
        Map<String, Object> body = new HashMap<>();
        if (payment == null) {
            body.put("message", "Payment not found");
            return ApiResponse.response(false, body, HttpStatus.NOT_FOUND);
        }
        body.put("data", payment);
        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @PostMapping("/")
    public ApiResponse createPayment(@RequestBody Payment payment) {
        payment.setId(null);

        if (payment.getDate() == null || payment.getDate().isEmpty()) {
            payment.setDate(LocalDate.now().toString());
        }

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> errors = paymentService.checkPaymentHasErrors(payment);

        if (errors.size() > 0) {
            body.put("errors", errors);
            return ApiResponse.response(false, body, HttpStatus.BAD_REQUEST);
        }
        
        Long loanId = payment.getLoanId();
        payment.setLoan(loanService.getLoan(loanId));
        Payment newPayment = paymentService.save(payment);

        Loan loan = newPayment.getLoan();
        Double debtValue = loan.getDebtValue() - newPayment.getValue();
        loan.setDebtValue(debtValue);
        if (loan.getDebtValue() == 0) {
            loan.setIsPayment(true);
        }
        loanService.save(loan);

        body.put("created_payment", newPayment);

        return ApiResponse.response(true, body, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable Long id, @RequestBody Payment requestPayment) {
        Map<String, Object> body = new HashMap<>();
        Payment isPaymentFound = paymentService.getPayment(id);

        if (isPaymentFound == null) {
            body.put("message", "Payment not found");
            return ApiResponse.response(false, body, HttpStatus.NOT_FOUND);
        }
        
        Map<String, Object> errors = paymentService.checkPaymentHasErrors(requestPayment);
        if (errors.size() > 0) {
            body.put("errors", errors);
            
            return ApiResponse.response(false, body, HttpStatus.BAD_REQUEST);
        }

        requestPayment.setId(id);
        requestPayment.setValue(isPaymentFound.getValue());
        requestPayment.setLoan(isPaymentFound.getLoan());

        body.put("updated_payment", paymentService.save(requestPayment));

        return ApiResponse.response(true, body, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Long id) {
        Map<String, Object> body = new HashMap<>();
        Payment payment = paymentService.getPayment(id);
        Boolean isDeletedPayment = paymentService.deletePayment(id);

        if (isDeletedPayment == false) {
            body.put("message", "Payment not found");
            return ApiResponse.response(false, body, HttpStatus.NOT_FOUND);
        }

        Loan loan = loanService.getLoan(payment.getLoanId());
        Double debtValue = loan.getDebtValue() + payment.getValue();
        loan.setDebtValue(debtValue);
        loan.setIsPayment(false);
        loanService.save(loan);
        
        body.put("message", "Payment deleted successfully");
        return ApiResponse.response(true, body, HttpStatus.OK);
    }
}
