package com.example.ncs.lifeon.Activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ncs.lifeon.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by NCS on 2017-06-05.
 */

public class PedometerActivity extends AppCompatActivity implements SensorEventListener {

    private TextView textViewDate;
    private TextView textViewStepNum;
    private Button buttonPause;
    private Button buttonReset;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    float[] values;
    int value;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewStepNum = (TextView) findViewById(R.id.textViewStepNum);
        buttonPause = (Button) findViewById(R.id.buttonPause);
        buttonReset = (Button) findViewById(R.id.buttonReset);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        String getTime = simpleDateFormat.format(date);
        textViewDate.setText(getTime);

        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((Button) view).getText().toString().equals("PAUSE")) {
                    onStop();
                    Toast.makeText(getApplicationContext(), "Pedometer Stop!", Toast.LENGTH_SHORT).show();
                    buttonPause.setText("START");
                } else {
                    onResume();
                    Toast.makeText(getApplicationContext(), "Pedometer Start!", Toast.LENGTH_SHORT).show();
                    buttonPause.setText("PAUSE");
                }

            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "Pedometer Reset!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this, stepCounterSensor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        values = sensorEvent.values;
        value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            textViewStepNum.setText(value + "");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
