package com.example.noshake;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private final int SENEOR_TYPE = Sensor.TYPE_LINEAR_ACCELERATION;
    private final int ACCELEROMOTER_FPS = SensorManager.SENSOR_DELAY_FASTEST;
    private final int BUFFER_SECOND = 4;
    private final int BUFFER_DATA_SIZE = BUFFER_SECOND * 60;
    private final int OFFSET_SCALE = 10;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private CircularBuffer mBufferX;
    private CircularBuffer mBufferY;
    private int mScreenHeight, mScreenWidth;


    @Bind(R.id.accX) TextView mTextAccX;
    @Bind(R.id.accY) TextView mTextAccY;
    @Bind(R.id.accZ) TextView mTextAccZ;
    @Bind(R.id.content) View mTargetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth  = size.x;
        mScreenHeight = size.y;

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(SENEOR_TYPE);
        senSensorManager.registerListener(this, senAccelerometer, ACCELEROMOTER_FPS);

        mBufferX = new CircularBuffer(BUFFER_DATA_SIZE, BUFFER_SECOND);
        mBufferY = new CircularBuffer(BUFFER_DATA_SIZE, BUFFER_SECOND);
    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, ACCELEROMOTER_FPS);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == SENEOR_TYPE) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            mTextAccX.setText(""+x);
            mTextAccY.setText("" + y);
            mTextAccZ.setText("" + z);

            mBufferX.insert(x);
            mBufferY.insert(y);


            float dx = -mBufferX.convolveWithH() * OFFSET_SCALE;
            float dy = -mBufferY.convolveWithH() * OFFSET_SCALE;
            updateStablilizedResult(dx, dy);
            Log.d("MainActivity", " " + dx + " " + dy);
        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void updateStablilizedResult(float x, float y){
        mTargetView.setX(x+ mScreenWidth/2);
        mTargetView.setY(y+ mScreenHeight/2);

    }
}
