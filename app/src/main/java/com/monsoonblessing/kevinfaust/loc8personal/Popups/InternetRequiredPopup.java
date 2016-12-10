package com.monsoonblessing.kevinfaust.loc8personal.Popups;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Kevin Faust on 11/20/2016.
 */

public class InternetRequiredPopup extends WarningPopup {

    public WarningPopup newInstance() {
        return super.newInstance("Please make sure you are connected to the internet");
    }
}


