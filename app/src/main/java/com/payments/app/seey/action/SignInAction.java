package com.payments.app.seey.action;

public class SignInAction extends NetworkAction {

    private  String action = "signIn";
    private String user;
    private String password;
    private String accountType;

    public SignInAction(){}
    public SignInAction(String user, String password,String accountType){
        this.user = user;
        this.password = password;
        this.accountType = accountType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String action) {
        this.action = action;

    }
}
