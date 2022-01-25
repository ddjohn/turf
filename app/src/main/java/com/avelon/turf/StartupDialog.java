package com.avelon.turf;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class StartupDialog extends DialogFragment {
    Logger logger = new Logger(StartupDialog.class);

    private String message = "Waiting for valid GPS...\r\n";
    private boolean enabled = true;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Turf!").setMessage(this.message);
        return builder.create();
    }

    public synchronized void addMessage(String message) {
        this.message += message + "\r\n";

        if(enabled)
            ((AlertDialog)this.getDialog()).setMessage(this.message);
    }

    public synchronized void cancel() {
        enabled = false;

        logger.error("t=" + this.getDialog());
        this.getDialog().cancel();
    }
}