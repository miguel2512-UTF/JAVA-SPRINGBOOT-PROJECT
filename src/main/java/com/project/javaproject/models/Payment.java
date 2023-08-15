package com.project.javaproject.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payment")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Column(nullable = false)
    @Getter
    @Setter
    private String date;

    @Column(nullable = false)
    @Getter
    @Setter
    private Double value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    @JsonProperty(access = Access.WRITE_ONLY)
    @Getter
    @Setter
    private Loan loan;

    public Payment() {}

    public Payment(Long id, String date, Double value, Loan loan) {
        this.id = id;
        this.date = date;
        this.value = value;
        this.loan = loan;
    }    

    public Long getLoanId() {
        if (this.loan == null) {
            return null;
        }
        
        return this.loan.getIdLoan();
    }
}
