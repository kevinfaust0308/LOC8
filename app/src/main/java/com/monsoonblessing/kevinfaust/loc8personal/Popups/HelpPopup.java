package com.monsoonblessing.kevinfaust.loc8personal.Popups;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.monsoonblessing.kevinfaust.loc8personal.R;

/**
 * Created by Kevin on 2016-12-23.
 */

public class HelpPopup extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.help_popup, null);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton("Ok", null)
                .create();
    }
}
