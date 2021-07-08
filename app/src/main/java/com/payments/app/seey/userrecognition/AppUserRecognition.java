package com.payments.app.seey.userrecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import com.google.gson.Gson;
import com.payments.app.seey.App;
import com.payments.app.seey.action.OcrAction;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class AppUserRecognition {

    protected Context context;
    protected String TessData="tessdata";
    private String dataName = "eng.traineddata";
    private String app;
    public AppUserRecognition( Context context,String app){
        this.context = context;
        this.app = app;


        if(!hasDataExist()){
            copyTessdata();
        }
    }

    protected    String ocr(String path) throws Exception {


        OkHttpClient client2 =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

        Gson gson = new Gson();

        byte[] bytes =org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(path));


        String json = gson.toJson(new OcrAction( Base64.encodeToString(bytes, Base64.DEFAULT)));
        //  App.Log("json");
        // App.Log(json);
        Request requestaction = new Request.Builder()
                .url(App.Url).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();
        Response response = client2.newCall(requestaction).execute();

        String searhTruResponse = response.body().string().replaceAll("^\"+|\"+$", "").replaceAll("\\\\", "");
        App.Log("searhTruResponse "+searhTruResponse);

        String[] datas = new Gson().fromJson(searhTruResponse, String[].class)
                ;
        String returnResult = "";

        if(app.equals(App.YoutubeUserName) || app.equals(App.InstagramUserName)){

            for (String data:
                    datas
            ) {

                returnResult = data.split(" ")[0];
                break;

            }

            returnResult = datas[0];
        }
        else if(app.equals(App.TwitterUserName)){

            for (String data:
                    datas
            ) {

                if(data.startsWith("@")){

                    returnResult = data;
                    break;
                }

            }

        }
        //  AddTruthAction result = new Gson().fromJson(searhTruResponse,AddTruthAction.class);
        return returnResult;
    }

    private void copyTessdata(){
      File myAppDir = new File(context.getCacheDir(),TessData) ;
        myAppDir.mkdirs();

        copyAssetFile( context, dataName, new File(myAppDir,dataName).getAbsolutePath());

    }
    private boolean hasDataExist(){

        File myAppDir = new File(context.getCacheDir(),TessData) ;
        return new File(myAppDir,dataName).exists();
    }
    protected String getDataDirPath(){

        File myAppDir = context.getCacheDir() ;
        App.Log("getDataDirPath "+myAppDir.getAbsolutePath());
        return myAppDir.getAbsolutePath();
    }

    public  boolean copyAssetFolder(Context context, String srcName, String dstName) {
        try {
            boolean result = true;
            String fileList[] = context.getAssets().list(srcName);
            if (fileList == null) return false;

            if (fileList.length == 0) {
                result = copyAssetFile(context, srcName, dstName);
            } else {
                File file = new File(dstName);
                result = file.mkdirs();
                for (String filename : fileList) {
                    result &= copyAssetFolder(context, srcName + File.separator + filename, dstName + File.separator + filename);
                }
            }
            return result;
        } catch ( IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public  boolean copyAssetFile(Context context, String srcName, String dstName) {
        try {
            InputStream in = context.getAssets().open(srcName);
            File outFile = new File(dstName);
            OutputStream out = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public abstract String findUser(Bitmap screenshot);
}
