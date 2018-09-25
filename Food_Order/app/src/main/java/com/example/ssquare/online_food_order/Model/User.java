package com.example.ssquare.online_food_order.Model;

/**
 * Created by S square on 03-06-2018.
 */

public class User
{
    private String Name;
    private String Password;
    private String Phone;
    private String secureCode;
    private String IsStaff;
    private String homeAddress;
    private double balance;


    public User()
    {

    }
    public User(String name,String password,String secureCode)
    {
        Name=name;
        Password=password;
        this.secureCode=secureCode;
        IsStaff="false";
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }
}

