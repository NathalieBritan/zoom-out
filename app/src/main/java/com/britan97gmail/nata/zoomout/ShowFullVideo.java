package com.britan97gmail.nata.zoomout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;

public class ShowFullVideo extends AppCompatActivity {


    private VideoView video;
    private Uri myVideoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_video);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Context context = getBaseContext();
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected)
        {
            video = (VideoView) findViewById(R.id.videoView);
            video.setMediaController(new android.widget.MediaController(this));
            video.setKeepScreenOn(true);
            myVideoUri= Uri.parse("http://vid53.photobucket.com/albums/g67/NathalieBritan/ZoomOut/full_zpsz2rlcokl.mp4");
            video.setVideoURI(myVideoUri);
            video.start();
            video.requestFocus();

            final Intent intent = new Intent(this, MainMenu.class);
            video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    startActivity(intent);
                    ShowFullVideo.this.finish();
                }
            });
        }
        else
        {
            Toast.makeText(this, "Check your Internet connection", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //stop video when app is stopped
    @Override
    public void onStop()
    {
        super.onStop();
        video.pause();
    }
}
