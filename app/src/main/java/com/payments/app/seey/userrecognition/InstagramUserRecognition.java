package com.payments.app.seey.userrecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.payments.app.seey.App;

import org.apache.commons.lang3.RandomStringUtils;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class InstagramUserRecognition extends AppUserRecognition {
    public InstagramUserRecognition(Context context,String app) {
        super(context,app);
    }

    @Override
    public String findUser(Bitmap screenshot) {

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(getDataDirPath(),"eng");
        // baseApi.set
        baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);

        Mat template = new Mat();
        Mat matScreenshot = new Mat();
        Mat result = new Mat();


        Bitmap bmp32 = screenshot.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, matScreenshot);

        Mat gray = new Mat();
        Imgproc.cvtColor(matScreenshot, gray, Imgproc.COLOR_BGR2GRAY);

        Mat circles = new Mat();
        double minDist = 100;
        // higher threshold of Canny Edge detector, lower threshold is twice smaller
      //  double p1UpperThreshold = 200;
        double p1UpperThreshold = 80;
        // the smaller it is, the more false circles may be detected
        double p2AccumulatorThreshold = 40;
        int minRadius = 20;
        int maxRadius = 100;
        // use gray image, not edge detected

        Imgproc.HoughCircles(gray, circles, Imgproc.CV_HOUGH_GRADIENT, 1,
                minDist, p1UpperThreshold, p2AccumulatorThreshold, minRadius, maxRadius);


        double max = Double.MAX_VALUE;
        int resultRadius = 0;
        Point resultPoint = null;
        for (int x = 0; x < circles.cols(); x++) {
            double[] c1xyr = circles.get(0, x);
            Point xy = new Point(Math.round(c1xyr[0]), Math.round(c1xyr[1]));
            int radius = (int) Math.round(c1xyr[2]);

            if(xy.y<max){
                resultRadius =radius;
                resultPoint =xy;
                max=xy.y;
            }
        }



            Bitmap sectionBitmap = Bitmap.createBitmap(screenshot, 0,
                    (int)  resultPoint.y-resultRadius,
            screenshot.getWidth(), resultRadius*2);


            baseApi.setImage(sectionBitmap);
            String texts= baseApi.getUTF8Text();



            File fileName =new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), RandomStringUtils.random(12,true,false)+".png") ;

            try (FileOutputStream out = new FileOutputStream(fileName)) {
                sectionBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (IOException e) {
                e.printStackTrace();
            }

            String resultk="";
            try {
                 resultk =  ocr(fileName.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
            App.Log("found text point: "+resultPoint.toString()+"" +
                    " radius:"+resultRadius+" values: "+texts+" aws:"+resultk);
          //  int radius = (int) Math.round(c1xyr[2]);
        //    Core.circle(detected, xy, radius, new Scalar(0, 0, 255), 3);



        /*

        Imgproc.matchTemplate(matScreenshot,template,result,Imgproc.TM_CCOEFF);
        Core.MinMaxLocResult minMaxLocResult =Core.minMaxLoc(result);
        org.opencv.core.Point topLeft = minMaxLocResult.maxLoc;

*/

        return resultk;
    }


}
