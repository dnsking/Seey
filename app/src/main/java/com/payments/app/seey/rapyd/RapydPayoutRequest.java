package com.payments.app.seey.rapyd;

import com.payments.app.seey.action.Beneficiary;

public class RapydPayoutRequest {

    private Beneficiary beneficiary;
    private String  beneficiary_country;
    private String  beneficiary_entity_type;
    private String  description;
    private String  payout_method_type;
    private String  ewallet;
    private String  payout_amount;
    private String  payout_currency;
    private String  sender_country;
    private String  sender_entity_type;
    private String sender_currency;

    public RapydPayoutRequest(){}

    public RapydPayoutRequest(String payout_amount,Beneficiary beneficiary){
        this.payout_amount = payout_amount;
        this.beneficiary = beneficiary;


        beneficiary_country= "US";
        beneficiary_entity_type= "individual";
                description= "desc1562234632";
                payout_method_type= "us_atmdebit_card";
                ewallet= "ewallet_f387ced54b1d1fb6fee948d7c908da83";
                payout_currency= "USD";
                sender_country= "US";
                sender_currency= "USD";
                sender_entity_type= "individual";
    }


    public Beneficiary getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(Beneficiary beneficiary) {
        this.beneficiary = beneficiary;
    }

    public String getBeneficiary_country() {
        return beneficiary_country;
    }

    public void setBeneficiary_country(String beneficiary_country) {
        this.beneficiary_country = beneficiary_country;
    }

    public String getBeneficiary_entity_type() {
        return beneficiary_entity_type;
    }

    public void setBeneficiary_entity_type(String beneficiary_entity_type) {
        this.beneficiary_entity_type = beneficiary_entity_type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPayout_method_type() {
        return payout_method_type;
    }

    public void setPayout_method_type(String payout_method_type) {
        this.payout_method_type = payout_method_type;
    }

    public String getEwallet() {
        return ewallet;
    }

    public void setEwallet(String ewallet) {
        this.ewallet = ewallet;
    }

    public String getPayout_amount() {
        return payout_amount;
    }

    public void setPayout_amount(String payout_amount) {
        this.payout_amount = payout_amount;
    }

    public String getPayout_currency() {
        return payout_currency;
    }

    public void setPayout_currency(String payout_currency) {
        this.payout_currency = payout_currency;
    }

    public String getSender_country() {
        return sender_country;
    }

    public void setSender_country(String sender_country) {
        this.sender_country = sender_country;
    }

    public String getSender_entity_type() {
        return sender_entity_type;
    }

    public void setSender_entity_type(String sender_entity_type) {
        this.sender_entity_type = sender_entity_type;
    }

    public String getSender_currency() {
        return sender_currency;
    }

    public void setSender_currency(String sender_currency) {
        this.sender_currency = sender_currency;
    }
}
