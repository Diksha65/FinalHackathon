package com.example.soubhagya.finalhackathon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by soubhagya on 2/4/17.
 */

public class AddPinActivity extends AppCompatActivity {


    private static final String EXTRA_PHONE_NUM = "com.example.soubhagya.finalhackathon.phoneNum";

    public static Intent newIntent(Context context, String phoneNum){
        Intent i = new Intent(context, AddPinActivity.class);
        i.putExtra(EXTRA_PHONE_NUM, phoneNum);
        return i;
    }

    private DataStash dataStash = DataStash.getDataStash();

    private EditText pinValue;
    private Button submitButton;

    private String pin;
    private String userPhoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pin);

        userPhoneNumber = getIntent().getStringExtra(EXTRA_PHONE_NUM);

        pinValue = (EditText)findViewById(R.id.pinValue);
        pinValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pin = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submitButton = (Button)findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataStash.fireBase
                        .child("USERS")
                        .child(userPhoneNumber)
                        .child("pin-code")
                        .setValue(pin)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Intent i = HomeScreen.newIntent(AddPinActivity.this, userPhoneNumber);
                                startActivity(i);
                                finish();
                            }
                        });
            }
        });
    }





}
