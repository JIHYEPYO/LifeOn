package com.example.ncs.lifeon.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ncs.lifeon.ECT.DatabaseExerciseController;
import com.example.ncs.lifeon.ECT.GifView;
import com.example.ncs.lifeon.R;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ncs.lifeon.ECT.Const.FIRSTRUN;
import static com.example.ncs.lifeon.ECT.Const.TABLE_NAME_EXERCISE;
import static com.example.ncs.lifeon.ECT.Const.curExerciseName;
import static com.example.ncs.lifeon.ECT.Const.curExerciseTime;
import static com.example.ncs.lifeon.ECT.Const.melody;
import static com.example.ncs.lifeon.ECT.Const.name;
import static com.example.ncs.lifeon.ECT.Const.r;

/**
 * Created by PYOJIHYE on 2017-06-07.
 */

public class ExerciseMelodyFragment extends Fragment {

    TextView textViewTimer;
    TextView textViewCurExerciseName;
    Button buttonPause;
    Button buttonCancel;

    DatabaseExerciseController dbController;
    SQLiteDatabase db;

    boolean flagPause;

    long millisTime;

    String timerText;
    Timer timer;

    ProgressDialog dialog;
    SharedPreferences settings;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_melody, container, false);

        textViewTimer = (TextView) view.findViewById(R.id.textViewTimer);
        textViewCurExerciseName = (TextView) view.findViewById(R.id.textViewCurExerciseName);
        buttonPause = (Button) view.findViewById(R.id.buttonExercisePause);
        buttonCancel = (Button) view.findViewById(R.id.buttonExerciseCancel);

        textViewCurExerciseName.setText(curExerciseName);

        dbController = new DatabaseExerciseController(view.getContext());
        db = dbController.getWritableDatabase();

        GifView gifView = (GifView) view.findViewById(R.id.gifView);
        gifView.setGifImageResource(R.drawable.running);

        flagPause = false;

        if (Integer.parseInt(curExerciseTime) < 10) {
            textViewTimer.setText("0" + curExerciseTime + ":00:00");
            millisTime = (Long.parseLong(curExerciseTime) * 60 * 100);
            startTimer();
        } else {
            textViewTimer.setText(curExerciseTime + ":00:00");
        }

        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagPause) {
                    flagPause = false;
                } else {
                    flagPause = true;
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    flagPause = true;
                    long realTime = (Long.parseLong(curExerciseTime) * 60 * 100) - millisTime;
                    int seconds = (int) (realTime / 100);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;
                    long millis = realTime % 100;

                    db.execSQL("INSERT INTO " + TABLE_NAME_EXERCISE + " VALUES (null, '" + name + "', '" + curExerciseName + "', '" + String.format("%02d : %02d : %02d", minutes, seconds, millis) + "');");

                    handler.sendEmptyMessage(0);
                } catch (SQLiteException e) {
                    db = dbController.getReadableDatabase();
                }
                curExerciseName = "";
                curExerciseTime = "";
            }
        });
        return view;
    }

    Runnable timerDisplay = new Runnable() {
        public void run() {
            if (!flagPause) {
                if (buttonPause.getText().toString().equals("Restart")) {
                    buttonPause.setText("Pause");
                } else if (millisTime == 60000) { //10분
                    handler.sendEmptyMessage(1);
                    Toast.makeText(getActivity(), "Ten minutes left.", Toast.LENGTH_SHORT).show();
                } else if (millisTime == 30000) { //5분
                    handler.sendEmptyMessage(1);
                    Toast.makeText(getActivity(), "Five minutes left.", Toast.LENGTH_SHORT).show();
                } else if (millisTime == 0) {     //타임아웃
                    handler.sendEmptyMessage(1);
                    Toast.makeText(getActivity(), "Time has run out.", Toast.LENGTH_SHORT).show();
                }
                textViewTimer.setText(timerText);
            } else {
                buttonPause.setText("Restart");
            }
        }
    };

    class UpdateTimeTask extends TimerTask {
        public void run() {
            if (!flagPause) {
                if (millisTime != 0) {
                    millisTime = millisTime - 1;
                    int seconds = (int) (millisTime / 100);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;
                    long millis = millisTime % 100;

                    timerText = String.format("%02d : %02d : %02d", minutes, seconds, millis);
                    textViewTimer.post(timerDisplay);
                } else {
                    try {
                        db.execSQL("INSERT INTO " + TABLE_NAME_EXERCISE + " VALUES (null, '" + name + "', '" + curExerciseName + "', '" + curExerciseTime + ":00:00');");
                    } catch (SQLiteException e) {
                        db = dbController.getReadableDatabase();
                    }
                    flagPause = true;
                    curExerciseName = "";
                    curExerciseTime = "";
                    handler.sendEmptyMessage(0);
                }
            }
        }
    }

    public void startTimer() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new UpdateTimeTask(), 1, 10);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Fragment fragment = new ExerciseFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content_main, fragment);
                    fragmentTransaction.commit();
                    break;
                case 1:
                    settings = getActivity().getSharedPreferences(FIRSTRUN, MODE_PRIVATE);

                    dialog = new ProgressDialog(getActivity());
                    if(settings.getBoolean("ExerciseSchedule", true)){
                        try {
                            melody = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                            r = RingtoneManager.getRingtone(getActivity(), melody);
                            r.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    dialog.setMessage("Do you want to close this window ?");
                    dialog.setCancelable(true);
                    dialog.setButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            r.stop();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    postDelayed(mRunnable, 60000);
            }
        }
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (dialog != null && dialog.isShowing()) {
                try {
                    r.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        }
    };
}