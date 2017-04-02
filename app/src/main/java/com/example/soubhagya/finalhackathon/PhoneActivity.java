package com.example.soubhagya.finalhackathon;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.soubhagya.finalhackathon.utils.LocationUtils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by diksha on 2/4/17.
 */

public class PhoneActivity extends AppCompatActivity {

    protected static final String TAG = "PhoneActivity";

    private EditText editText;
    private Button button;
    private DataStash dataStash = DataStash.getDataStash();

    String phoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        button = (Button) findViewById(R.id.button_submit_details);
        editText = (EditText) findViewById(R.id.editText_phone_number);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneNumber= s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences(dataStash.sharedPreferences, MODE_PRIVATE)
                        .edit()
                        .putString("PHONE_NUMBER", phoneNumber)
                        .apply();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                dataStash.fireBase.child("USERS")
                        .child(phoneNumber)
                        .child("USER_ID")
                        .setValue(auth.getCurrentUser().getUid());

            }
        });
    }


}
