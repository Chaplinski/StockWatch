package com.example.stockwatch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

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
