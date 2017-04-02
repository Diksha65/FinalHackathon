package com.example.soubhagya.finalhackathon.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by soubhagya on 2/4/17.
 */

public class GeneralUtils {

    public static void notifyUser(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
