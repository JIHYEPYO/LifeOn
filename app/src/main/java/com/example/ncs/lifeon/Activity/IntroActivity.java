package com.example.ncs.lifeon.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.ncs.lifeon.R;

/**
 * Created by NCS on 2017-06-05.
 */

public class IntroActivity extends AppCompatActivity {

    Handler h;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Runnable irun = new Runnable() {
            public void run() {
                Intent i = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };

        h = new Handler();
        h.postDelayed(irun, 2000);
    }
}