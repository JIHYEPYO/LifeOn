package com.example.ncs.lifeon.Fragment;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ncs.lifeon.ECT.DatabaseStepController;
import com.example.ncs.lifeon.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.ncs.lifeon.ECT.Const.TABLE_NAME_EXERCISE;
import static com.example.ncs.lifeon.ECT.Const.TABLE_NAME_STEP;
import static com.example.ncs.lifeon.ECT.Const.curExerciseName;
import static com.example.ncs.lifeon.ECT.Const.curExerciseTime;
import static com.example.ncs.lifeon.ECT.Const.name;

/**
 * Created by PYOJIHYE on 2017-06-06.
 */

public class ExerciseFragment extends Fragment {

    TextView textViewCurDate;
    EditText editViewCurExerciseName;
    EditText editViewCurExerciseTime;
    Button buttonStart;

    String curDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        textViewCurDate = (TextView) view.findViewById(R.id.textViewCurDate);
        editViewCurExerciseName = (EditText) view.findViewById(R.id.editViewCurExerciseName);
        editViewCurExerciseTime = (EditText) view.findViewById(R.id.editViewCurExerciseTime);
        buttonStart = (Button) view.findViewById(R.id.buttonStart);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        curDate = simpleDateFormat.format(date);
        textViewCurDate.setText(curDate);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editViewCurExerciseName.getText().toString();
                String time = editViewCurExerciseTime.getText().toString();
                if (!name.equals("") && !time.equals("")) {
                    curExerciseName = name;
                    curExerciseTime = time;
                    editViewCurExerciseName.setText("");
                    editViewCurExerciseTime.setText("");
                    Fragment fragment = new ExerciseMelodyFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content_main, fragment);
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(getActivity(), "Please enter all values.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}