package com.project.javaproject.controller;

import java.time.LocalDate;
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

import com.project.javaproject.interfaces.ILoanService;
import com.project.javaproject.interfaces.IPaymentService;
import com.project.javaproject.models.Loan;
import com.project.javaproject.models.Payment;

@RestController
@RequestMapping("/loan/payment")
public class PaymentController {
    
    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private ILoanService loanService;

    @GetMapping("/")
    public List<Payment> list() {
        return paymentService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPayment(@PathVariable Long id) {
        Payment payment = paymentService.getPayment(id);
        Map<String, Object> res = new HashMap<>();
        if (payment == null) {
            res.put("success", false);
            res.put("message", "Payment not found");
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
        res.put("success", true);
        res.put("data", payment);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody Payment payment) {
        payment.setId(null);

        if (payment.getDate() == null || payment.getDate().isEmpty()) {
            payment.setDate(LocalDate.now().toString());
        }

        Map<String, Object> res = new HashMap<>();
        Map<String, Object> errors = paymentService.checkPaymentHasErrors(payment);

        if (errors.size() > 0) {
            res.put("success", false);
            res.put("errors", errors);
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
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

        res.put("success", true);
        res.put("created_payment", newPayment);

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody Payment requestPayment) {
        Map<String, Object> res = new HashMap<>();
        Payment isPaymentFound = paymentService.getPayment(id);

        if (isPaymentFound == null) {
            res.put("success", false);
            res.put("message", "Payment not found");
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
        
        Map<String, Object> errors = paymentService.checkPaymentHasErrors(requestPayment);
        if (errors.size() > 0) {
            res.put("success", false);
            res.put("errors", errors);
            
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        requestPayment.setId(id);
        requestPayment.setValue(isPaymentFound.getValue());

        res.put("success", true);
        res.put("updated_payment", paymentService.save(requestPayment));

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        Payment payment = paymentService.getPayment(id);
        Boolean isDeletedPayment = paymentService.deletePayment(id);

        if (isDeletedPayment == false) {
            res.put("success", false);
            res.put("message", "Payment not found");
            return new ResponseEntity<Object>(res, HttpStatus.NOT_FOUND);
        }

        Loan loan = loanService.getLoan(payment.getLoanId());
        Double debtValue = loan.getDebtValue() + payment.getValue();
        loan.setDebtValue(debtValue);
        loan.setIsPayment(false);
        loanService.save(loan);
        
        res.put("success", true);
        res.put("message", "Payment deleted successfully");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
