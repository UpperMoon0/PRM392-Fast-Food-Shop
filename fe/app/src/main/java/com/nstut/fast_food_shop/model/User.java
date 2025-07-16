package com.nstut.fast_food_shop.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String email;
    private String password;
    private String fullname;
    private String address;
    private String phone;
    private String role;

    public User() {
    }

    public User(String email, String password, String fullname, String address, String phone, String role) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.address = address;
        this.phone = phone;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}