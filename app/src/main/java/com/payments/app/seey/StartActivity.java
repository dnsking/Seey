package com.payments.app.seey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.payments.app.seey.storage.StorageUtils;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_start);
        if(StorageUtils.GetUserName(this)!=null){
            String accountType = StorageUtils.GetString(App.Account,this);

            Intent intent = new Intent(this,
                    accountType.equals(App.Normal) ? ContributionsActivity.class:
                    IntegrationActivity.class);
            startActivity(intent);
            finish();

        }
        else{
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}