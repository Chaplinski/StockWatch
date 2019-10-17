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

public class InputDialog extends AppCompatDialogFragment {
    private EditText sInput;
    private InputDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.input_alert, null);
        sInput = view.findViewById(R.id.input_symbol);

        sInput.setInputType(InputType.TYPE_CLASS_TEXT);
        sInput.setGravity(Gravity.CENTER_HORIZONTAL);
        sInput.setTextSize(24);
        sInput.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        builder.setMessage("Please enter a Stock Symbol:");
        builder.setView(view)
                .setTitle("Stock Selection")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String thisInput = sInput.getText().toString();
                        listener.applyTexts(thisInput);

                    }
                });
        sInput = view.findViewById(R.id.input_symbol);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (InputDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement InputDialogListener");
        }
    }

    public interface InputDialogListener{
        void applyTexts(String input);
    }
}
