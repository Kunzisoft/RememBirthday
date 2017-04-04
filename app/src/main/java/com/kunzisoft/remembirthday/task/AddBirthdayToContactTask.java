package com.kunzisoft.remembirthday.task;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.ImageView;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.DateUnknownYear;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * AsyncTask who store the birthday in contact database for a specific contact
 */
public class AddBirthdayToContactTask extends AsyncTask<Void, Void, Exception> {

    private long contactId;
    private DateUnknownYear birthday;
    private Activity context;

    public AddBirthdayToContactTask(long contactId, DateUnknownYear birthday, Activity context) {
        this.contactId = contactId;
        this.birthday = birthday;
        this.context = context;
    }

    @Override
    protected Exception doInBackground(Void... params) {
        try {
            Log.d("TAG", contactId+"");
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, (int) contactId)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
                    .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, "26-05-2015")
                    .build());
            context.getContentResolver().
                    applyBatch(ContactsContract.AUTHORITY, ops);
        } catch(Exception e) {
            return e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Exception exception) {
        String message;
        if(exception == null)
            message = context.getString(R.string.activity_list_contacts_success_add_birthday);
        else {
            message = context.getString(R.string.activity_list_contacts_error_add_birthday);
        }

        Snackbar infoSnackbar = Snackbar.make(context.findViewById(R.id.activity_list_contacts_information),
                message, Snackbar.LENGTH_SHORT);
        infoSnackbar.show();
    }
}