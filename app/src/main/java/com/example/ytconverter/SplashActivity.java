package com.example.ytconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {
    private ProgressBar pb;
    int pstatus = 0;
    private int pbmax = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pb =(ProgressBar) findViewById(R.id.progressbar);
        while(pstatus < pbmax){
            pstatus += 10;
            pb.setProgress(pstatus);
            pb.setMax(100);
        }
        if(pstatus == pbmax){
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            showToast("100% completed");
            startActivity(i);
        }
    }
    private void showToast(String msg){
        Toast t = Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG);
        t.show();
    }
}