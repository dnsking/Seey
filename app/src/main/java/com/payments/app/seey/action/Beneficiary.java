package com.payments.app.seey.action;

import com.zaferayan.creditcard.model.CreditCard;

public class Beneficiary {
    private String email;
    private String card_number;
    private String card_expiration_month;
    private String card_expiration_year;
    private String card_cvv;

    public Beneficiary(){
        email = "sandboxtest@rapyd.net";
    }
    
    public Beneficiary( CreditCard creditCard){

        email = "sandboxtest@rapyd.net";

         card_number=creditCard.getNumber();
         card_expiration_month=creditCard.getExpirationMonth();
         card_expiration_year=creditCard.getExpirationYear();
         card_cvv=creditCard.getCvv();
        
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getCard_expiration_month() {
        return card_expiration_month;
    }

    public void setCard_expiration_month(String card_expiration_month) {
        this.card_expiration_month = card_expiration_month;
    }

    public String getCard_expiration_year() {
        return card_expiration_year;
    }

    public void setCard_expiration_year(String card_expiration_year) {
        this.card_expiration_year = card_expiration_year;
    }

    public String getCard_cvv() {
        return card_cvv;
    }

    public void setCard_cvv(String card_cvv) {
        this.card_cvv = card_cvv;
    }
}
