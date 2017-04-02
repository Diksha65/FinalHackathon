package com.example.soubhagya.finalhackathon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by diksha on 2/4/17.
 */

public class HomeScreen extends AppCompatActivity {

    private static final String EXTRA_PHONE_NUM = "com.example.soubhagya.finalhackathon.phoneNum";

    public static Intent newIntent(Context context, String phoneNumber){
        Intent intent = new Intent(context, HomeScreen.class);
        intent.putExtra(EXTRA_PHONE_NUM, phoneNumber);
        return intent;
    }

    private String userPhoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userPhoneNumber = getIntent().getStringExtra(EXTRA_PHONE_NUM);
    }
}
