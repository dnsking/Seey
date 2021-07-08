package com.payments.app.seey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.payments.app.seey.action.OcrAction;
import com.payments.app.seey.action.SignInAction;
import com.payments.app.seey.storage.StorageUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private String accountType;
    private TextInputEditText phoneNumbertextField,passwordtextField;
    private Button signIn,signUp;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneNumbertextField = findViewById(R.id.phoneNumbertextField);
        passwordtextField = findViewById(R.id.passwordtextField);

        signIn = findViewById(R.id.actionBtn);
        signUp = findViewById(R.id.actionBtn2);

        progressBar = findViewById(R.id.progressBar);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gotoSignUp();
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(phoneNumbertextField.getText()!=null&&
                        passwordtextField.getText()!=null)
                startSignIn(phoneNumbertextField.getText().toString(),
                        passwordtextField.getText().toString());
            }
        });
    }
    private void gotoSignUp(){

        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(
                R.anim.slide_in,R.anim.slide_out);

        finish();
    }
    private void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
        phoneNumbertextField.setEnabled(false);
        passwordtextField.setEnabled(false);
        signIn.setEnabled(false);
        signUp.setEnabled(false);
    }
    private void hideProgress(){
        progressBar.setVisibility(View.GONE);
        phoneNumbertextField.setEnabled(true);
        passwordtextField.setEnabled(true);
        signIn.setEnabled(true);
        signUp.setEnabled(true);
    }
    private void startSignIn(String username, String password){
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {

                 accountType=null;
                try{
                   String result= signIn( username,  password);
                   if(!result.equals("fail"))
                       accountType = result;
                }
                catch (Exception ex){}

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        if(accountType!=null){

                            StorageUtils.SaveString(App.User,
                                    LoginActivity.this,username);
                            StorageUtils.SaveString(App.Account,
                                    LoginActivity.this,accountType);

                            Intent intent = new Intent(LoginActivity.this,
                                    accountType.equals(App.Normal) ? ContributionsActivity.class:
                                            IntegrationActivity.class);
                            startActivity(intent);

                            overridePendingTransition(
                                    R.anim.slide_in,R.anim.slide_out);

                            finish();
                        }
                        else{
                            Toast.makeText(LoginActivity.this,
                                    "Failed To Log In",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
    private String signIn(String username, String password) throws IOException {
        SignInAction signInAction = new SignInAction();
        signInAction.setUser(username);
        signInAction.setPassword(password);

        OkHttpClient client2 =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

        Gson gson = new Gson();

        String json = gson.toJson(signInAction);
        //  App.Log("json");
        // App.Log(json);
        Request requestaction = new Request.Builder()
                .url(App.Url).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();
        Response response = client2.newCall(requestaction).execute();

        String searhTruResponse = response.body().string().replaceAll("^\"+|\"+$", "");
        App.Log("searhTruResponse "+searhTruResponse);
        //  AddTruthAction result = new Gson().fromJson(searhTruResponse,AddTruthAction.class);
        return searhTruResponse;
    }
}