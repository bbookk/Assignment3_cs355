package com.example.asus.assignment3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView textX, textY, textZ;
    private SensorManager sensorManager;
    private Sensor sensor;
    AlertDialog.Builder builder;
    Random rand;
    int n;
    Boolean isShow = false;
    Resources res;
    String[] array;
    float x, y, z;
    Handler hdr = new Handler();

    private float accel;
    private float accelNow;
    private float accelLatest;
    private Runnable pollTask = new Runnable() {
        public void run() {
            dialogs();
            hdr.postDelayed(pollTask, 10000);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isShow = false;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        res = getResources();
        array = res.getStringArray(R.array.result);
        builder = new AlertDialog.Builder(MainActivity.this);
        accel = 0.00f;
        accelNow = SensorManager.GRAVITY_EARTH;
        accelLatest = SensorManager.GRAVITY_EARTH;
        Button btn = (Button)findViewById(R.id.restart);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResume();
            }
        });
    }

    public void dialogs() {
        if(!isShow){
            isShow = true;
        accelLatest = accelNow;
        accelNow = (float) Math.sqrt((double) (x*x + y*y + z*z));
        float subtraction = accelNow - accelLatest;
        accel = accel * 0.9f + subtraction; // perform low-cut filter
        rand = new Random();
        n = rand.nextInt(10);
        if (accel > 6) {
            final AlertDialog.Builder viewDialog = new AlertDialog.Builder(MainActivity.this);
            viewDialog.setTitle("คำทำนาย");
            viewDialog.setMessage("คุณได้หมายเลข : " + (n + 1) + "\n" + array[n]);
            viewDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    isShow = false;
                }
            });
            viewDialog.show();
        }
        }//end if
    }//end method

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        hdr.postDelayed(pollTask, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        hdr.removeCallbacks(pollTask);
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // can be safely ignored for this demo
    }


    public void onSensorChanged(SensorEvent event) {

        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
        }
    }
}


