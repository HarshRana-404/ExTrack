package com.harsh.extrack.models;

public class ExpenseModel {
    String name="";
    String person="";
    Double amount=0.;
    String type="";
    String date="";

    public ExpenseModel(String name, String person, Double amount, String type, String date){
        this.name = name;
        this.person = person;
        this.amount = amount;
        this.type = type;
        this.date = date;
    }

    public String getName() {
        return name;
    }
    public String getPerson() {
        return person;
    }
    public Double getAmount() {
        return amount;
    }
    public String getType() {
        return type;
    }
    public String getDate() {
        return date;
    }
}
