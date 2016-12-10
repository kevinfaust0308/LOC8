package com.monsoonblessing.kevinfaust.loc8personal;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by Kevin Faust on 12/9/2016.
 */

public class PermissionManager {
    /*
    Utility class to help with any permissions
     */

    // permission codes
    public static final int PERMISSION_LOCATION_CODE = 1;

    // permission names
    public static final String LOCATION_PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION;


    public static boolean hasLocationPermission(Context context) {
        /*
        Returns true if we have the location permission
         */
        return hasPermission(context, LOCATION_PERMISSION);
    }

    private static boolean hasPermission(Context context, String permission) {
        /*
        Returns true if we have the specified permission
         */
        return (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
    }

    public static void requestPermission(final Activity activity, final String permission, final int permissionCode) {

        // if user denied permission, show them a popup with an explanation before re-prompting permission
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {

            String message = "";

            switch (permission) {
                case LOCATION_PERMISSION:
                    message = "Location permission required to show you on the map. Can turn off " +
                            "location or hide your visibility within app if you don't want to be seen";
                    break;
                // other permissions can have different messages
            }

            new AlertDialog.Builder(activity)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissionPopup(activity, permission, permissionCode);
                        }
                    })
                    .create()
                    .show();

        } else {
            // prompt user to enable permission
            requestPermissionPopup(activity, permission, permissionCode);
        }


    }

    private static void requestPermissionPopup(Activity activity, String permission, int permissionCode) {
        /*
        Prompts user to enable permission using android's built in permission popup
         */
        ActivityCompat.requestPermissions(activity,
                new String[]{permission},
                permissionCode);

    }


}
