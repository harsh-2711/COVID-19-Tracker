package com.developer.abhinavraj.covid19tracker.model;

import java.io.Serializable;

public class Country implements Comparable, Serializable {

    private String countryName;
    private String countryCode;
    private int activeCases;
    private int totalCases;
    private int totalRecovered;
    private int newCases;
    private int totalDeaths;
    private int seriouslyCritical;
    private int newDeaths;
    private String totalCasesPerMillion;

    public Country(String countryName, String countryCode, int totalCases, int totalRecovered, int activeCases, int totalDeaths,
                   int newCases, int newDeaths, int seriouslyCritical, String totalCasesPerMillion) {
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.totalCases = totalCases;
        this.totalRecovered = totalRecovered;
        this.activeCases = activeCases;
        this.totalDeaths = totalDeaths;
        this.newCases = newCases;
        this.newDeaths = newDeaths;
        this.seriouslyCritical = seriouslyCritical;
        this.totalCasesPerMillion = totalCasesPerMillion;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getActiveCases() {
        return activeCases;
    }

    public void setActiveCases(int activeCases) {
        this.activeCases = activeCases;
    }

    public int getNewCases() {
        return newCases;
    }

    public void setNewCases(int newCases) {
        this.newCases = newCases;
    }

    public int getSeriouslyCritical() {
        return seriouslyCritical;
    }

    public void setSeriouslyCritical(int seriouslyCritical) {
        this.seriouslyCritical = seriouslyCritical;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getNewDeaths() {
        return newDeaths;
    }

    public void setNewDeaths(int newDeaths) {
        this.newDeaths = newDeaths;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(int totalCases) {
        this.totalCases = totalCases;
    }

    public String getTotalCasesPerMillion() {
        return totalCasesPerMillion;
    }

    public void setTotalCasesPerMillion(String totalCasesPerMillion) {
        this.totalCasesPerMillion = totalCasesPerMillion;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(int totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public int getTotalRecovered() {
        return totalRecovered;
    }

    public void setTotalRecovered(int totalRecovered) {
        this.totalRecovered = totalRecovered;
    }

    @Override
    public int compareTo(Object o) {
        int compareTotalCases = ((Country) o).getTotalCases();
        return compareTotalCases - this.totalCases;
    }
}
