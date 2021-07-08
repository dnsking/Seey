package com.payments.app.seey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory;
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi;
import com.bytedance.sdk.open.tiktok.authorize.model.Authorization;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.gson.Gson;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.payments.app.seey.action.ContributionAction;
import com.payments.app.seey.action.LinkAction;
import com.payments.app.seey.action.OcrAction;
import com.payments.app.seey.fragments.PayoutFragment;
import com.payments.app.seey.storage.StorageUtils;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class IntegrationActivity extends AppCompatActivity

        implements EasyPermissions.PermissionCallbacks{


    TwitterLoginButton loginButton;


    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call YouTube Data API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { YouTubeScopes.YOUTUBE_READONLY };


    private View linkedInstagram;
    private View unlinkedInstagram;
    private ImageView imgViewInstagram;
    private TextView userNameInstagram;
    private TextView amountInstagram,totalContribution;

    private View linkedYoutube;
    private View unlinkedYoutube;
    private ImageView imgViewYoutube;
    private TextView userNameYoutube;
    private TextView amountYoutube;

    private View linkedTwitter;
    private View unlinkedTwitter;
    private ImageView imgViewTwitter;
    private TextView userNameTwitter;
    private TextView amountTwitter;



    @Override
    protected void attachBaseContext(Context newBase) {
        // GoogleMaterial.Icon.gmd_edit
        //  WeatherIcons.Icon.wic_forecast_io_wind
        //   FontAwesome.Icon.faw_comments2
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent()!=null&&getIntent().getData()!=null){

            Uri uri = getIntent().getData();
            try {
                URL url = new URL(uri.getScheme(), uri.getHost(), uri.getPath());

                String code = uri.getQueryParameter("code");

                App.Log("Intent url "+url.toString()
                        +" code "+code);
                continueConnectInstagram( code);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }


        setContentView(R.layout.activity_integration);

        linkedInstagram = findViewById(R.id.linkedInstagram);
        unlinkedInstagram = findViewById(R.id.unlinkedInstagram);
        imgViewInstagram = findViewById(R.id.imgViewInstagram);
        userNameInstagram = findViewById(R.id.userNameInstagram);
        amountInstagram = findViewById(R.id.amountInstagram);


        linkedYoutube = findViewById(R.id.linkedYoutube);
        unlinkedYoutube = findViewById(R.id.unlinkedYoutube);
        imgViewYoutube = findViewById(R.id.imgViewYoutube);
        userNameYoutube = findViewById(R.id.userNameYoutube);
        amountYoutube = findViewById(R.id.amountYoutube);



        linkedTwitter = findViewById(R.id.linkedTwitter);
        unlinkedTwitter = findViewById(R.id.unlinkedTwitter);
        imgViewTwitter = findViewById(R.id.imgViewTwitter);
        userNameTwitter = findViewById(R.id.userNameTwitter);
        amountTwitter = findViewById(R.id.amountTwitter);

        totalContribution = findViewById(R.id.totalContribution);



        init();
        checkIntegration();

//        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
    }
    private void init(){

        Twitter.initialize(this);


        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Connecting YouTube");


        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        findViewById(R.id.withDrawButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PayoutFragment.newInstance().show(getSupportFragmentManager(),"1");
            }
        });
        findViewById(R.id.connectYoutube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                connectYoutube();
            }
        });
        findViewById(R.id.connectInstagram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                connectInstagram();
            }
        });
        findViewById(R.id.connectTwitter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                connectTwitter();
            }
        });



    }
    private void connectTwitter(){

         loginButton = new TwitterLoginButton(this);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> resultMain) {

                TwitterCore.getInstance().getApiClient(resultMain.data)
                        .getAccountService().verifyCredentials(true, true, false).enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {



                        mProgress = new ProgressDialog(IntegrationActivity.this);
                        mProgress.setMessage("Connecting Twitter");
                        mProgress.show();
                      final  String profilePicture   = result.data.profileImageUrl.replace("http","https");
                        final String username   =  resultMain.data.getUserName();

                        App.Log("connectTwitter profilePicture "+ profilePicture);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try{

                                    saveUser( profilePicture, username,App.TwitterUserName);
                                }
                                catch (Exception ex){

                                    App.Log("connectTwitter failed "+ ex.toString());
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void failure(TwitterException exception) {

                    }
                });

                //https://twitter.com/[screen_name]/profile_image?size=original
             //   result.data.
                App.Log("connectTwitter success "+ resultMain.data.getUserName());

              /*  Call<User> userResult=Twitter.getInstance()..getApiClient(result).getAccountService().verifyCredentials(true,false);
                userResult.enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        User user = userResult.data;
                        String profileImage= user.profileImageUrl;
                    }

                    @Override
                    public void failure(TwitterException exception) {

                    }
                });*/

            }

            @Override
            public void failure(TwitterException exception) {

                App.Log("connectTwitter failure "+ exception.getMessage());

            }
        });
        loginButton.performClick();
    }
    
    private void connectInstagram(){

        mProgress.dismiss();
        String request_url = "https://api.instagram.com/oauth/authorize?client_id=317516206735709&redirect_uri=https://httpstat.us/200&scope=user_profile,user_media&response_type=code";

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(request_url));
    }


    private void continueConnectInstagram(String code){

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Connecting Instagram");
        mProgress.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try{

                    OkHttpClient client2 =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();


                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("client_id", "317516206735709")
                            .addFormDataPart("client_secret", "02102e574e7244f82bf3493d65a1f482")
                            .addFormDataPart("grant_type", "authorization_code")
                            .addFormDataPart("redirect_uri", "https://httpstat.us/200")
                            .addFormDataPart("code", code)
                            .build();
                    Request requestaction = new Request.Builder()
                            .url("https://api.instagram.com/oauth/access_token")
                            .post(requestBody)
                            .build();
                    Response response = client2.newCall(requestaction).execute();

                    String codeResult = response.body().string();

                    App.Log("continueConnectInstagram "+codeResult
                    );

                    JSONObject jObj = new JSONObject(codeResult);
                    String user_id = jObj.getString("user_id");
                    String access_token = jObj.getString("access_token");
                    fetchInstagramData(   user_id,  access_token );
                }
                catch (Exception ex){
                    App.Log("Instagram failed "+ex.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgress.dismiss();
                            Toast.makeText(IntegrationActivity.this,
                                    "Failed",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();


    }

    private void fetchInstagramData(  String user_id, String access_token ) throws IOException, JSONException {
        OkHttpClient client2 =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

        Request requestaction = new Request.Builder()
                .url("https://graph.instagram.com/v11.0/"+user_id+"?fields=id,username&access_token="+access_token)
                .get()
                .build();

        Response response = client2.newCall(requestaction).execute();
        String result = response.body().string();
        App.Log("fetchInstagramData "+result);


        JSONObject jObj = new JSONObject(result);
        final String username = jObj.getString("username");

        App.Log("fetchInstagramData username "+username);

        String profilePicture =getInstagramProPic(username);

        String result2 =  saveUser( profilePicture, username,App.InstagramUserName);
        App.Log("fetchInstagramData link "+result2);



    }
    private String saveUser(final String profilePicture,
                            final String username,final String type) throws IOException {
        OkHttpClient client2 =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

        byte[] imageBytes = IOUtils.toByteArray(new URL(profilePicture));
        String base64 = Base64.encodeToString(imageBytes, Base64.DEFAULT) ;
        LinkAction linkAction = new LinkAction( username,type,base64);

        Request requestaction2 = new Request.Builder()
                .url(App.Url).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(linkAction)))
                .build();

        Response response2 = client2.newCall(requestaction2).execute();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                StorageUtils.SaveString(type,
                        IntegrationActivity.this,   username);
                if(mProgress!=null)
                mProgress.dismiss();
                checkIntegration();
            }
        });
        return response2.body().string().replaceAll("^\"+|\"+$", "");

    }
    private double getAmountFor(String user,String social) throws IOException {
        ContributionAction contributionAction = new ContributionAction(
                Long.toString(System.currentTimeMillis()),
                user,  social, "0"
        );
        contributionAction.setAction("getsupport");

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
        return Double.parseDouble(searhTruResponse);
    }
    private double totalAmount;
    private void checkIntegration(){

        totalAmount = 0;
        boolean hasAnyIntegration = false;
        final String instagramUser = StorageUtils.GetString(App.InstagramUserName,this);
        final String youtubeUser = StorageUtils.GetString(App.YoutubeUserName,this);
        final String twitterUser = StorageUtils.GetString(App.TwitterUserName,this);


        if(instagramUser!=null){
            hasAnyIntegration = true;
            linkedInstagram.setVisibility(View.VISIBLE);
            unlinkedInstagram.setVisibility(View.GONE);
            userNameInstagram.setText("@"+instagramUser);
            amountInstagram.setText("$0");


            Glide.with(this)
                    .load(Uri.parse(App.BucketUrl+instagramUser+App.InstagramUserName)) // or URI/path
                    .into(imgViewInstagram );
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{

                        final double amount =getAmountFor(instagramUser,App.InstagramUserName);

                        totalAmount+=amount;


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                amountInstagram.setText("$"+ NumberFormat.getNumberInstance(Locale.US).format(amount));

                                totalContribution.setText("$"+ NumberFormat.getNumberInstance(Locale.US).format(totalAmount));
                            }
                        });
                    }
                    catch (Exception ex){}
                }
            }).start();

        }

        if(youtubeUser!=null){
            hasAnyIntegration = true;
            linkedYoutube.setVisibility(View.VISIBLE);
            unlinkedYoutube.setVisibility(View.GONE);
            userNameYoutube.setText("@"+youtubeUser);
            amountYoutube.setText("$0");


            Glide.with(this)
                    .load(Uri.parse(App.BucketUrl+youtubeUser+App.YoutubeUserName)) // or URI/path
                    .into(imgViewYoutube );

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{

                        final double amount =getAmountFor(youtubeUser,App.YoutubeUserName);
                        totalAmount+=amount;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                amountYoutube.setText("$"+ NumberFormat.getNumberInstance(Locale.US).format(amount));
                                totalContribution.setText("$"+ NumberFormat.getNumberInstance(Locale.US).format(totalAmount));
                            }
                        });
                    }
                    catch (Exception ex){}
                }
            }).start();

        }

        if(twitterUser!=null){
            hasAnyIntegration = true;
            linkedTwitter.setVisibility(View.VISIBLE);
            unlinkedTwitter.setVisibility(View.GONE);
            userNameTwitter.setText("@"+twitterUser);
            amountTwitter.setText("$0");


            Glide.with(this)
                    .load(Uri.parse(App.BucketUrl+instagramUser+App.TwitterUserName)) // or URI/path
                    .into(imgViewTwitter);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{

                        final double amount =getAmountFor(twitterUser,App.TwitterUserName);
                        totalAmount+=amount;


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                amountTwitter.setText("$"+ NumberFormat.getNumberInstance(Locale.US).format(amount));
                                totalContribution.setText("$"+ NumberFormat.getNumberInstance(Locale.US).format(totalAmount));
                            }
                        });


                    }
                    catch (Exception ex){}
                }
            }).start();


        }
        if(hasAnyIntegration){
            findViewById(R.id.empty).setVisibility(View.GONE);
            findViewById(R.id.contributions).setVisibility(View.VISIBLE);
        }
    }


    private String getInstagramProPic(String user) throws IOException, JSONException {
       /* OkHttpClient client2 =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

        Request requestaction = new Request.Builder()
                .addHeader("content-type","application/json")
                .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 12_3_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 Instagram 105.0.0.11.118 (iPhone11,8; iOS 12_3_1; en_US; en-US; scale=2.00; 828x1792; 165586599")
                .addHeader("Accept","application/json")

                .url("https://www.instagram.com/"+user+"/?__a=1")
                .get()
                .build();

        Response response = client2.newCall(requestaction).execute();
        String result = response.body().string();*/

        URL url = new URL("https://www.instagram.com/"+user+"/?__a=1");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(false);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.connect();
        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String result = "", line;
        while ((line = rd.readLine()) != null) {
            result += line + "\n";
        }



        App.Log("getInstagramProPic "+result);


        JSONObject jObj = new JSONObject(result);
        return jObj.getJSONObject("graphql")
                .getJSONObject("user").getString("profile_pic_url_hd");
    }



    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void connectYoutube() {
        if (! isGooglePlayServicesAvailable()) {
            App.Log("acquireGooglePlayServices");
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            App.Log("chooseAccount "+mCredential.getSelectedAccountName() );
            chooseAccount();
        } else if (! isDeviceOnline()) {

            App.Log("isDeviceOnline");
       //     mOutputText.setText("No network connection available.");
        } else {
            App.Log("IntegrationActivity");
            new IntegrationActivity.MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            startActivityForResult(
                    mCredential.newChooseAccountIntent(),
                    REQUEST_ACCOUNT_PICKER);

           /* if (accountName != null) {
                App.Log("chooseAccount accountName != null");
                mCredential.setSelectedAccountName(accountName);
                connectYoutube();
            } else {

                App.Log("chooseAccount startActivityForResult");
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }*/
        } else {

            App.Log("chooseAccount requestPermissions");
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                  /*  mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
               */ } else {
                    connectYoutube();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        connectYoutube();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    connectYoutube();
                }
                break;
            default:

                loginButton.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                IntegrationActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("YouTube Data API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call YouTube Data API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {

            try {
                return getDataFromApi();
            } catch (Exception e) {
                App.Log("Get channels error "+e.toString());
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch information about the "GoogleDevelopers" YouTube channel.
         * @return List of Strings containing information about the channel.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // Get a list of up to 10 files.
            List<String> channelInfo = new ArrayList<String>();
            ChannelListResponse result = mService.channels().list("snippet,contentDetails,statistics")
                    .setMine(true)
                    .execute();
            List<Channel> channels = result.getItems();
            if (channels != null) {
                Channel channel = channels.get(0);


                saveUser( channel.getSnippet().getThumbnails().getHigh().getUrl(), channel.getSnippet().getTitle(),App.YoutubeUserName);
             String thumbUrl=   channel.getSnippet().getThumbnails().getHigh().getUrl();
                App.Log("Channel title "+ channel.getSnippet().getTitle()
                +" views "+ channel.getStatistics().getViewCount()
                +" thumbUrl "+thumbUrl);
                channelInfo.add("This channel's ID is " + channel.getId() + ". " +
                        "Its title is '" + channel.getSnippet().getTitle() + ", " +

                        "and it has " + channel.getStatistics().getViewCount() + " views.");
            }
            return channelInfo;
        }

        @Override
        protected void onPreExecute() {
        //    mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();


            if (output == null || output.size() == 0) {
            //    mOutputText.setText("No results returned.");
            } else {
                output.add(0, "Data retrieved using the YouTube Data API:");
              //  mOutputText.setText(TextUtils.join("\n", output));
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                  //  mOutputText.setText("The following error occurred:\n"   + mLastError.getMessage());
                }
            } else {
             //   mOutputText.setText("Request cancelled.");
            }
        }
    }

    private void verifyTwitter(){
        /*loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                //String token = authToken.token;
                //  String secret = authToken.secret;

                loginMethod(session);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(getApplicationContext(),"Login fail",Toast.LENGTH_LONG).show();
            }
        });*/
    }
    private void verifyTiktok(){
        TikTokOpenApi tiktokOpenApi= TikTokOpenApiFactory.create(this);

// 2. Create Authorization.Request instance
        Authorization.Request request = new Authorization.Request();
        request.scope = "user.info.basic";
        request.state = "xxx";
//        return tiktokOpenApi.authorize(request);

// 3. Start Authorization
        tiktokOpenApi.authorize(request);
    }
}