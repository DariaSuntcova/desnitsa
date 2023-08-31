package ru.desnitsa.backend.entities;

import org.springframework.data.annotation.Id;

import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Table("users")
public class User {
    @Id
    Long id;
    String username;
    String password;
    String fullName;
    String email;
    String phoneNumber;
    @MappedCollection(idColumn = "user_id")
    Set<Role> roles;

    public User(Long id, String username, String password, String fullName, String email, String phoneNumber, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
    }

    public static User of(String username, String password, String fullName, String email, String phoneNumber, Set<Role> roles) {
        return new User(null, username, password, fullName, email, phoneNumber, roles);
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
