package com.britan97gmail.nata.zoomout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;


public class ZoomInOut extends AppCompatActivity {

    private Context context;

    private TextView textView; //to print the info
    private ImageView imageView; //to shoe\w pictures refered to info
    private ScrollView infoBox; // contains imageView and textView
    private ImageButton btnInfo; //to show the info about object
    private ImageButton btnMusicOnOff; //to control sound

    private int flagCurrPart = 1; //current part must be played

    private ImageView imageViewSlideShow;
    //AudioPlayer
    private MediaPlayer mediaPlayer;

    //ids for text (it'd better to create an arrays.xml, but I'm too lazy for this stuff=))
    private int[] arrayTexts = {R.string.mars, R.string.jupiter, R.string.saturn,
            R.string.bisystem, R.string.gas, R.string.milky, R.string.andromeda, R.string.quasars, R.string.we};

    //ids for images
    private int[] arrayImages= {R.drawable.mars, R.drawable.jupiter, R.drawable.saturn,
            R.drawable.bisystem, R.drawable.gas, R.drawable.milky, R.drawable.andromeda, R.drawable.quasars};

    private AnimationDrawable animationSlideShow; //for animation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        context = getBaseContext();

        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        infoBox = (ScrollView) findViewById(R.id.infoBox);
        btnInfo = (ImageButton) findViewById(R.id.btnInfo);
        btnMusicOnOff = (ImageButton) findViewById(R.id.btnMusic);

        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        //to play music after it's finished
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });

        imageViewSlideShow = (ImageView) findViewById(R.id.videoPlayer);

        setAnimDrawable();
        animationSlideShow.start();
        //as there's no onCompletionListener for animationDrawable I've used this solution
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnInfo.setVisibility(btnInfo.VISIBLE);
            }
        }, 100 * 31);
        mediaPlayer.start();
    }

    //stop music when app is stopped
    @Override
    public void onStop()
    {
        super.onStop();
        mediaPlayer.pause();
    }

    public void onClickBack(View view)
    {
        if(flagCurrPart < 9) //if it's not the last part than zoom out
        {
            btnInfo.setVisibility(btnInfo.GONE);
            flagCurrPart++;
            setAnimDrawable();
            animationSlideShow.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnInfo.setVisibility(btnInfo.VISIBLE);
                }
            }, 100 * 31);
        }

    }

    public void onClickFor(View view)
    {
        if(flagCurrPart > 0) //it's not the first part than zoom in
        {
            btnInfo.setVisibility(btnInfo.GONE);
            setReversedAnimDrawable();
            animationSlideShow.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (flagCurrPart != 0) {
                        btnInfo.setVisibility(btnInfo.VISIBLE);
                    }
                }
            }, 100 * 31);
            flagCurrPart--;
        }
    }

    public void onClickInfo(View view)
    {
        //show pictures in infoBox for all videos except the last one
        if(flagCurrPart != 9)
        {
            imageView.setImageResource(arrayImages[flagCurrPart - 1]);
        }
        else
        {
            imageView.setImageBitmap(null);
        }
        textView.setText(arrayTexts[flagCurrPart - 1]);
        //to set null image for the last video
        infoBox.setVisibility(infoBox.VISIBLE);//to make the layout with info visible
    }

    //to make layout with info gone
    public void onClickClose(View view)
    {
        infoBox.setVisibility(infoBox.GONE);
    }

    //to control music
    public void onClickMusic(View view)
    {

        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            btnMusicOnOff.setBackgroundResource(R.drawable.mute);
        }
        else
        {
            try
            {
                mediaPlayer.start();
                btnMusicOnOff.setBackgroundResource(R.drawable.sound);
            }
            catch (IllegalStateException e)
            {
                mediaPlayer.pause();
            }
        }
    }

    //for zoom out
    private void setAnimDrawable()
    {
        imageViewSlideShow.setImageDrawable(null);
        animationSlideShow = new AnimationDrawable();
        for(int i = 1; i <= 30; i++)
        {
            String path;
            if(i < 10)
            {
                path = context.getFilesDir().getAbsolutePath() + "/ZoomOut/Frames/p" + flagCurrPart  + "_0" + i + ".jpg";
            }
            else
            {
                path = context.getFilesDir().getAbsolutePath() + "/ZoomOut/Frames/p" + flagCurrPart + "_" + i + ".jpg";
            }
            animationSlideShow.addFrame(new BitmapDrawable(getResources(), path), 100);
        }
        animationSlideShow.setOneShot(true);
        imageViewSlideShow.setImageDrawable(animationSlideShow);
    }

    //for zoom in (pictures in reversed order)
    private void setReversedAnimDrawable()
    {
        imageViewSlideShow.setImageDrawable(null);
        animationSlideShow = new AnimationDrawable();
        for(int i = 30; i >= 1; i--)
        {
            String path;
            if(i < 10)
            {
                path = context.getFilesDir().getAbsolutePath() + "/ZoomOut/Frames/p" + flagCurrPart  + "_0" + i + ".jpg";
            }
            else
            {
                path = context.getFilesDir().getAbsolutePath() + "/ZoomOut/Frames/p" + flagCurrPart + "_" + i + ".jpg";
            }
            animationSlideShow.addFrame(new BitmapDrawable(getResources(), path), 100);
        }
        animationSlideShow.setOneShot(true);
        imageViewSlideShow.setImageDrawable(animationSlideShow);
    }
}
