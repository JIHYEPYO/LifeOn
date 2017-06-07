package com.example.ncs.lifeon.Fragment;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.ncs.lifeon.ECT.DatabaseStepController;
import com.example.ncs.lifeon.R;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ncs.lifeon.ECT.Const.FIRSTRUN;

/**
 * Created by PYOJIHYE on 2017-06-06.
 */

public class SettingFragment extends Fragment {

    ToggleButton toggleButtonExerciseSchedule;
    ToggleButton toggleButtonSendMessage;
    ToggleButton toggleButtonIdentifyActivities;

    SharedPreferences settings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        toggleButtonExerciseSchedule = (ToggleButton) view.findViewById(R.id.toggleButtonExerciseSchedule);
        toggleButtonSendMessage = (ToggleButton) view.findViewById(R.id.toggleButtonSendMessage);
        toggleButtonIdentifyActivities = (ToggleButton) view.findViewById(R.id.toggleButtonIdentifyActivities);

        settings = this.getActivity().getSharedPreferences(FIRSTRUN, MODE_PRIVATE);

        toggleButtonExerciseSchedule.setChecked(settings.getBoolean("ExerciseSchedule",true));
        toggleButtonSendMessage.setChecked(settings.getBoolean("SendMessage",true));
        toggleButtonIdentifyActivities.setChecked(settings.getBoolean("IdentifyActivity",true));

        toggleButtonExerciseSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButtonExerciseSchedule.isChecked()) { //on
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("ExerciseSchedule", true);
                    editor.commit();
                    Toast.makeText(getActivity(), "Exercise schedule Melody registration is set.", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("ExerciseSchedule", false);
                    editor.commit();
                    Toast.makeText(getActivity(), "Exercise schedule Melody registration is cancel.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        toggleButtonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButtonSendMessage.isChecked()) { //on
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("SendMessage", true);
                    editor.commit();
                    Toast.makeText(getActivity(), "Send a Message is set.", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("SendMessage", false);
                    editor.commit();
                    Toast.makeText(getActivity(), "Send a Message is cancel.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        toggleButtonIdentifyActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButtonIdentifyActivities.isChecked()) { //on
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("IdentifyActivity", true);
                    editor.commit();
                    Toast.makeText(getActivity(), "Activity identification is set.", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("IdentifyActivity", false);
                    editor.commit();
                    Toast.makeText(getActivity(), "Activity identification is cancel.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}