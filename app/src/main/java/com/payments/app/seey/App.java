package com.payments.app.seey;

import android.app.Application;
import android.util.Log;

import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory;
import com.bytedance.sdk.open.tiktok.TikTokOpenConfig;
import com.mikepenz.iconics.Iconics;
import com.rapyd.rapyd_card_capture.custom_views.RapydTextInputLayout;
import com.rapydsdk.entities.RPDPaymentRequest;
import com.rapydsdk.ppackages.RPDPaymentManager;
import com.rapydsdk.ppackages.RPDSdk;
import com.rapydsdk.ppackages.RPDTask;

import org.opencv.android.OpenCVLoader;

import pl.tajchert.nammu.Nammu;

public class App extends Application {

    public static String Url = "https://55fpgk1qu9.execute-api.us-east-1.amazonaws.com/SocialTruthStage";

    public static class Rapyd {
        public static final String Base_URL = "https://sandboxapi.rapyd.net/v1";
        public static final String Access = "CBB0F2E49B9BE9B85122";
        public static final String Secret = "cac76d0453937d884966ccd0016b5d33b6321f6f6203faeb72cc5bd5e9bd6d454bad061e7a57ea31";
    }

    public static boolean IsDebug = true;
    private static final String TAG = "Seey Client";
    public static final String Content = "Content";

    public static final String BucketUrl ="https://socialtruthbucket.s3.amazonaws.com/";





    public static  final String Prefs = "com.payments.app.seey.prefs";
    public static final String User = "User";
    public static final String Password = "Password";
    public static final String Account = "Account";


    public static final String ContentCreator = "ContentCreator";
    public static final String Normal = "Normal";

    public static final String InstagramUserName = "Instagram";
    public static final String YoutubeUserName = "Youtube";
    public static final String TiktokUserName = "Tiktok";
    public static final String TwitterUserName = "Twitter";
    public static final String ClubHouseUserName = "ClubHouse";

    @Override
    public void onCreate() {
        super.onCreate();


        Iconics.init(this);

        Nammu.init(this);
        OpenCVLoader.initDebug();
        String clientKey = "[CLIENT_KEY]";
        TikTokOpenConfig tiktokOpenConfig = new TikTokOpenConfig(clientKey);
        TikTokOpenApiFactory.init(tiktokOpenConfig);

       RPDSdk.setup(this,"CBB0F2E49B9BE9B85122","cac76d0453937d884966ccd0016b5d33b6321f6f6203faeb72cc5bd5e9bd6d454bad061e7a57ea31");
      //Rp
    }

    public static void Log(String msg){
        if(App.IsDebug){
            int maxLogSize = 1000;
            if(msg.length()>maxLogSize)
                for(int i = 0; i <= msg.length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i+1) * maxLogSize;
                    end = end > msg.length() ? msg.length() : end;
                    Log.i(TAG, msg.substring(start, end));
                }
            else
                Log.i(TAG, msg);
        }
    }
}
