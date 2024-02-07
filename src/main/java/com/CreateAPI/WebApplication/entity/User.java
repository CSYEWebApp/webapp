package com.CreateAPI.WebApplication.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @NotBlank
    private String username;

    @NotBlank
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private LocalDateTime accountCreated;
    private LocalDateTime accountUpdated;

    @PrePersist
    public  void  create(){
        LocalDateTime time = LocalDateTime.now();
        accountCreated = time;
        accountUpdated = time;
    }

    @PreUpdate
    public void update(){
        accountUpdated = LocalDateTime.now();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public String getFirstName() {

        return firstName;
    }

    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {

        this.lastName = lastName;
    }

    public LocalDateTime getAccountCreated() {

        return accountCreated;
    }

    public void setAccountCreated(LocalDateTime accountCreated) {

        this.accountCreated = accountCreated;
    }

    public LocalDateTime getAccountUpdated() {

        return accountUpdated;
    }

    public void setAccountUpdated(LocalDateTime accountUpdated) {

        this.accountUpdated = accountUpdated;
    }

    public UUID getId() {

        return id;
    }

    public void setId(UUID id) {

        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", accountCreated=" + accountCreated +
                ", accountUpdated=" + accountUpdated +
                '}';
    }

    public User(String username, String password, String firstName, String lastName, LocalDateTime accountCreated, LocalDateTime accountUpdated, UUID id) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountCreated = accountCreated;
        this.accountUpdated = accountUpdated;
        this.id = id;
    }

    public User() {
    }



}
