package com.chaplinski.stockwatch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

public class GeneralDialog extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String getTitle = getArguments().getString("title");
        String getContent = getArguments().getString("content");
        int getIcon = getArguments().getInt("icon");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(getIcon == 1) {
            builder.setIcon(R.drawable.danger);
        }
        builder.setTitle(getTitle);
        builder.setMessage(getContent);

        return builder.create();
    }


}
