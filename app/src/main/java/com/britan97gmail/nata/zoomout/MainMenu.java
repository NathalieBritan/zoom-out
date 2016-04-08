package com.britan97gmail.nata.zoomout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainMenu extends AppCompatActivity {

    private boolean exit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void whatchInfo(View view)
    {
        Intent intent = new Intent(this,zoomActivity.class);
        startActivity(intent);
    }

    public void whatchVideo(View view)
    {
        Intent intent = new Intent(this,fullVideo.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed()
    {
        if (!exit)
        {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable()
            {

                @Override
                public void run()
                {
                    exit = false;
                }
            }, 2000);
        }
        else
        {
            super.onBackPressed();
            File root = Environment.getExternalStorageDirectory();
            File path = new File(root.getAbsolutePath() + "/ZoomOut/Frames");
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
            path.delete();
            System.exit(0);
        }
    }
}
