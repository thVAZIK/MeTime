package com.example.metime.Models;

public class PaymentMethodDetails {
    private String cvv;
    private String card_number;
    private String expire_date;
    private String cardholder_name;

    public PaymentMethodDetails(String cvv, String cardNumber, String expireDate, String cardholderName) {
        this.cvv = cvv;
        this.card_number = cardNumber;
        this.expire_date = expireDate;
        this.cardholder_name = cardholderName;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }

    public String getCardholder_name() {
        return cardholder_name;
    }

    public void setCardholder_name(String cardholder_name) {
        this.cardholder_name = cardholder_name;
    }
}
