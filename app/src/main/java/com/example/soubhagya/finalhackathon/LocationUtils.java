package com.example.soubhagya.finalhackathon;

import android.Manifest;
import android.app.Activity;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by diksha on 2/4/17.
 */

public class LocationUtils {

    private static DataStash dataStash = DataStash.getDataStash();

    private static final String[] mRequiredPermissions = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };


    public static LatLng getLatLng(GeoLocation location){
        return new LatLng(location.latitude, location.longitude);
    }

    public static LatLng getLatLng(Location location){
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static GeoLocation getGeoLocation(Location location){
        return new GeoLocation(location.getLatitude(), location.getLongitude());
    }


    public static boolean checkPermissions(Activity activity){
        boolean permissionsGranted = true;

        for(String permission : mRequiredPermissions)
            permissionsGranted = permissionsGranted &&
                    ContextCompat
                            .checkSelfPermission(
                                    activity,
                                    permission)
                            !=
                            PackageManager.PERMISSION_DENIED;

        return permissionsGranted;
    }

    public static void requestPermissions(Activity activity, int code){
        ActivityCompat.requestPermissions(
                activity,
                mRequiredPermissions,
                code
        );
    }

    public static PendingResult<LocationSettingsResult> requestSettings(LocationRequest locationRequest,
                                                                        GoogleApiClient googleApiClient){
        LocationSettingsRequest request = new LocationSettingsRequest
                .Builder()
                .addLocationRequest(locationRequest)
                .build();

        return LocationServices
                .SettingsApi
                .checkLocationSettings(
                        googleApiClient,
                        request
                );
    }

}
