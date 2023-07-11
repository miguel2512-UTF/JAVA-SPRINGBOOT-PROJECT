package com.project.javaproject.models;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user")
public class User implements Serializable {

    @Id
    @GeneratedValue @Getter @Setter
    private Long id;

    @Column @Getter @Setter
    private String email;

    @Column @Getter @Setter
    private String password;

    @Column @Getter @Setter
    private Boolean isActive;

    public User() {
    }

    public User(Long id, String email, String password, Boolean isActive) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
    }
}
