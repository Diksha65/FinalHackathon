package com.example.soubhagya.finalhackathon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;


public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGNIN = 123;

    private FirebaseAuth auth;
    
    private Button getStartedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getStartedButton = (Button) findViewById(R.id.button_get_started);

            auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                notifyUser("Already signed in!");
                startActivity(new Intent(SignInActivity.this, PhoneActivity.class));
                finish();
            }
            else {
                notifyUser("Please sign in!");

                getStartedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setProviders(
                                                Arrays.asList(
                                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                        .setIsSmartLockEnabled(false)
                                        .build(),
                                RC_SIGNIN
                        );

                    }
                });
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == RC_SIGNIN) {
                IdpResponse response = IdpResponse.fromResultIntent(data);

                // Successfully signed in
                if (resultCode == RESULT_OK) {
                    notifyUser("Next Activity");
                    //Starts Slider
                    startIntroSLider();
                    finish();
                    return;
                } else {
                    // Sign in failed
                    if (response == null) {
                        // User pressed back button
                        notifyUser("Sign in cancelled!");
                        return;
                    }

                    if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                        notifyUser("Network error!");
                        return;
                    }

                    if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        notifyUser("Unknown error!");
                        return;
                    }
                }
            }
        }

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void startIntroSLider(){
        Intent intent = new Intent(SignInActivity.this, IntroSliderActivity.class);
        startActivity(intent);
    }
}
