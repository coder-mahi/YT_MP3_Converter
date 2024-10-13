package com.example.ytconverter;

import static android.content.Context.CLIPBOARD_SERVICE;


import android.Manifest;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    Button convertbtn, downloadbtn;
    AppCompatEditText edittextlink;
    ImageButton pastebtn;
    TextView convertlink_textview;
    private YouTubeToMp3Api apiService;

    private static final int PERMISSION_REQUEST_CODE = 100;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        edittextlink = view.findViewById(R.id.textlink);
        convertbtn = view.findViewById(R.id.convert_btn);
        convertlink_textview = view.findViewById(R.id.convertlink_textview);
        downloadbtn = view.findViewById(R.id.download_btn);
        pastebtn = view.findViewById(R.id.paste_btn);
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
        checkPermissions();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.102.79:3000/") // Replace with your API base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(YouTubeToMp3Api.class);
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
            public void onClick(View v) {
                String youtubeLink = edittextlink.getText().toString().trim();
                if (!TextUtils.isEmpty(youtubeLink)) {
                    convertLinkToMp3(youtubeLink);
                } else {
                    Toast.makeText(getActivity(), "Please enter a valid YouTube link", Toast.LENGTH_SHORT).show();
                }
            }
        });

        downloadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String downloadUrl = convertlink_textview.getText().toString().trim();
                if (!TextUtils.isEmpty(downloadUrl)) {
                    startDownload(downloadUrl);
                } else {
                    Toast.makeText(getActivity(), "No link available for download", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void startDownload(String downloadUrl) {
        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "downloaded_audio.mp3");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("audio/mpeg");
        downloadManager.enqueue(request);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void convertLinkToMp3(String youtubeLink) {
        Call<ConversionResponse> call = apiService.convertYoutubeLink(youtubeLink);
        call.enqueue(new Callback<ConversionResponse>() {
            @Override
            public void onResponse(Call<ConversionResponse> call, Response<ConversionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String downloadUrl = response.body().getDownloadUrl();
                    convertlink_textview.setText(downloadUrl);
                    convertlink_textview.setVisibility(View.VISIBLE);
                    downloadbtn.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getActivity(), "Conversion failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ConversionResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}