package com.project.javaproject.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "loan")
public class Loan {
    
    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long idLoan;

    @Column(nullable = false)
    private String debtorName;

    @Column(nullable = false)
    private Double loanValue;

    @Column(nullable = false)
    private String LoanDate;
}
