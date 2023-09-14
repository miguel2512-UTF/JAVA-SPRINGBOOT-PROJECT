package com.project.javaproject.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    private Double debtValue;

    @Column(nullable = false)
    @Getter
    @Setter
    private String loanDate;

    @Column(nullable = false)
    @Getter
    @Setter
    private Boolean isPayment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonProperty(access = Access.WRITE_ONLY)
    @Getter
    @Setter
    private User user = new User();

    @OneToMany(mappedBy = "loan", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Getter
    @Setter
    private List<Payment> payments = new ArrayList<Payment>();

    public Loan() {}

    public Loan(Long idLoan, String debtorName, Double loanValue, Double debtValue, String loanDate, Boolean isPayment, User user) {
        this.idLoan = idLoan;
        this.debtorName = debtorName;
        this.loanValue = loanValue;
        this.debtValue = debtValue;
        this.loanDate = loanDate;
        this.isPayment = isPayment;
        this.user = user;
    }

    public Long getUserId() {
        return this.user.getId();
    }

    public void setUserId(Long id) {
        this.user.setId(id);
    }
}
