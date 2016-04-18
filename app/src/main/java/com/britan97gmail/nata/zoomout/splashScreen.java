package com.britan97gmail.nata.zoomout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SplashScreen extends AppCompatActivity {

    private SharedPreferences prefs = null;
    private ImageView imageViewPlanet;
    private AnimationDrawable animationPlanet;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setAnimDrawable();//set logo

        prefs = getSharedPreferences("PrefsFile", 0);//to check the first run
        context = getBaseContext();
        File root = context.getFilesDir();
        //create folder for videos if there's no one
        File dir = new File(root.getAbsolutePath() + "/ZoomOut");
        if (!dir.exists())
        {
            dir.mkdirs();
        }

        //to check Internet connection
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        //to load files if it's first app run, if not - just unzip
        boolean firstRun = prefs.getBoolean("firstRun", true);
        if (firstRun)
        {
            if(!isConnected)
            {
                Toast.makeText(this, "Check your Internet connection", Toast.LENGTH_SHORT).show();
                finish();
            }
            Load("https://archive.org/download/NataBritanframes1_201604/frames1.zip", "Frames.zip", dir);
        }
        else
        {
            unpackZip(dir, "Frames.zip");
        }
    }

    private void setAnimDrawable()
    {
        int[] logos = {R.drawable.logo1, R.drawable.logo2, R.drawable.logo3, R.drawable.logo4,R.drawable.logo5, R.drawable.logo6,
                R.drawable.logo7, R.drawable.logo8, R.drawable.logo9, R.drawable.logo10};
        imageViewPlanet = (ImageView)findViewById(R.id.videoView);
        animationPlanet = new AnimationDrawable();
        for(int i = 0; i < 10; i++)
        {
            animationPlanet.addFrame((BitmapDrawable) getResources().getDrawable(logos[i]), 100);
        }
        animationPlanet.setOneShot(false);
        imageViewPlanet.setImageDrawable(animationPlanet);
        animationPlanet.start();
    }

    public void Load(final String url, final String fileName, final File dir)
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);

        new AsyncTask<String, Integer, File>()
        {
            private Exception m_error = null;

            //set the profressBar
            @Override
            protected void onPreExecute()
            {
                progressDialog.setCancelable(false);
                progressDialog.setMax(100);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
            }

            @Override
            protected File doInBackground(String... params)
            {
                URL url;
                HttpURLConnection urlConnection;
                InputStream inputStream;
                int totalSize;
                int downloadedSize;
                byte[] buffer;
                int bufferLength;
                File file = null;
                FileOutputStream fos = null;
                try
                {
                    url = new URL(params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();

                    file = new File(dir, "/" + fileName);
                    fos = new FileOutputStream(file);
                    inputStream = urlConnection.getInputStream();
                    totalSize = urlConnection.getContentLength();
                    downloadedSize = 0;
                    buffer = new byte[1024];
                    bufferLength = 0;

                    // show progress after every iteration
                    while ((bufferLength = inputStream.read(buffer)) > 0)
                    {
                        fos.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                        publishProgress(downloadedSize, totalSize);
                    }
                    fos.close();
                    inputStream.close();
                    return file;

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    m_error = e;
                }
                return null;
            }

            // update progressDialog
            protected void onProgressUpdate(Integer... values)
            {
                progressDialog.setProgress((int) ((values[0] / (float) values[1]) * 100));
            }

            @Override
            protected void onPostExecute(File file) {
                // show message in case of error
                if (m_error != null)
                {
                    m_error.printStackTrace();
                    return;
                }
                // change sharedPreference, unpack zip and close progressBar
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstRun", false);
                editor.commit();
                progressDialog.hide();
                unpackZip(dir, "Frames.zip");
            }
        }.execute(url);
    }


    public void unpackZip(final File dir, final String zipname)
    {
        //create a folder for extracted files
        final File path = new File(dir.getAbsolutePath() + "/Frames");
        if (!path.exists())
        {
            path.mkdirs();
        }
        new AsyncTask<String, Integer, File>()
        {
            private Exception m_error = null;

            @Override
            protected File doInBackground(String... params)
            {
                try
                {
                    InputStream is;
                    ZipInputStream zis;
                    is = new FileInputStream(dir + "/" + zipname);
                    zis = new ZipInputStream(new BufferedInputStream(is));
                    ZipEntry ze;

                    while ((ze = zis.getNextEntry()) != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int count;

                        String filename = ze.getName();
                        FileOutputStream fout = new FileOutputStream(path + "/" + filename);

                        // reading and writing
                        while ((count = zis.read(buffer)) != -1) {
                            baos.write(buffer, 0, count);
                            byte[] bytes = baos.toByteArray();
                            fout.write(bytes);
                            baos.reset();
                        }

                        fout.close();
                        zis.closeEntry();
                    }
                    zis.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(File file)
            {
                // show message in case of error
                if (m_error != null) {
                    m_error.printStackTrace();
                    return;
                }
                //go to Main Menu
                Intent intent = new Intent(SplashScreen.this, MainMenu.class);
                startActivity(intent);
                finish();//to disable return to splash screen
            }
        }.execute(path.getAbsolutePath());
    }
}

