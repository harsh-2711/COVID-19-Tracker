package com.developer.abhinavraj.covid19tracker.model;

public class Helpline {

    private String stateName;
    private String number;

    public Helpline(String stateName, String number) {
        this.stateName = stateName;
        this.number = number;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
