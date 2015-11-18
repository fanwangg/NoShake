package com.example.noshake;

import android.app.Activity;
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
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private final int SENEOR_TYPE = Sensor.TYPE_LINEAR_ACCELERATION;
    private final int ACCELEROMOTER_FPS = SensorManager.SENSOR_DELAY_FASTEST;
    private final int BUFFER_SECOND = 4;
    private final int FPS = 60;
    private final int BUFFER_DATA_SIZE = BUFFER_SECOND * FPS;


    private int OFFSET_SCALE = 30;

    private Context mContext;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private CircularBuffer mBufferX;
    private CircularBuffer mBufferY;
    private int mScreenHeight, mScreenWidth;


    @Bind(R.id.seekBarK) SeekBar mSeekBarK;
    @Bind(R.id.seekBarAlpha) SeekBar mSeekBarAlpha;
    @Bind(R.id.textKVal) TextView mTextK;
    @Bind(R.id.textAVal) TextView mTextA;
    @Bind(R.id.accX) TextView mTextAccX;
    @Bind(R.id.accY) TextView mTextAccY;
    @Bind(R.id.accZ) TextView mTextAccZ;
    @Bind(R.id.content) TextView mTargetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mContext = this;
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

        mSeekBarK.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = 1 + progress;
                mBufferX.setK(val);
                mBufferY.setK(val);
                mTextK.setText(String.valueOf(val));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBarAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = 1 + progress;
                OFFSET_SCALE = val;
                mTextA.setText(String.valueOf(val));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
            final float x = sensorEvent.values[0];
            final float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            mTextAccX.setText("" + x);
            mTextAccY.setText("" + y);
            mTextAccZ.setText("" + z);


            new Thread(new Runnable() {
                public void run() {
                    mBufferX.insert(x);
                    mBufferY.insert(y);
                    final float dx = -mBufferX.convolveWithH() * OFFSET_SCALE;
                    final float dy = -mBufferY.convolveWithH() * OFFSET_SCALE;

                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        public void run() {
                            updateStablilizedResult(dx, dy);
                        }
                    });
                }
            }).start();

        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void updateStablilizedResult(float x, float y){
        mTargetView.setX(x+ mScreenWidth/2);
        mTargetView.setY(y + mScreenHeight / 2);
        Log.d("MainActivity", " " + x + " " + y);
    }
}
