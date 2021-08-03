package com.kunzisoft.remembirthday.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.kunzisoft.remembirthday.R;

import org.sufficientlysecure.htmltextview.HtmlTextView;

/**
 * Created by joker on 13/07/17.
 */

public class StartupDialogFragment extends DialogFragment {

    private DialogInterface.OnClickListener onPositiveButtonClickListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_startup_title)
                .setNegativeButton(R.string.dialog_startup_negative_button, null)
                .setPositiveButton(R.string.dialog_startup_positive_button, onPositiveButtonClickListener);
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

    public void setOnPositiveButtonClickListener(DialogInterface.OnClickListener onClickListener) {
        this.onPositiveButtonClickListener = onClickListener;
    }
}
