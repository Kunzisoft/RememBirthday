package com.kunzisoft.remembirthday.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.kunzisoft.remembirthday.R;

import org.sufficientlysecure.htmltextview.HtmlTextView;

/**
 * Created by joker on 13/07/17.
 */

public class StartupDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_startup_title)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.dialog_startup_donate_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO Donation link
                    }
                });
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View viewRoot = inflater.inflate(R.layout.dialog_startup, null);
        HtmlTextView htmlTextView = (HtmlTextView) viewRoot.findViewById(R.id.html_text);

        String htmlContent =
                "<p>"+getString(R.string.html_text_purpose)+"</p>"+
                "<p>"+getString(R.string.html_text_free)+"</p>"+
                "<p>"+getString(R.string.html_text_donation)+"</p>";

        htmlTextView.setHtml(htmlContent);
        builder.setView(viewRoot);

        return builder.create();
    }
}
