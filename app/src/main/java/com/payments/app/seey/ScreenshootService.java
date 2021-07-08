package com.payments.app.seey;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.payments.app.seey.observers.ScreenshotDetection;
import com.payments.app.seey.userrecognition.AppUserRecognition;
import com.payments.app.seey.userrecognition.InstagramUserRecognition;
import com.payments.app.seey.userrecognition.TiktokUserRecognition;
import com.payments.app.seey.userrecognition.TwitterUserRecognition;
import com.payments.app.seey.userrecognition.YoutubeUserRecognition;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ScreenshootService extends Service implements ScreenshotDetection.ScreenshotDetectionListener {
    private Notification notification;
    private ScreenshotDetection screenshotDetection;
    public ScreenshootService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        return START_STICKY;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        String CHANNEL_ID = "my_channel_01";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "Seey",
                NotificationManager.IMPORTANCE_MIN);


        channel.setSound(null,null);

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Seey").setPriority(Notification.PRIORITY_MIN).build();

        startForeground(1, notification);

        screenshotDetection = new ScreenshotDetection(this,this);
        screenshotDetection.startScreenshotDetection();
    }


    private static final String TikTokPackage = "com.ss.android.ugc.trill";
    private static final String TikTokPackage2 = "com.zhiliaoapp.musically";

    private static final String InstagramPackage="com.instagram.android";
    private static final String Twitter ="com.twitter.android";
    private static final String ClubHouse ="com.clubhouse.app";
    private static final String Youtube = "com.google.android.youtube";

    @Override
    public void onScreenCaptured(@NotNull String path) {


        App.Log("Screenshoot " +path);

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        // We get usage stats for the last 10 seconds
        List <UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
        // Sort the stats by the last time used
        if (stats != null) {
            SortedMap< Long, UsageStats > mySortedMap = new TreeMap< Long, UsageStats >();
            for (UsageStats usageStats: stats) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (mySortedMap != null && !mySortedMap.isEmpty()) {
             final  String topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();

                App.Log("Screenshoot for "+topPackageName+" in "+path);
             if(topPackageName.equals(TikTokPackage)
             ||topPackageName.equals(TikTokPackage2)){

                 TiktokUserRecognition userRecognition = new TiktokUserRecognition(ScreenshootService.this,
                         App.TiktokUserName);

                 String userName = userRecognition.findUser( BitmapFactory.decodeFile(path));

                 App.Log("TopPackage Name "+ topPackageName+" userName "+userName );

             }
             else if(topPackageName.equals(InstagramPackage)){

                 search(App.InstagramUserName, path);
             }
             else if(topPackageName.equals(Youtube)){
                 search(App.YoutubeUserName, path);

             }

             else if(topPackageName.equals(Twitter)){
                 search(App.TwitterUserName, path);

             }



            }
        }
      // App.Log("Screenshoot for "+packageName+" in "+path);

       // packageName = getApplicationContext().getRunningAppProcesses().get(1).processName;
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
      //  startActivity(i);

    }

    private void search(final String app,final String path){
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppUserRecognition userRecognition = null;
                if(app.equals(App.InstagramUserName)){

                    userRecognition= new InstagramUserRecognition(ScreenshootService.this,app);
                }
                else if(app.equals(App.YoutubeUserName)){

                    userRecognition= new YoutubeUserRecognition(ScreenshootService.this,app);
                }
                else if(app.equals(App.TwitterUserName)){

                    userRecognition= new TwitterUserRecognition(ScreenshootService.this,app);
                }

                final String userName = userRecognition.findUser( BitmapFactory.decodeFile(path));

                final String social =app;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(ScreenshootService.this,
                                PaymentsActivity.class);
                        intent.putExtra(App.User,userName);
                        intent.putExtra(App.Account,social);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        startActivity(intent);
                    }
                });

            }
        }).start();
    }

    @Override
    public void onScreenCapturedWithDeniedPermission() {

    }
}