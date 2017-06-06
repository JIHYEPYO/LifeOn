package com.example.ncs.lifeon.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ncs.lifeon.R;

import static com.example.ncs.lifeon.ECT.Const.FIRSTRUN;

/**
 * Created by NCS on 2017-06-05.
 */

public class LoginActivity extends AppCompatActivity {

    Button buttonSubmit;
    EditText editTextName;
    EditText editTextEmail;
    EditText editTextHeight;
    EditText editTextWeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextHeight = (EditText) findViewById(R.id.editTextHeight);
        editTextWeight = (EditText) findViewById(R.id.editTextWeight);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextName.getText().toString().equals("") && !editTextEmail.getText().toString().equals("") &&
                        !editTextHeight.getText().toString().equals("") && !editTextWeight.getText().toString().equals("")) {
                    final SharedPreferences settings = getSharedPreferences(FIRSTRUN, MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("name", editTextName.getText().toString());
                    editor.putString("email", editTextEmail.getText().toString());
                    editor.putInt("height", Integer.parseInt(editTextHeight.getText().toString()));
                    editor.putInt("weight", Integer.parseInt(editTextWeight.getText().toString()));
                    editor.commit();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplication(), "Please fill in all the fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
