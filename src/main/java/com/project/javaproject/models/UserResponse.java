package com.project.javaproject.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class UserResponse {
    
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private Boolean isActive;

    @Getter
    private List<Loan> loans;

    @Getter
    @Setter
    private String role;

    public UserResponse() {
    }

    public UserResponse(Long id, String email, Boolean isActive) {
        this.id = id;
        this.email = email;
        this.isActive = isActive;
    }

    public void setLoans(List<Loan> loans) {
        if (loans == null) {
            this.loans = new ArrayList<>();
        } else {
            this.loans = loans;
        }
    }
}
