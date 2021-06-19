package com.example.quictest

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    lateinit var mr : MediaRecorder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonRec: Button = findViewById(R.id.buttonRec)
        val buttonStop: Button = findViewById(R.id.buttonStop)
        val buttonPlay: Button = findViewById(R.id.buttonPlay)

        var path = Environment.getExternalStorageDirectory().toString()+"/myrec.3gp"
        mr = MediaRecorder()

        buttonRec.isEnabled = false
        buttonStop.isEnabled = false

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO ) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE), 111)
            buttonRec.isEnabled = true

        //start recording
        buttonRec.setOnClickListener{
            mr.setAudioSource(MediaRecorder.AudioSource.MIC)
            mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mr.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
            mr.setOutputFile(path)
            mr.prepare()
            mr.start()
            buttonStop.isEnabled = true
            buttonRec.isEnabled = false
        }

        //stop recording
        buttonStop.setOnClickListener{
            mr.stop()
            buttonRec.isEnabled = true
            buttonStop.isEnabled = false
        }

        //play recording
        buttonPlay.setOnClickListener {
            var mp = MediaPlayer()
            mp.setDataSource(path)
            mp.prepare()
            mp.start()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val buttonRec: Button = findViewById(R.id.buttonRec)
        val buttonStop: Button = findViewById(R.id.buttonStop)

        if(requestCode==111 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            buttonRec.isEnabled = true
    }


}