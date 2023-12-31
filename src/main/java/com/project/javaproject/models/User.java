package com.project.javaproject.models;

import java.io.Serializable;
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
@Table(name = "user")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Column(nullable = false, unique = true)
    @Getter
    @Setter
    private String email;

    @Column(nullable = false)
    @Getter
    @Setter
    private String password;

    @Column(nullable = false)
    @Getter
    @Setter
    private Boolean isActive;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Getter
    @Setter
    private List<Loan> loans = new ArrayList<Loan>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonProperty(access = Access.WRITE_ONLY)
    @Getter
    @Setter
    private Role role = new Role();

    public User() {
    }

    public User(Long id, String email, String password, Boolean isActive) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
    }

    public String getRoleName() {
        if (this.role == null) {
            return null;
        }

        return this.role.getName();
    }
}
