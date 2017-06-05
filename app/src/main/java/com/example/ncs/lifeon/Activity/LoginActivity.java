package com.example.ncs.lifeon.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ncs.lifeon.R;

import static com.example.ncs.lifeon.Const.email;
import static com.example.ncs.lifeon.Const.height;
import static com.example.ncs.lifeon.Const.name;
import static com.example.ncs.lifeon.Const.weight;

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
                    name = editTextName.getText().toString();
                    email = editTextEmail.getText().toString();
                    height = Integer.parseInt(editTextHeight.getText().toString());
                    weight = Integer.parseInt(editTextWeight.getText().toString());
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
