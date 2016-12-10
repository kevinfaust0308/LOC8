package com.monsoonblessing.kevinfaust.loc8personal.Popups;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Kevin Faust on 12/10/2016.
 */

public class WarningPopup extends DialogFragment {

    public WarningPopup newInstance(String message) {
        Bundle b = new Bundle();
        b.putString("WarningMessage", message);
        WarningPopup wp = new WarningPopup();
        wp.setArguments(b);
        return wp;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /* if for some reason we didn't create popup using newInstance (with a message),
        set a default message as the warning message
        */
        String warningMessage;
        if (getArguments() != null) {
            warningMessage = getArguments().getString("WarningMessage");
        } else {
            warningMessage = "Warning!";
        }

        return new AlertDialog.Builder(getActivity())
                .setMessage(warningMessage)
                .setPositiveButton("OK", null)
                .create();
    }

}
