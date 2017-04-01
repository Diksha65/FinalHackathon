package com.example.soubhagya.finalhackathon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGNIN = 123;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                notifyUser("Already signed in!");
                //startActivity(new Intent(MainActivity.this, DetailsEntryActivity.class));
                finish();
            }
            else {
                notifyUser("Please sign in!");

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
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == RC_SIGNIN) {
                IdpResponse response = IdpResponse.fromResultIntent(data);

                // Successfully signed in
                if (resultCode == RESULT_OK) {
                    notifyUser("Next Activity");
                    //startActivity(new Intent(MainActivity.this, DetailsEntryActivity.class));
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
}
