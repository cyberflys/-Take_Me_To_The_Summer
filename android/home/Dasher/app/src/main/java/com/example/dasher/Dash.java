
/*    DALAS Eye detection activity
 *        for Android 
 *      
 *          For campaign #Take_Me_To_The_Summer_2022
 *          Last commit: May 17. 22.
 *          @author:nitrodegen
 *          @contact:gavrilopalalic@protonmail.com
 */
package com.example.dasher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.Image;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.schema.Model;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Dash extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 , ImageAnalysis.Analyzer{
    CameraBridgeViewBase cbvb;
    BaseLoaderCallback blc;
    com.example.dasher.ml.Model model;
    Mat fifi;
    TextView tim;
    Mat comb;
    List<MatOfPoint> points;
    TextView bat;
    Mat gray;
    float x,y;
    float r,l,t,b;
    Mat right;
    FaceDetector detector;
    TextView signdetect;
    Mat left;
    FrontCam cam;
    ImageButton backTomain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        tim = findViewById(R.id.timestamp);


        bat = findViewById(R.id.batstat);
        backTomain= findViewById(R.id.backToMain);
        backTomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMainStuff();

            }
        });

        FaceDetectorOptions realTimeOpts =
                new FaceDetectorOptions.Builder()
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .build();

        try {
             model = com.example.dasher.ml.Model.newInstance(this);
        } catch (IOException e) {
            e.printStackTrace();
        }


        signdetect = findViewById(R.id.signdetect);
        cbvb = (JavaCamera2View) findViewById(R.id.camview);
        detector = FaceDetection.getClient(realTimeOpts);
        cam = new FrontCam();


        cbvb.setVisibility(SurfaceView.VISIBLE);
       // signdetect.setText(detector.getDetectorType());
        cbvb.setCvCameraViewListener(this);
        cbvb.setCameraIndex(1);
        blc = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);
                switch(status){
                    case BaseLoaderCallback.SUCCESS:
                        System.gc();

                        cbvb.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
        Timer updateBat = new Timer();
        updateBat.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(()->{
                   getBatteryTemp();
                });
            }
        },1000,1000);

        Timer timeupd = new Timer();
        updateBat.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(()->{
                    timeupdt();
                });
            }
        },900,900);


    }


    void timeupdt(){
        Date date = new Date();
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date);
        String h = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        String m = String.valueOf(cal.get(Calendar.MINUTE));
        if(String.valueOf(h).length() < 2) {

            tim.setText("0"+String.valueOf(h)+":"+String.valueOf(m));

        }
        else if(String.valueOf(m).length() < 2){
            tim.setText(String.valueOf(h)+":0"+String.valueOf(m));
        }
        else if(String.valueOf(m).length() < 2  && String.valueOf(h).length() < 2){
            tim.setText("0"+String.valueOf(h)+":0"+String.valueOf(m));
        }
        else{
            tim.setText(String.valueOf(h)+":"+String.valueOf(m));
        }
    }

    void getBatteryTemp()
    {
        Intent intt = this.registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int tmp =((int)intt.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0))/10;
        bat.setText(String.valueOf(tmp));

    }
    void backToMainStuff(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"Couldn't start dash", Toast.LENGTH_SHORT).show();
        }
        else
        {
            left = new Mat();
            gray = new Mat();


            blc.onManagerConnected(blc.SUCCESS);
        }
    }
   
    private float PredictImage(Bitmap img){
        float pred = 0;


        ByteBuffer buffer = ByteBuffer.allocate(4*32*32);
        buffer.order(ByteOrder.nativeOrder());

        int[] vals = new int[32*32];
        img.getPixels(vals,0,img.getWidth(),0,0,img.getWidth(),img.getHeight());
        int pix = 0;
        for(int i =0;i<32;i++){
            for(int j=0;j<32;j++){
                int val = vals[pix++];
                buffer.putFloat((float)Color.red(val)/255.f);
            }
        }

        // Creates inputs for reference.
        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 32, 32, 1}, DataType.FLOAT32);

        inputFeature0.loadBuffer(buffer);


        // Runs model inference and gets result.
        com.example.dasher.ml.Model.Outputs outputs = model.process(inputFeature0);

        TensorBuffer outputFeature0 = ((com.example.dasher.ml.Model.Outputs) outputs).getOutputFeature0AsTensorBuffer();
        float[] c = outputFeature0.getFloatArray();

        float pos = c[0];
        double conf = 0.96;
        int re =0;

        for(int i =1;i<c.length;i++){
            if(c[i] >pos){
                pos = c[i];
                re=i;
            }
        }
        return re;

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat frame = inputFrame.rgba();
        Mat test = frame.clone();
        Mat shit = frame.clone();

        Imgproc.resize(test,gray,new Size(100,100));
        Bitmap bit = Bitmap.createBitmap(gray.cols(),gray.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(gray,bit);
        InputImage img = InputImage.fromBitmap(bit,0);

        Task<List<Face>> result =
                detector.process(img).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                    @Override
                    public void onSuccess(List<Face> faces) {

                            if(faces.size()> 0){
                               for(Face face:faces){
                                   Rect bounds = face.getBoundingBox();

                                   FaceLandmark eye = face.getLandmark(FaceLandmark.LEFT_EYE);

                                   if(eye != null){

                                       PointF pos =eye.getPosition();
                                       x = (int) pos.x;
                                       y = (int) pos.y;
                                       break;

                                   }
                               }


                            }


                    }
                });
        //better eye cropping  - need to do
        if(x>1  && y > 1) {

            x = (float) (x*20);
            y= (float) (y*11);

            r = x-80;
            l = x+60;
            t = y-80;
            b = y+100;
            //Imgproc.rectangle(test, new Point((x * 20) - 80, (y * 10) - 30), new Point((x * 20) + 60, (y * 10) + 100), new Scalar(150, 150, 0), 3);

            Imgproc.rectangle(test, new Point(l, t), new Point(r, b), new Scalar(150, 150, 0), 3);
            org.opencv.core.Rect rect =new org.opencv.core.Rect((int)Math.round(l),(int)Math.round(b),(int)Math.round(Math.abs(r-l)),(int)Math.round(Math.abs(b-t)));
            if(0<=rect.width && 0<=rect.height & 0<=rect.x && 0<=rect.y && rect.x+rect.width <= test.cols() && rect.y+rect.height <=test.rows()) {
                Mat f = shit.submat(rect);
                Log.d("T", String.valueOf(r-l));
                Log.d("T", String.valueOf(b-t));



                Mat cropped = new Mat();
                Imgproc.resize(f,cropped,new Size(32,32));

                Imgproc.cvtColor(cropped, left, Imgproc.COLOR_BGR2GRAY);
                Imgproc.equalizeHist(left, left);
                Bitmap bit1 = Bitmap.createBitmap(left.cols(), left.rows(), Bitmap.Config.RGB_565);
                Utils.matToBitmap(left, bit1);
                Bitmap sc = Bitmap.createScaledBitmap(bit1, 32, 32, false);

                Float pred = PredictImage(sc);
                if (pred == 1) {
                    signdetect.setText("KEEP YOUR EYES ON THE ROAD!");

                } else {
                    signdetect.setText("");
                }

                sc.recycle();;
                bit1.recycle();
                cropped.release();
                f.release();


            }
            x=0;
            y=0;

        }
        else{
            signdetect.setText("");
        }



        gray.release();;
        left.release();
        frame.release();;
        shit.release();

        return test;


    }
    @Override
    public void onCameraViewStopped() {

    }
    @Override
    protected void onPause() {
        super.onPause();
        if(cbvb!=null){

            cbvb.disableView();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cbvb!=null){
            cbvb.disableView();
        }
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {


    }
}
