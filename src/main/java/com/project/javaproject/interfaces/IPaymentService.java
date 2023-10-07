package com.project.javaproject.interfaces;

import java.util.List;
import java.util.Map;

import com.project.javaproject.models.Payment;
import com.project.javaproject.models.User;

public interface IPaymentService {
    public List<Payment> getAll();

    public List<Payment> getAllByUser(User user);
    
    public Payment getPayment(Long id);

    public Payment save(Payment payment);

    public Boolean deletePayment(Long id);

    public Map<String, Object> checkPaymentHasErrors(Payment payment);
}
