package com.payments.app.seey.action;

public class OcrAction extends NetworkAction {
    private String action = "ocr";
    private String Key;
    public OcrAction(){}
    public OcrAction(String Key){
        this.Key = Key;
    }
    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String action) {

    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }
}
