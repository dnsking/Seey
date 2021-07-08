package com.payments.app.seey.integration;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory;
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi;
import com.bytedance.sdk.open.tiktok.authorize.model.Authorization;
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.tiktok.common.model.BaseReq;
import com.bytedance.sdk.open.tiktok.common.model.BaseResp;

public class TikTokEntryActivity extends AppCompatActivity implements IApiEventHandler {

    TikTokOpenApi ttOpenApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ttOpenApi= TikTokOpenApiFactory.create(this);
        ttOpenApi.handleIntent(getIntent(),this);
    }
    @Override
    public void onReq(BaseReq req) {
    }
    @Override
    public void onResp(BaseResp resp) {
        if (resp instanceof Authorization.Response)  {
            Authorization.Response response = (Authorization.Response) resp;
            Toast.makeText(this, " code：" + response.errorCode + " errorMessage：" + response.errorMsg, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onErrorIntent(@Nullable Intent intent) {
        Toast.makeText(this, "Intent Error", Toast.LENGTH_LONG).show();
    }
}