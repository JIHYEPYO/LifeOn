package com.example.ncs.lifeon.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.ncs.lifeon.R;

import static com.example.ncs.lifeon.ECT.Const.FIRSTRUN;

/**
 * Created by NCS on 2017-06-05.
 */

public class IntroActivity extends AppCompatActivity {

    Handler h;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        final SharedPreferences settings = getSharedPreferences(FIRSTRUN, MODE_PRIVATE);

        if (settings.getBoolean("isFirstRun", true)) {
            Runnable irun = new Runnable() {
                public void run() {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("isFirstRun", false);
                    editor.commit();

                    Intent i = new Intent(IntroActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            };
            h = new Handler();
            h.postDelayed(irun, 2000);
        }else{
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
}