/*    DALAS Main Activity
 *        for Android 
 *      
 *          For campaign #Take_Me_To_The_Summer_2022
 *          Last commit: May 17. 22.
 *          @author:nitrodegen
 *          @contact:gavrilopalalic@protonmail.com
 */
package com.example.dasher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    ImageButton spot;
    ImageButton gmap;
    ImageButton dash;
    TextView city;
    TextView stat;
    TextView temp;
    String ver = "0.0.1";
    TextView time;
    TextView stattime;
    ImageButton settings;
    ImageView wicon;
    String cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.loadLibrary("lowlevel");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 0355);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0355);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        spot = findViewById(R.id.spotify);
        gmap = findViewById(R.id.gmaps);
        dash = findViewById(R.id.dashcam);

        city = findViewById(R.id.city);
        stat = findViewById(R.id.stat);
        temp = findViewById(R.id.weather);
        wicon = findViewById(R.id.tempicon);
        stattime = findViewById(R.id.timestat);
        time = findViewById(R.id.time);
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSettings();

            }
        });


        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(()->{
                    Date date = new Date();
                    Calendar cal =GregorianCalendar.getInstance();
                    cal.setTime(date);
                    int h = cal.get(Calendar.HOUR_OF_DAY);
                    int m = cal.get(Calendar.MINUTE);
                    if(h<=12 && h != 0 && h!= 1 && h!=2 && h!=3 && h!=4){
                        stattime.setText("Good morning!");

                    }
                    else if(h>12 && h<20){
                        stattime.setText("Good afternoon!");
                    }
                    else if(h>20 || h == 0 || h == 1 ||  h == 2 || h==3 || h==4){
                        stattime.setText("Good night!");
                    }
                    if(String.valueOf(h).length() < 2) {

                            time.setText("0"+String.valueOf(h)+":"+String.valueOf(m));

                    }
                    else if(String.valueOf(m).length() < 2){
                        time.setText(String.valueOf(h)+":0"+String.valueOf(m));
                    }
                    else if(String.valueOf(m).length() < 2  && String.valueOf(h).length() < 2){
                        time.setText("0"+String.valueOf(h)+":0"+String.valueOf(m));
                    }
                    else{
                        time.setText(String.valueOf(h)+":"+String.valueOf(m));
                    }




                });

            }
        }, 1000);
        Runtime rt = Runtime.getRuntime();
        String[] cmd = {"cat","/data/local/tmp/city.txt"};
        try {
            Process proc = rt.exec(cmd);
            BufferedReader inp = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String s="";
            String l;
            while((l = inp.readLine())!= null){
                s+=l;
            }
            if(s.contains("No")){
                Runtime rt1 = Runtime.getRuntime();
                String[] cmd1 = {"touch","/data/local/tmp/city.txt"};
                Process proc1 = rt1.exec(cmd1);
                //warn user that he needs to set city !!

            }
            else{
                BufferedReader rd = new BufferedReader(new FileReader("/data/local/tmp/city.txt"));
                String dat;
                String cit = "";
                while((dat = rd.readLine()) != null){
                    cit+=dat;
                    Log.d("ERR",dat);
                }
                cityName=cit;

            }
        } catch (IOException e) {
            Log.d("ALA","ADAW");
        }
        Log.d("ERR",cityName);
        if(cityName.contains(":")){
            String first = cityName.substring(0,cityName.indexOf(":"));
            String second = cityName.substring(cityName.indexOf(":")+1);

            String ff= first.substring(0,1).toUpperCase()+first.substring(1);
            String ff1= second.substring(0,1).toUpperCase()+second.substring(1);
            String fifi = ff+" "+ff1;
            city.setText(fifi);
            String g  = cityName.replace(":","%20");
            cityName=g;
        }
        else{
            String tg = cityName.substring(0,1).toUpperCase()+cityName.substring(1);
            city.setText(tg);
        }

        String resp = GetTemp(cityName);
        String tempva = String.valueOf(Math.round(Double.parseDouble(resp.substring(0,resp.indexOf(":")))-273.15 )    );
        String wstat =resp.substring(resp.indexOf(":")+1);
        wstat.replace('\n',' ');
        wstat.replace('\t',' ');
        temp.setText(tempva+"°");
        stat.setText(wstat);

        if(wstat.contains("lear")){
            wicon.setImageResource(R.mipmap.day_clear);
        }
        else  if(wstat.contains("ew")){
            wicon.setImageResource(R.mipmap.cloudy);
        }
        else if(wstat.contains("cattered")){
            wicon.setImageResource(R.mipmap.day_partial_cloud);
        }
        else if(wstat.contains("roken")){
            wicon.setImageResource(R.mipmap.wind);
        }
        else if(wstat.contains("hower")){
            wicon.setImageResource(R.mipmap.night_full_moon_rain);
        }
        else if(wstat.contains("ain")){
            wicon.setImageResource(R.mipmap.rain);
        }
        else if(wstat.contains("hunderstorm")){
            wicon.setImageResource(R.mipmap.thunder);
        }
        else if(wstat.contains("now")){
            wicon.setImageResource(R.mipmap.snow);
        }
        else if(wstat.contains("ist")){
            wicon.setImageResource(R.mipmap.fog);
        }
        else if(wstat.contains("louds")){
            wicon.setImageResource(R.mipmap.cloudy);
        }





        dash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDash();
            }
        });
        spot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpotf();
            }
        });
        gmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMaps();
            }
        });
    }

    public  native String GetTemp(String nm);
    public native int CheckUpdates(String path);
    public native int UpdateCity(String cc);

    void startSettings(){
        Intent intent = new Intent(this,SettingsPage.class);
        startActivity(intent);
    }
    void startDash(){
        Intent launchIntent = new Intent(this,Dash.class);
        if (launchIntent != null) {
            startActivity(launchIntent);//null pointer check in case package name was not found
        }
    }
    void startSpotf(){
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.spotify.music");
        if (launchIntent != null) {
            startActivity(launchIntent);//null pointer check in case package name was not found
        }
    }
    void startMaps(){
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.maps");
        if (launchIntent != null) {
            startActivity(launchIntent);//null pointer check in case package name was not found
        }
    }
}
