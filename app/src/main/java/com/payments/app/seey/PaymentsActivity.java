package com.payments.app.seey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.payments.app.seey.action.ContributionAction;
import com.payments.app.seey.action.OcrAction;
import com.payments.app.seey.rapyd.RapydPaymentActivity;
import com.payments.app.seey.rapyd.RapydUtils;
import com.payments.app.seey.storage.db.helpers.ContributionsDBHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymentsActivity extends AppCompatActivity
  implements View.OnClickListener{

    private EditText inputEditText;
    private Button[] inputButtons;
    private  int[] buttonsIds;

    private String amount = "";
    private String userName;
    private String social;

    private ImageView userImgView;
    private TextView userNameTextView;
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_payments);

        userName = getIntent().getStringExtra(App.User);
        social = getIntent().getStringExtra(App.Account);

        userImgView = findViewById(R.id.userImgView);
        userNameTextView = findViewById(R.id.userNameTextView);

        userNameTextView.setText(userName);

        Glide.with(this)
                .load(Uri.parse(App.BucketUrl+userName.replace("@","")+social)) // or URI/path
                .into(userImgView );

        inputEditText = findViewById(R.id.input);
        inputButtons = new Button[10];

       buttonsIds = new int[]{
                R.id.inputBtn0,   R.id.inputBtn1,   R.id.inputBtn2
                ,   R.id.inputBtn3,   R.id.inputBtn4,   R.id.inputBtn5
                ,   R.id.inputBtn6,   R.id.inputBtn7,   R.id.inputBtn8
                ,   R.id.inputBtn9

        };
        for (int i=0;i<buttonsIds.length;i++) {
            inputButtons[i] = findViewById(buttonsIds[i]);
            inputButtons[i].setOnClickListener(this);
        }

        findViewById(R.id.inputBtnDot).setOnClickListener(this);
        findViewById(R.id.inputBtnBackspace).setOnClickListener(this);
        findViewById(R.id.sendMoneyBtn).setOnClickListener(this);
       // doPayment();
    }
    private void loadPage(String url){
         webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(webViewClient);


       // webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
    }

    public class JavaScriptInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }

    WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("https://www.rapyd.net/")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{

                            sendSupport( amount);
                        }
                        catch (Exception ex){
                            App.Log("sendSupport error "+ex.toString());
                        }
                    }
                }).start();
                finish();
                return true;
            }

            App.Log("page shouldOverrideUrlLoading "+url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            App.Log("page loaded "+url);
            webView.setVisibility(View.VISIBLE);
            if(url.equals("https://www.rapyd.net/")){

                finish();
            }
            /*if (url.contains("code=")) {
                Uri uri = Uri.EMPTY.parse(url);
                String access_token = uri.getEncodedFragment();
                access_token = access_token.substring(access_token.lastIndexOf("=") + 1);
                App.Log("Instagram key "+access_token);
            }*/
        }
    };
    private void sendSupport(String amount) throws IOException {
        ContributionAction contributionAction = new ContributionAction(
                Long.toString(System.currentTimeMillis()),
              userName,  social, amount
        );

        OkHttpClient client2 =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

        Gson gson = new Gson();



        String json = gson.toJson(contributionAction);
        //  App.Log("json");
        // App.Log(json);
        Request requestaction = new Request.Builder()
                .url(App.Url).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();
        Response response = client2.newCall(requestaction).execute();

        String searhTruResponse = response.body().string().replaceAll("^\"+|\"+$", "").replaceAll("\\\\", "");
        App.Log("sendSupport "+searhTruResponse);

        ContributionsDBHelper contributionsDBHelper = new ContributionsDBHelper(getApplicationContext());
        contributionsDBHelper.open();
        contributionsDBHelper.insertEntry(contributionAction);
        contributionsDBHelper.close();

    }
    private void doPayment(final String amount){

        findViewById(R.id.sendMoneyBtn).setEnabled(false);
        findViewById(R.id.ani).setVisibility(View.VISIBLE);
        userNameTextView.setVisibility(View.INVISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {

                String sendAmount = amount;

                if(!sendAmount.contains(".")){
                    sendAmount = sendAmount+".01";
                }
              final String url= new RapydUtils().CreateCheckOut(Double.parseDouble(sendAmount));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(PaymentsActivity.this,
                                RapydPaymentActivity.class);
                        intent.putExtra(App.Content,url);
                        loadPage(url);
                    //    startActivity(intent);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        ArrayList<Integer> ids = new ArrayList<Integer>(buttonsIds.length);
        for (int i : buttonsIds)
        {
            ids.add(i);
        }

        if(ids.contains(id)){
            String adding = Integer.toString(ids.indexOf(id));
            amount +=adding;
            inputEditText.setText("$"+amount);
        }
        else if(id==R.id.inputBtnDot){
            if(!amount.contains(".")){

                amount +=".";
                inputEditText.setText("$"+amount);
            }
        }

        else if(id==R.id.inputBtnBackspace){

            amount =removeLast(amount);
            inputEditText.setText("$"+amount);
        }
        else if(id==R.id.sendMoneyBtn){

            doPayment(amount);
        }



    }

    public String removeLast(String str) {
        if (str != null && str.length() > 0 ) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
}