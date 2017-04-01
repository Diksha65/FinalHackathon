package com.example.soubhagya.finalhackathon;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by diksha on 2/4/17.
 */

public class PhoneActivity extends AppCompatActivity {

    private Button submitButton;
    private EditText phoneNumberEditText;
    private String phoneNumber;

    private FirebaseUser user;
    private String userId;

    private DataStash dataStash = DataStash.getDataStash();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        phoneNumberEditText = (EditText) findViewById(R.id.editText_phone_number);
        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneNumber = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submitButton = (Button) findViewById(R.id.button_submit_details);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataStash.fireBase
                        .child("USERS")
                        .child(phoneNumber)
                        .child("ID")
                        .setValue(userId)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent i = AddContactsActivity.newIntent(PhoneActivity.this, phoneNumber);
                                startActivity(i);
                                finish();
                            }
                        });
            }
        });
    }
}
