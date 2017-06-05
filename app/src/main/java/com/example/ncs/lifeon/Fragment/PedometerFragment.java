package com.example.ncs.lifeon.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ncs.lifeon.R;

import static com.example.ncs.lifeon.Const.cal;
import static com.example.ncs.lifeon.Const.height;
import static com.example.ncs.lifeon.Const.intentFilterStep;
import static com.example.ncs.lifeon.Const.km;
import static com.example.ncs.lifeon.Const.step;
import static com.example.ncs.lifeon.Const.weight;

/**
 * Created by PYOJIHYE on 2017-06-06.
 */

public class PedometerFragment extends Fragment {
    Button buttonPause;
    Button buttonReset;
    TextView textViewStepNum;
    TextView textViewKmNum;
    TextView textViewCalNum;

    Intent intentMyService;
    BroadcastReceiver receiver;
    boolean flag = true;
    String serviceData;
    private static PedometerFragment instance = new PedometerFragment();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedometer, container, false);

        textViewStepNum = (TextView) view.findViewById(R.id.textViewStepNum);
        textViewKmNum = (TextView) view.findViewById(R.id.textViewKmNum);
        textViewCalNum = (TextView) view.findViewById(R.id.textViewCalNum);
        buttonPause = (Button) view.findViewById(R.id.buttonPause);
        buttonReset = (Button) view.findViewById(R.id.buttonReset);

        intentMyService = new Intent(getActivity(), MyServiceIntent.class);
        receiver = new MyMainLocalReceiver();

        try {
            IntentFilter mainFilter = new IntentFilter(intentFilterStep);
            getActivity().registerReceiver(receiver, mainFilter);
            getActivity().startService(intentMyService);
            textViewStepNum.setText(step + "");
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
                step = 0;
                km = 0;
                cal = 0;

                textViewStepNum.setText(step + "");
                Toast.makeText(getActivity(), "Pedometer Reset!", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    class MyMainLocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            serviceData = intent.getStringExtra("serviceData");

            double km = ((height - 100) * Integer.parseInt(serviceData)) / 1000;
            double mailCal = 3.7103 + 0.2678 * weight + (0.0359 * (weight * 60 * 0.0006213) * 2) * weight;
            double cal = km * mailCal * 0.0006213;
            textViewStepNum.setText(serviceData);
            textViewKmNum.setText(Math.ceil(km) + "");
            textViewCalNum.setText(Math.ceil(cal)+"");
        }
    }
}