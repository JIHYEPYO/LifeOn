package com.example.ncs.lifeon.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ncs.lifeon.ECT.DatabaseStepController;
import com.example.ncs.lifeon.ECT.MyServiceIntent;
import com.example.ncs.lifeon.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ncs.lifeon.ECT.Const.FIRSTRUN;
import static com.example.ncs.lifeon.ECT.Const.TABLE_NAME_STEP;
import static com.example.ncs.lifeon.ECT.Const.intentFilterStep;
import static com.example.ncs.lifeon.ECT.Const.name;


/**
 * Created by PYOJIHYE on 2017-06-06.
 */

public class PedometerFragment extends Fragment {
    Button buttonPause;
    Button buttonReset;
    TextView textViewDate;
    TextView textViewStepNum;
    TextView textViewKmNum;
    TextView textViewCalNum;

    Intent intentMyService;
    BroadcastReceiver receiver;
    boolean flag = true;
    String serviceData;
    private static PedometerFragment instance = new PedometerFragment();
    SharedPreferences settings;

    DatabaseStepController dbController;
    SQLiteDatabase db;

    String curDate;
    String curStep;
    String curM;
    String curCal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedometer, container, false);

        textViewDate = (TextView) view.findViewById(R.id.textViewDate);
        textViewStepNum = (TextView) view.findViewById(R.id.textViewStepNum);
        textViewKmNum = (TextView) view.findViewById(R.id.textViewKmNum);
        textViewCalNum = (TextView) view.findViewById(R.id.textViewCalNum);
        buttonPause = (Button) view.findViewById(R.id.buttonPause);
        buttonReset = (Button) view.findViewById(R.id.buttonReset);

        dbController = new DatabaseStepController(view.getContext());
        db = dbController.getWritableDatabase();
        settings = this.getActivity().getSharedPreferences(FIRSTRUN, MODE_PRIVATE);

        intentMyService = new Intent(getActivity(), MyServiceIntent.class);
        receiver = new MyMainLocalReceiver();

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        curDate = simpleDateFormat.format(date);
        textViewDate.setText(curDate);

        if (!settings.getString("today", "0000-00-00").equals(curDate)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("today", curDate);
            editor.commit();

            curStep = settings.getString("step", "0");
            curM = settings.getString("m", "0");
            curCal = settings.getString("cal", "0");
            try {
                db.execSQL("INSERT INTO " + TABLE_NAME_STEP + " VALUES (null, '" + name + "', '" +
                        curDate + "', '" + curStep + "', '" + curM + "', '" + curCal + "');");

                editor.putString("step", "0");
                editor.putString("m", "0");
                editor.putString("cal", "0");

                editor.commit();
            } catch (SQLiteException e) {
                db = dbController.getReadableDatabase();
            }

            try {
                IntentFilter mainFilter = new IntentFilter(intentFilterStep);
                getActivity().registerReceiver(receiver, mainFilter);
                getActivity().startService(intentMyService);

                textViewStepNum.setText(settings.getString("step", "0") + "");
                textViewKmNum.setText(settings.getString("m", "0") + "");
                textViewCalNum.setText(settings.getString("km", "0") + "");
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            IntentFilter mainFilter = new IntentFilter(intentFilterStep);
            getActivity().registerReceiver(receiver, mainFilter);
            getActivity().startService(intentMyService);

            String curStep = settings.getString("step", "0");
            String curM = settings.getString("m", "0");
            String curCal = settings.getString("cal", "0");

            textViewStepNum.setText(curStep);
            textViewKmNum.setText(curM);
            textViewCalNum.setText(curCal);
        }

        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    buttonPause.setText("START");
                    try {
                        getActivity().unregisterReceiver(receiver);
                        getActivity().stopService(intentMyService);
                        Toast.makeText(getActivity(), "Pedometer Stop!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    buttonPause.setText("PAUSE");
                    try {
                        IntentFilter mainFilter = new IntentFilter(intentFilterStep);
                        getActivity().registerReceiver(receiver, mainFilter);
                        getActivity().startService(intentMyService);
                        Toast.makeText(getActivity(), "Pedometer Start!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                flag = !flag;
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("step", "0");
                editor.putString("m", "0");
                editor.putString("cal", "0");

                editor.commit();

                try {
                    db.execSQL("UPDATE " + TABLE_NAME_STEP + " SET step='0', m='0', cal='0' WHERE curDate='" + curDate +
                            "'AND name='" + name + "';");
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                textViewStepNum.setText(0 + "");
                textViewKmNum.setText(0 + "");
                textViewCalNum.setText(0 + "");
                Toast.makeText(getActivity(), "Pedometer Reset!", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    class MyMainLocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            serviceData = Integer.parseInt(settings.getString("step", "0")) + 1 + "";

            int height = settings.getInt("height", 0);
            int weight = settings.getInt("weight", 0);
            double m = ((height - 100) * Integer.parseInt(serviceData)) / 1000;
            double mailCal = 3.7103 + 0.2678 * weight + (0.0359 * (weight * 60 * 0.0006213) * 2) * weight;
            double cal = m * mailCal * 0.0006213;

            SharedPreferences.Editor editor = settings.edit();
            editor.putString("step", serviceData);
            editor.putString("m", Math.ceil(m) + "");
            editor.putString("cal", Math.ceil(cal) + "");

            editor.commit();

            try {
                db.execSQL("UPDATE " + TABLE_NAME_STEP + " SET step='" + curStep + "', m='" + curM +
                        "', cal='" + curCal + "' WHERE curDate='" + curDate + "'AND name='" + name + "';");
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            textViewStepNum.setText(serviceData);
            textViewKmNum.setText(Math.ceil(m) + "");
            textViewCalNum.setText(Math.ceil(cal) + "");
        }
    }
}