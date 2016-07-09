package com.floriantoenjes.analyzer.model;

import javax.persistence.*;

@Entity
public class Country {
    @Id
    private String code;

    @Column
    private String name;

    @Column
    private double adultLiteracyRate;

    @Column
    private double internetUsers;

    public Country() {}

    public Country(CountryBuilder cb) {
        this.code = cb.code;
        this.name = cb.name;
        this.adultLiteracyRate = cb.adultLiteracyRate;
        this.internetUsers = cb.internetUsers;
    }

    public Country(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Country(String code, String name, double adultLiteracyRate, double internetUsers) {
        this.code = code;
        this.name = name;
        this.adultLiteracyRate = adultLiteracyRate;
        this.internetUsers = internetUsers;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAdultLiteracyRate() {
        return adultLiteracyRate;
    }

    public void setAdultLiteracyRate(double adultLiteracyRate) {
        this.adultLiteracyRate = adultLiteracyRate;
    }

    public double getInternetUsers() {
        return internetUsers;
    }

    public void setInternetUsers(double internetUsers) {
        this.internetUsers = internetUsers;
    }

    @Override
    public String toString() {
        return code + " " + name + " " + adultLiteracyRate + " " + internetUsers;
    }

    public static class CountryBuilder {
        String code;
        String name;
        double adultLiteracyRate;
        double internetUsers;

        public CountryBuilder(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public CountryBuilder withAdultLiteracyRate(double alr) {
            this.adultLiteracyRate = alr;
            return this;
        }

        public CountryBuilder withInternetUsers(double intUsers) {
            this.internetUsers = intUsers;
            return this;
        }

        public Country build() {
            return new Country(this);
        }
    }
}




