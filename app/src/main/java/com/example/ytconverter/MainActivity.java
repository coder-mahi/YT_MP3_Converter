package com.example.ytconverter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.app.DownloadManager;
import android.widget.TextView;
import android.widget.Toast;


import java.net.URI;

public class MainActivity extends AppCompatActivity {
    Button convertbtn,downloadbtn;
    AppCompatEditText edittextlink;
    ImageButton pastebtn;
    TextView convertlink_textview;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        convertbtn = findViewById(R.id.convert_btn);
        downloadbtn = findViewById(R.id.download_btn);
        pastebtn = findViewById(R.id.paste_btn);
        edittextlink = findViewById(R.id.textlink);
        convertlink_textview = findViewById(R.id.convertlink_textview);
        //getting copied text form clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//        }
        pastebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clipboard.hasPrimaryClip()) {
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    edittextlink.setText(item.getText().toString());
                }
            }
        });
        convertbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String linktxt = edittextlink.getText().toString().trim();
                if(!TextUtils.isEmpty(linktxt)){
                    convertlink_textview.setText(linktxt);
                    convertlink_textview.setVisibility(View.VISIBLE);
                    downloadbtn.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(MainActivity.this, "Please enter valid YT link", Toast.LENGTH_SHORT).show();
                }
            }
        });
        downloadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ytlink = convertlink_textview.getText().toString().trim();
                if(!TextUtils.isEmpty(ytlink)){
                    startDownload(ytlink);
                    Toast.makeText(MainActivity.this, "Downloading....!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "No link available for download..!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void convertToMp3(String txt){

    }
    private void startDownload(String downloadUri){
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(downloadUri);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"donloaded_audio.mp3");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
    }
}