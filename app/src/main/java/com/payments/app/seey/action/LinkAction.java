package com.payments.app.seey.action;

public class LinkAction extends NetworkAction{

    private String user;
    private String social;
    private String img;

    private final String action = "link";

    public LinkAction(){}
    public LinkAction( String user,String social,String img){
        this.user = user;
        this.social = social;
        this.img = img;
    }
    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String action) {

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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
