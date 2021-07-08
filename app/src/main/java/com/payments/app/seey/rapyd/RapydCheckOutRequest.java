package com.payments.app.seey.rapyd;

public class RapydCheckOutRequest {

    private double amount;
    private String complete_payment_url;
    private String country;
    private String currency;
    private String error_payment_url;
    private String merchant_reference_id;
    private boolean cardholder_preferred_currency;
    private String language;

    public String getEwallet() {
        return ewallet;
    }

    public void setEwallet(String ewallet) {
        this.ewallet = ewallet;
    }

    private String ewallet;

    private String[] payment_method_types_include;

    public RapydCheckOutRequest(){}
    public RapydCheckOutRequest(double amount){
        this.amount = amount;
         complete_payment_url="http://example.com/complete";
         country="US";
         currency="USD";
         error_payment_url="http://example.com/error";
         merchant_reference_id="950ae8c6-78";
         cardholder_preferred_currency= true;
         language ="en";
        ewallet="ewallet_f387ced54b1d1fb6fee948d7c908da83";
        payment_method_types_include = new String[] {"us_mastercard_card","us_visa_card"};
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getComplete_payment_url() {
        return complete_payment_url;
    }

    public void setComplete_payment_url(String complete_payment_url) {
        this.complete_payment_url = complete_payment_url;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getError_payment_url() {
        return error_payment_url;
    }

    public void setError_payment_url(String error_payment_url) {
        this.error_payment_url = error_payment_url;
    }

    public String getMerchant_reference_id() {
        return merchant_reference_id;
    }

    public void setMerchant_reference_id(String merchant_reference_id) {
        this.merchant_reference_id = merchant_reference_id;
    }

    public boolean isCardholder_preferred_currency() {
        return cardholder_preferred_currency;
    }

    public void setCardholder_preferred_currency(boolean cardholder_preferred_currency) {
        this.cardholder_preferred_currency = cardholder_preferred_currency;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


    public String[] getPayment_method_types_include() {
        return payment_method_types_include;
    }

    public void setPayment_method_types_include(String[] payment_method_types_include) {
        this.payment_method_types_include = payment_method_types_include;
    }

}
