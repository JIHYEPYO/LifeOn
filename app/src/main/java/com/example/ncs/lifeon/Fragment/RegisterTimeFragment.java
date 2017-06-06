package com.example.ncs.lifeon.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ncs.lifeon.R;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ncs.lifeon.ECT.Const.FIRSTRUN;

/**
 * Created by PYOJIHYE on 2017-06-06.
 */

public class RegisterTimeFragment extends Fragment {
    private static RegisterTimeFragment instance = new RegisterTimeFragment();

    EditText editTextTime;
    Button buttonTimeRegister;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_time, container, false);

        editTextTime = (EditText) view.findViewById(R.id.editTextTime);
        buttonTimeRegister = (Button) view.findViewById(R.id.buttonTimeRegister);

        final SharedPreferences settings = getActivity().getSharedPreferences(FIRSTRUN, MODE_PRIVATE);

        editTextTime.setText(settings.getString("timeRegister", "5000"));

        buttonTimeRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("timeRegister", editTextTime.getText().toString());
                editor.commit();

                Toast.makeText(getActivity(), "The time has been registered.", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public static synchronized RegisterTimeFragment getInstance() {
        return instance;
    }
}
