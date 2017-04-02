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
import android.widget.Toast;

import com.example.soubhagya.finalhackathon.utils.LocationUtils;
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


/**
 * Handles Boiler Plate. Meant to be inherited by only MapsActivity.
 *
 * Creates the GoogleApiClient, Issues geoLocation request centered around mFireBasePlayer's current geoLocation.
 *
 */

public abstract class DistressMapActivity extends AppCompatActivity {

    protected static final String TAG = "DistressMapActivity";

    protected static final int REQUEST_PERMISSIONS    = 0;
    protected static final int REQUEST_CHECK_SETTINGS = 1;

    protected GoogleApiClient googleApiClient;
    protected LocationRequest locationRequest;

    protected GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleApiClient = createGoogleApiClient();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
            }
        });
    }


    protected LocationRequest createLocationRequest(){
        return LocationRequest
                .create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1500);//ms

    }

    protected void issueLocationRequest(LocationRequest locationRequest){
        try {
            LocationServices
                    .FusedLocationApi
                    .requestLocationUpdates(
                            googleApiClient,
                            locationRequest,
                            new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {

                                }
                            });
        } catch (SecurityException se) {
            Log.d("SECURITY_API_CLIENT", se.toString());
        }
    }

    protected void checkedIssueRequest(){
        LocationUtils.requestSettings(locationRequest, googleApiClient)
                .setResultCallback(
                        new ResultCallback<LocationSettingsResult>() {
                            @Override
                            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                                final Status status = locationSettingsResult.getStatus();

                                switch (status.getStatusCode()){
                                    case LocationSettingsStatusCodes.SUCCESS:
                                        //Actual Location Request Call
                                        issueLocationRequest(locationRequest);
                                        break;

                                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                        try{
                                            status.startResolutionForResult(
                                                    getPresentActivity(),
                                                    REQUEST_CHECK_SETTINGS
                                            );
                                        } catch (IntentSender.SendIntentException e) {
                                            Log.e(TAG, "SendIntentException " + e);
                                        }
                                        break;
                                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                        Toast.makeText(getPresentActivity(),
                                                "IRREVOCABLY FUCKED, RESTART", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(Ma)
                            }
                        })
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if(LocationUtils.checkPermissions(getPresentActivity())) {
                            locationRequest = createLocationRequest();
                            checkedIssueRequest();
                        }
                        else{
                            LocationUtils.requestPermissions(getPresentActivity(),
                                    REQUEST_PERMISSIONS);
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Toast.makeText(getPresentActivity(), "SUSPENDED", Toast.LENGTH_SHORT).show();
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
                    }
                    else {
                        Toast.makeText(getApplicationContext(),
                                "Please grant all permissions!",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                else {
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
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == RESULT_OK)
                    issueLocationRequest(locationRequest);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


}