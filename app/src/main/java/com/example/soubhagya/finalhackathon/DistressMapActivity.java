package com.example.soubhagya.finalhackathon;

import android.content.Intent;
import android.content.IntentSender;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.soubhagya.finalhackathon.utils.LocationUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;


/**
 * Handles Boiler Plate. Meant to be inherited by only MapsActivity.
 *
 * Creates the GoogleApiClient, Issues geoLocation request centered around mFireBasePlayer's current geoLocation.
 *
 */

public class DistressMapActivity extends AppCompatActivity {

    protected static final String TAG = "DistressMapActivity";

    protected static final int REQUEST_PERMISSIONS = 0;
    protected static final int REQUEST_CHECK_SETTINGS = 1;

    protected GoogleApiClient googleApiClient;
    protected LocationRequest locationRequest;
    protected DataStash dataStash = DataStash.getDataStash();
    protected GoogleMap map;

    private static final int RC_SIGNIN = 123;

    public void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
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
        } else
            googleApiClient = createGoogleApiClient();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
            }
        });
    }


    protected LocationRequest createLocationRequest() {
        return LocationRequest
                .create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1500);

    }

    protected void issueLocationRequest(LocationRequest locationRequest) {
        try {
            LocationServices
                    .FusedLocationApi
                    .requestLocationUpdates(
                            googleApiClient,
                            locationRequest,
                            new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    dataStash.fireBase
                                            .child("LOCATIONS")
                                            .child(auth.getCurrentUser().getUid())
                                            .setValue(LocationUtils.getGeoLocation(location));
                                }
                            });
        } catch (SecurityException se) {
            Log.d("SECURITY_API_CLIENT", se.toString());
        }
    }

    protected void checkedIssueRequest() {
        LocationUtils.requestSettings(locationRequest, googleApiClient)
                .setResultCallback(
                        new ResultCallback<LocationSettingsResult>() {
                            @Override
                            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                                final Status status = locationSettingsResult.getStatus();

                                switch (status.getStatusCode()) {
                                    case LocationSettingsStatusCodes.SUCCESS:
                                        //Actual Location Request Call
                                        issueLocationRequest(locationRequest);
                                        break;

                                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                        try {
                                            status.startResolutionForResult(
                                                    DistressMapActivity.this,
                                                    REQUEST_CHECK_SETTINGS
                                            );
                                        } catch (IntentSender.SendIntentException e) {
                                            Log.e(TAG, "SendIntentException " + e);
                                        }
                                        break;
                                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                        Toast.makeText(DistressMapActivity.this, "IRREVOCABLY FUCKED, RESTART", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                );
    }

    protected synchronized GoogleApiClient createGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .enableAutoManage(
                        this,
                        new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                Toast.makeText(DistressMapActivity.this, "ConnFail", Toast.LENGTH_SHORT).show();
                            }
                        })
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (LocationUtils.checkPermissions(DistressMapActivity.this)) {
                            locationRequest = createLocationRequest();
                            checkedIssueRequest();
                        } else {
                            LocationUtils.requestPermissions(DistressMapActivity.this,
                                    REQUEST_PERMISSIONS);
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Toast.makeText(DistressMapActivity.this, "SUSPENDED", Toast.LENGTH_SHORT).show();
                        googleApiClient.connect();
                    }
                })
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0) {
                    boolean permissionGranted = true;
                    for (int i = 0; i < 5; ++i) {
                        permissionGranted = permissionGranted &&
                                (grantResults[i] == PackageManager.PERMISSION_GRANTED);
                    }
                    if (permissionGranted) {
                        checkedIssueRequest();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please grant all permissions!",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Log.d("PERMISSIONS", "GrantResults length is zero!");
                }
            }



            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == RESULT_OK)
                    issueLocationRequest(locationRequest);
                break;
            case RC_SIGNIN:
                IdpResponse response = IdpResponse.fromResultIntent(data);
                // Successfully signed in
                if (resultCode == RESULT_OK) {
                    googleApiClient = createGoogleApiClient();
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
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}