/*    DALAS Settings activity
 *        for Android 
 *      
 *          For campaign #Take_Me_To_The_Summer_2022
 *          Last commit: May 17. 22.
 *          @author:nitrodegen
 *          @contact:gavrilopalalic@protonmail.com
 */

package com.example.dasher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

public class SettingsPage extends AppCompatActivity {
    EditText cityName;
    Button back;
    Button confirm;
    Button checkUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.loadLibrary("lowlevel");
        setContentView(R.layout.activity_settings_page);
        cityName = findViewById(R.id.cityName);
        back = findViewById(R.id.back);
        confirm= findViewById(R.id.setCity);
        checkUpdates = findViewById(R.id.checkUpdates);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               UpdateCity();

            }
        });
        checkUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkupdate();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMain();
            }
        });

    }
    void checkupdate(){
        MainActivity t = new MainActivity();
        int resp = t.CheckUpdates(t.ver);
        if(resp == -1){
            checkUpdates.setText("Software is out-of date!");
        }
        else{
            checkUpdates.setText("Software is up-to date.") ;
        }
    }
    void UpdateCity(){
        String name =cityName.getText().toString();
        name = name.replace(" ",":");
        name = name.toLowerCase();
        Log.d("CITY", name);
        MainActivity tt = new MainActivity();
        int res = tt.UpdateCity(name);
        if(res == 1){
            cityName.setText("Successfully updated.");
        }
        else{
            cityName.setText("Can't  update.");
        }
    }
    void backToMain(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}
