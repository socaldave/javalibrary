package com.example.library.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String address;
    private String phoneNumber;

    @ElementCollection
    @CollectionTable(name = "member_loans", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "loan_id")
    private List<Long> loanIds;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Long> getLoanIds() {
        return loanIds;
    }

    public void setLoanIds(List<Long> loanIds) {
        this.loanIds = loanIds;
    }
}
