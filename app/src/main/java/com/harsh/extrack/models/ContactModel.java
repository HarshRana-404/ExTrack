package com.harsh.extrack.models;

public class ContactModel {
    String person="";
    Double balance=0.;

    public ContactModel(String person, Double balance){
        this.person = person;
        this.balance = balance;
    }

    public String getPerson() {
        return person;
    }
    public Double getBalance() {
        return balance;
    }
}
