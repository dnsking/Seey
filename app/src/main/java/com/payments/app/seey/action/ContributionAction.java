package com.payments.app.seey.action;

public class ContributionAction extends NetworkAction {

    private  String action = "supportaction";
    private String user;
    private String social;
    private String amount;
    private String time;

    public ContributionAction(){}
    public ContributionAction(String time,String user, String social,String amount){
       this.time = time;
        this.user = user;
        this.social = social;
        this.amount = amount;
    }
    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String action) {
        this.action = action;

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSocial() {
        return social;
    }

    public void setSocial(String social) {
        this.social = social;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
