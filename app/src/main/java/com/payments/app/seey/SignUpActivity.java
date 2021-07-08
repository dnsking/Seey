package com.payments.app.seey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.payments.app.seey.action.SignInAction;
import com.payments.app.seey.storage.StorageUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {

    private String accountType;
    private TextInputEditText phoneNumbertextField,passwordtextField;
    private Button signIn;
    private ProgressBar progressBar;
    private RadioButton normal,content;
    private boolean isSuccess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        phoneNumbertextField = findViewById(R.id.phoneNumbertextField);
        passwordtextField = findViewById(R.id.passwordtextField);
        normal = findViewById(R.id.normal);
        content = findViewById(R.id.content);

        signIn = findViewById(R.id.actionBtn);

        progressBar = findViewById(R.id.progressBar);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneNumbertextField.getText()!=null&&
                        passwordtextField.getText()!=null)
                startSignup(phoneNumbertextField.getText().toString(),
                        passwordtextField.getText().toString()
                        ,normal.isChecked()?App.Normal:App.ContentCreator
                );
            }
        });
    }

    private void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
        phoneNumbertextField.setEnabled(false);
        passwordtextField.setEnabled(false);
        signIn.setEnabled(false);
    }
    private void hideProgress(){
        progressBar.setVisibility(View.GONE);
        phoneNumbertextField.setEnabled(true);
        passwordtextField.setEnabled(true);
        signIn.setEnabled(true);
    }
    private void startSignup(String username, String password,String accountType){
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    String result= signUp( username,  password,accountType);
                    isSuccess = result.equals("success");
                    App.Log("isSuccess "+isSuccess);
                }
                catch (Exception ex){}

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        if(isSuccess){

                            StorageUtils.SaveString(App.User,
                                    SignUpActivity.this,username);
                            StorageUtils.SaveString(App.Account,
                                    SignUpActivity.this,accountType);

                            Intent intent = new Intent(SignUpActivity.this,
                                    accountType.equals(App.Normal) ? ContributionsActivity.class:
                                            IntegrationActivity.class);
                            startActivity(intent);

                            overridePendingTransition(
                                    R.anim.slide_in,R.anim.slide_out);

                            finish();
                        }
                        else{
                            Toast.makeText(SignUpActivity.this,
                                    "Failed To Sign Up",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
    private String signUp(String username, String password,String accountType) throws IOException {
        SignInAction signInAction = new SignInAction();
        signInAction.setUser(username);
        signInAction.setPassword(password);
        signInAction.setAccountType(accountType);
        signInAction.setAction("signup");

        OkHttpClient client2 =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

        Gson gson = new Gson();

        String json = gson.toJson(signInAction);
        //  App.Log("json");
        // App.Log(json);
        Request requestaction = new Request.Builder()
                .url(App.Url).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();
        Response response = client2.newCall(requestaction).execute();

        String searhTruResponse = response.body().string().replaceAll("^\"+|\"+$", "");;
        App.Log("signUp "+searhTruResponse);
        //  AddTruthAction result = new Gson().fromJson(searhTruResponse,AddTruthAction.class);
        return searhTruResponse;
    }
}