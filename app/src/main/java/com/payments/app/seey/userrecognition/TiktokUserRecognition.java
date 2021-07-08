package com.payments.app.seey.userrecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Base64;

import com.google.gson.Gson;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.payments.app.seey.App;
import com.payments.app.seey.action.OcrAction;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class TiktokUserRecognition extends AppUserRecognition{
    public TiktokUserRecognition(Context context,String app) {
        super(context,app);
    }

    private Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
    @Override
    public String findUser(Bitmap screenshotColor) {

       // Bitmap screenshot = toGrayscale(screenshotColor);
        int section = screenshotColor.getHeight()/4;
        int top = screenshotColor.getHeight()-section;
        Bitmap screenshot = Bitmap.createBitmap(screenshotColor, 0,top,
                screenshotColor.getWidth(), section);

        File fileName =new File(getDataDirPath(),RandomStringUtils.random(12,true,false)) ;
        //screenshot.compress()

        try (FileOutputStream out = new FileOutputStream(fileName)) {
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
          String result =  ocr(fileName.getAbsolutePath());
          return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(getDataDirPath(),"eng");
       // baseApi.set
        baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);

        baseApi.setImage(screenshot);
       String texts= baseApi.getUTF8Text();

       String result = null;
        for (String text:texts.split(" ")
             ) {
            if(text.startsWith("@")){
                result = text;
                break;
            }
        }
        return texts;
    }





}
