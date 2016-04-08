package com.britan97gmail.nata.zoomout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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


public class zoomActivity extends AppCompatActivity {

    private TextView textView; //to print the info
    private ImageView imageView; //to shoe pictures refered to info
    private ScrollView infoBox; // contains imageView and textView
    private ImageButton btnInfo; //to show the info about object
    private ImageButton btnMusic; //to control sound

    private int flagCurrPart = 1; //current part must be played

    private ImageView img;
    //AudioPlayer
    private MediaPlayer mediaPlayer;

    //ids for text (it'd better to create an arrays.xml, but I'm too lazy for this stuff=))
    private int[] texts = {R.raw.mars, R.raw.jupiter, R.raw.saturn,
            R.raw.bisystem, R.raw.gas, R.raw.milky, R.raw.andromeda, R.raw.quasars, R.raw.we};

    //ids for images
    private int[] images= {R.drawable.mars, R.drawable.jupiter, R.drawable.saturn,
            R.drawable.bisystem, R.drawable.gas, R.drawable.milky, R.drawable.andromeda, R.drawable.quasars};

    private AnimationDrawable mAnimation; //for animation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        infoBox = (ScrollView) findViewById(R.id.infoBox);
        btnInfo = (ImageButton) findViewById(R.id.btnInfo);
        btnMusic = (ImageButton) findViewById(R.id.btnMusic);

        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        //to play music after it's finished
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });

        img = (ImageView) findViewById(R.id.videoPlayer);

        setAnimDrawable();
        mAnimation.start();
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
            mAnimation.start();
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
            mAnimation.start();
            new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnInfo.setVisibility(btnInfo.VISIBLE);
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
            imageView.setImageResource(images[flagCurrPart - 1]);
        }
        //to set null image for the last video
        else
        {
            imageView.setImageBitmap(null);
        }
        textView.setText(readFile());
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
            btnMusic.setBackgroundResource(R.drawable.mute);
        }
        else
        {
            try
            {
                mediaPlayer.start();
                btnMusic.setBackgroundResource(R.drawable.sound);
            }
            catch (IllegalStateException e)
            {
                mediaPlayer.pause();
            }
        }
    }

    //to read the text info from file
    private String readFile()
    {
        InputStream inputStream = getResources().openRawResource(texts[flagCurrPart - 1]);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;//counter
        try
        {
            i = inputStream.read();
            while (i != -1)
            {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }

    //for zoom out
    private void setAnimDrawable()
    {
        img.setImageDrawable(null);
        mAnimation = new AnimationDrawable();
        for(int i = 1; i <= 30; i++)
        {
            String path;
            if(i < 10)
            {
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ZoomOut/Frames/p" + flagCurrPart  + "_0" + i + ".jpg";
            }
            else
            {
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ZoomOut/Frames/p" + flagCurrPart + "_" + i + ".jpg";
            }
            mAnimation.addFrame(new BitmapDrawable(getResources(), path),100);
        }
        mAnimation.setOneShot(true);
        img.setImageDrawable(mAnimation);
    }

    //for zoom in (pictures in reversed order)
    private void setReversedAnimDrawable()
    {
       img.setImageDrawable(null);
       mAnimation = new AnimationDrawable();
        for(int i = 30; i >= 1; i--)
        {
            String path;
            if(i < 10)
            {
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ZoomOut/Frames/p" + flagCurrPart  + "_0" + i + ".jpg";
            }
            else
            {
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ZoomOut/Frames/p" + flagCurrPart + "_" + i + ".jpg";
            }
            mAnimation.addFrame(new BitmapDrawable(getResources(), path),100);
        }
        mAnimation.setOneShot(true);
        img.setImageDrawable(mAnimation);
    }
}
