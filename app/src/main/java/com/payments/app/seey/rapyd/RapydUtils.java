package com.payments.app.seey.rapyd;

import android.util.Base64;

import com.google.gson.Gson;
import com.payments.app.seey.App;
import com.payments.app.seey.action.Beneficiary;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RapydUtils {

    public static void Payout(double amount, Beneficiary beneficiary){

        String checkoutUrl = App.Rapyd.Base_URL+"/payouts";

        RapydPayoutRequest rapydCheckOutRequest
                = new RapydPayoutRequest(Double.toString(amount),beneficiary);
        String body = new Gson().toJson(rapydCheckOutRequest);

        RapydHeader rapydHeader =GenUrlAuth(checkoutUrl, body);


        OkHttpClient client =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(checkoutUrl)
                .addHeader("Content-Type","application/json")
                .addHeader("access_key",rapydHeader.getAccess_key())
                .addHeader("salt",rapydHeader.getSalt())
                .addHeader("timestamp",rapydHeader.getTimestamp())
                .addHeader("signature",rapydHeader.getSignature())
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body))
                .build();
        try {
            Response response = client.newCall(request).execute();
            String rr = response.body().string();
            App.Log("Payout "+rr);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public static String CreateCheckOut(double amount){
        String checkoutUrl = App.Rapyd.Base_URL+"/checkout";
        RapydCheckOutRequest rapydCheckOutRequest
                = new RapydCheckOutRequest(amount);
        String body = new Gson().toJson(rapydCheckOutRequest);

        RapydHeader rapydHeader =GenUrlAuth(checkoutUrl, body);


        OkHttpClient client =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(checkoutUrl)
                .addHeader("Content-Type","application/json")
                .addHeader("access_key",rapydHeader.getAccess_key())
                .addHeader("salt",rapydHeader.getSalt())
                .addHeader("timestamp",rapydHeader.getTimestamp())
                .addHeader("signature",rapydHeader.getSignature())
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body))
                .build();
        try {
            Response response = client.newCall(request).execute();
           String rr = response.body().string();
                    App.Log("CreateCheckOut "+rr);

            JSONObject jObj = new JSONObject(rr);
            String url = jObj.getJSONObject("data").getString("redirect_url");
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static RapydHeader GenUrlAuth(String url,String body){
        RapydHeader rapydHeader = new RapydHeader();



      long timestamp = System.currentTimeMillis()/ 1000L;

      String signature_salt = givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect();
      String secret = App.Rapyd.Secret;

        String to_sign = "post/v1/checkout" + signature_salt + Long.toString(timestamp) + App.Rapyd.Access + secret + body;

      // String signature= generateHashWithHmac256(to_sign, App.Rapyd.Secret);
        String signature=hmacDigest(to_sign, App.Rapyd.Secret,"HmacSHA256");

        rapydHeader.setAccess_key(App.Rapyd.Access);
        rapydHeader.setSalt(signature_salt);
        try {
            rapydHeader.setSignature(Base64.encodeToString(signature.getBytes("UTF-8"), Base64.NO_WRAP));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        rapydHeader.setTimestamp(Long.toString(timestamp));

        return rapydHeader;
    }

    private static String generateHashWithHmac256(String message, String key) {
        try {
            final String hashingAlgorithm = "HmacSHA256"; //or "HmacSHA1", "HmacSHA512"

            byte[] bytes = hmac(hashingAlgorithm, key.getBytes(), message.getBytes());

            final String messageDigest = bytesToHex(bytes);


            return messageDigest;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] hmac(String algorithm, byte[] key, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(message);
    }

    /*
    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0, v; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
*/




    public static String hash256(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }

    public static String bytesToHex(byte[]bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt: bytes)
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }

    public static String hmacDigest(String msg, String keyString, String algo) {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("ASCII"), algo);
            Mac mac = Mac.getInstance(algo);
            mac.init(key);

            byte[]bytes = mac.doFinal(msg.getBytes("ASCII"));

            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException e) {
            System.out.println("hmacDigest UnsupportedEncodingException");
        }
        catch (InvalidKeyException e) {
            System.out.println("hmacDigest InvalidKeyException");
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println("hmacDigest NoSuchAlgorithmException");
        }
        return digest;
    }

    public static String givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect() {
        int leftLimit = 97;   // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char)randomLimitedInt);
        }
        String generatedString = buffer.toString();

        return (generatedString);
    }
    /*

    public static void main(String[]args)throws Exception {
        try {
            System.out.println("GetPOS Start");
            String salt = givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect(); // Randomly generated for each request.
            long timestamp = System.currentTimeMillis() / 1000L; // Unix time.
            String accessKey = "AAAAAAAAAAA";                    // The access key received from Rapyd.
            String secretKey = "SSSSSSSSSSS";                    // Never transmit the secret key by itself.
            String toEnc = "get/v1/data/countries" + salt + Long.toString(timestamp) + accessKey + secretKey;
            System.out.println("String TO BE Encrypted::" + toEnc);
            String StrhashCode = hmacDigest("get/v1/data/countries" + salt +
                    Long.toString(timestamp) +
                    accessKey +
                    secretKey, "HmacSHA256");
            String signature = Base64.getEncoder().encodeToString(StrhashCode.getBytes());
            HttpClient httpclient = HttpClients.createDefault();

            try {
                HttpGet httpget = new HttpGet("https://sandboxapi.rapyd.net/v1/data/countries");

                httpget.addHeader("Content-Type", "application/json");
                httpget.addHeader("access_key", accessKey);
                httpget.addHeader("salt", salt);
                httpget.addHeader("timestamp", Long.toString(timestamp));
                httpget.addHeader("signature", signature);

                // Create a custom response handler
                ResponseHandler < String > responseHandler = new ResponseHandler < String > () {
                    @ Override
                    public String handleResponse(
                            final HttpResponse response)throws ClientProtocolException,
                            IOException {
                        int status = response.getStatusLine().getStatusCode();
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    }
                };
                String responseBody = httpclient.execute(httpget, responseHandler);
                System.out.println("----------------------------------------");
                System.out.println(responseBody);
            }
            finally {
            }
        } catch (Exception e) {
        }
    }*/
}
