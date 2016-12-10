package com.monsoonblessing.kevinfaust.loc8personal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Kevin Faust on 12/10/2016.
 */

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "ConnectivityChangeRec";

    @Override
    public void onReceive(Context context, Intent intent) {

        AppCompatActivity connectivityReceiverListeningActivity = ((AppCompatActivity) context);
        ImageView wifi = (ImageView) connectivityReceiverListeningActivity.findViewById(R.id.wifi_animation_logo);
        wifi.setBackgroundResource(R.drawable.wifi_animation);
        AnimationDrawable wifiAnimation = (AnimationDrawable) wifi.getBackground();

        boolean hasConnection = !(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false));

        Log.d(TAG, "Device has connection :" + hasConnection);

        if (!hasConnection) {
            // show attempting to connect to wifi animation
            wifi.setVisibility(View.VISIBLE);
            wifiAnimation.start();
        } else {
            wifi.setVisibility(View.GONE);
            wifiAnimation.stop();
        }

    }

}
