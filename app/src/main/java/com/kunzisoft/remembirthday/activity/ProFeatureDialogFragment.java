package com.kunzisoft.remembirthday.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.utility.IntentCall;

/**
 * Created by joker on 13/07/17.
 */

public class ProFeatureDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_pro_feature_message)
                // TODO change when pro is available
                //.setPositiveButton(R.string.dialog_pro_feature_download, new DialogInterface.OnClickListener() {
                .setPositiveButton(R.string.dialog_startup_positive_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Download PRO
                        // IntentCall.openStoreProApplication(ProFeatureDialogFragment.this.getContext());
                        IntentCall.openContributionPage(ProFeatureDialogFragment.this.getContext());
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
