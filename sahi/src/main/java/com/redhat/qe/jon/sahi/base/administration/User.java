package com.redhat.qe.jon.sahi.base.administration;

import java.util.ArrayList;

public class User {
    private String name = null;
    private String password = null;
    private String firsName = null;
    private String lastName = null;
    private String email = null;
    private String phoneNumber = null;
    private String department = null;
    private boolean loginEnabled = true;
    private ArrayList<String> roleNames = new ArrayList<String>();
    
    
    public User(String userName, String password, String firstName, String lastName, String email){
        this.name = userName;
        this.password = password;
        this.firsName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    public String getName() {
        return name;
    }
    public void setName(String userName) {
        this.name = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getFirsName() {
        return firsName;
    }
    public void setFirsName(String firsName) {
        this.firsName = firsName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
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
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public boolean isLoginEnabled() {
        return loginEnabled;
    }
    public void setLoginEnabled(boolean loginEnabled) {
        this.loginEnabled = loginEnabled;
    }
    public ArrayList<String> getRoleNames() {
        return roleNames;
    }
    public void setRoleNames(ArrayList<String> roleNames) {
        this.roleNames = roleNames;
    }
}
