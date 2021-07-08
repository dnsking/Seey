package com.payments.app.seey.userrecognition;

import android.content.Context;
import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ClubHouseUserRecognition extends AppUserRecognition{
    private Bitmap screenshot;


    public ClubHouseUserRecognition(Context context,String app) {
        super(context,app);
    }

    @Override
    public String findUser(Bitmap screenshot) {


        Mat template = new Mat();
        Mat matScreenshot = new Mat();
        Mat result = new Mat();


        Bitmap bmp32 = screenshot.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, matScreenshot);

        Imgproc.matchTemplate(matScreenshot,template,result,Imgproc.TM_CCOEFF);
        Core.MinMaxLocResult minMaxLocResult =Core.minMaxLoc(result);
        org.opencv.core.Point topLeft = minMaxLocResult.maxLoc;




      //  matchTemplate();
        return null;
    }
}
