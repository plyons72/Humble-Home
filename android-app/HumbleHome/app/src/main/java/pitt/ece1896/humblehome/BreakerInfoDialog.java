package pitt.ece1896.humblehome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static pitt.ece1896.humblehome.ManualControl.updateBreaker;

public class BreakerInfoDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int breakerId = getArguments().getInt("breakerId");
        final String label = getArguments().getString("label");
        final String description = getArguments().getString("description");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_view, null);
        TextView breakerIdView = (TextView)view.findViewById(R.id.breakerId);
        EditText labelView = (EditText)view.findViewById(R.id.label);
        EditText descriptionView = (EditText)view.findViewById(R.id.description);
        //Button okBtn = (Button)view.findViewById(R.id.okBtn);
        //Button cancelBtn = (Button)view.findViewById(R.id.cancelBtn);

        breakerIdView.setText("Breaker ID: " + breakerId);
        labelView.setText(label);
        descriptionView.setText(description);

        builder.setView(view)
            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateBreaker(breakerId, label, description);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
            }
        });

        return dialog;
    }
}
