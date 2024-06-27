package com.example.ytconverter;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YouTubeToMp3Api {
    @GET("/convert")
    Call<ConversionResponse> convertYoutubeLink(@Query("url") String youtubeUrl);
}
