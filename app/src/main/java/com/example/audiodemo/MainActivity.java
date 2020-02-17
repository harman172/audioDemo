package com.example.audiodemo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Main";

    ImageView btnStop, btnPlay, btnRecord, btnResume, btnPause, btnStopRecording;
    String path;
    MediaRecorder myRecorder;
    MediaPlayer mediaPlayer;
    ImageView ivAddImage;

    final int REQUEST_PERMISSION_CODE = 1000;
    int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = findViewById(R.id.btn_play);
        btnRecord = findViewById(R.id.btn_record);
        btnStop = findViewById(R.id.btn_stop);
        btnResume = findViewById(R.id.btn_resume);
        btnPause = findViewById(R.id.btn_pause);
        btnStopRecording = findViewById(R.id.btn_stop_recording);
        ivAddImage = findViewById(R.id.iv_add_image);

        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";

        if(!checkPermissionDevice())
            requestPermission();
        if(!checkCameraPermission())
            requestCameraPermission();


        ivAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                Uri.fromFile()
            }
        });


        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (checkPermissionDevice()) {
                    setMediaRecorder();
                    Log.i(TAG, "onCreate: " + path);
                    try {
                        myRecorder.prepare();
                        myRecorder.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    btnRecord.setVisibility(View.GONE);
                    btnStopRecording.setVisibility(View.VISIBLE);
                    btnPause.setVisibility(View.VISIBLE);
                    btnResume.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Recording started....", Toast.LENGTH_SHORT).show();
                }
                else{
                    requestPermission();
                }
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                myRecorder.pause();
                btnResume.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.GONE);
            }
        });

        btnResume.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                myRecorder.resume();
                btnResume.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
            }
        });

        btnStopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRecorder.stop();
                myRecorder.release();
                myRecorder = null;

                btnStopRecording.setVisibility(View.GONE);
                btnPause.setVisibility(View.GONE);
                btnResume.setVisibility(View.GONE);
                btnRecord.setVisibility(View.VISIBLE);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();

                } catch (Exception e) {
                    // make something
                }

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Toast.makeText(MainActivity.this, "audio complete", Toast.LENGTH_SHORT).show();
                        btnPlay.setVisibility(View.VISIBLE);
                        btnStop.setVisibility(View.GONE);
                    }
                });

                mediaPlayer.start();
                Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();

                 btnPlay.setVisibility(View.GONE);
                 btnStop.setVisibility(View.VISIBLE);

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null){
                    mediaPlayer.stop();
                     mediaPlayer.release();
                }

                btnPlay.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.GONE);
            }
        });


    }

    private void setMediaRecorder() {

        myRecorder = new MediaRecorder();
        myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myRecorder.setOutputFile(path);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermissionDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkCameraPermission(){
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return (permissionState == PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: " + grantResults.length);
        if (requestCode == REQUEST_PERMISSION_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
//        else if(requestCode == REQUEST_CODE){
//            if (grantResults.length > 0 && grantResults[3] == PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
//            }
//        }
    }
}
