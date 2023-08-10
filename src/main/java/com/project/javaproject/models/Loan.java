package com.project.javaproject.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "loan")
public class Loan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long idLoan;

    @Column(nullable = false)
    @Getter
    @Setter
    private String debtorName;

    @Column(nullable = false)
    @Getter
    @Setter
    private Double loanValue;

    @Column(nullable = false)
    @Getter
    @Setter
    private String loanDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonProperty(access = Access.WRITE_ONLY)
    @Getter
    @Setter
    private User user;

    public Loan() {}

    public Loan(Long idLoan, String debtorName, Double loanValue, String loanDate,
            User user) {
        this.idLoan = idLoan;
        this.debtorName = debtorName;
        this.loanValue = loanValue;
        this.loanDate = loanDate;
        this.user = user;
    }

    public Long getUserId() {
        return this.user.getId();
    }
}
