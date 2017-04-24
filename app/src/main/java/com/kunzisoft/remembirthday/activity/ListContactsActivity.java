package com.kunzisoft.remembirthday.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.task.ActionBirthdayInDatabaseTask;
import com.kunzisoft.remembirthday.task.AddBirthdayToContactTask;

/**
 * Created by joker on 19/01/17.
 */
public class ListContactsActivity extends AppCompatActivity
        implements ActionBirthdayInDatabaseTask.CallbackActionBirthday, SelectBirthdayDialogOpen {

    private static final String TAG = "ListContactsActivity";

    private static final int INSERT_RESULT_CODE = 1567;

    // Dialog for birthday selection
    private static final String TAG_SELECT_DIALOG = "TAG_SELECT_DIALOG";
    private SelectBirthdayDialogFragment dialogSelection;
    private long contactId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contacts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.select_contact_title));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton addContactButton = (FloatingActionButton) findViewById(R.id.fab_add_buddy);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, INSERT_RESULT_CODE);
            }
        });

        // Initialize dialog for birthday selection
        dialogSelection = (SelectBirthdayDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_SELECT_DIALOG);
        if(dialogSelection == null)
            dialogSelection = new SelectBirthdayDialogFragment();

        dialogSelection.setOnClickListener(new SelectBirthdayDialogFragment.OnClickBirthdayListener() {
            @Override
            public void onClickPositiveButton(DateUnknownYear dateUnknownYear) {
                AddBirthdayToContactTask addBirthdayToContactTask = new AddBirthdayToContactTask(contactId, dateUnknownYear, ListContactsActivity.this);
                addBirthdayToContactTask.setCallbackActionBirthday(ListContactsActivity.this);
                addBirthdayToContactTask.execute();
                finish();
            }

            @Override
            public void onClickNegativeButton(DateUnknownYear selectedDate) {
            }
        });
    }

    @Override
    public void openDialogSelection(long contactId) {
        this.contactId = contactId;
        dialogSelection.show(getSupportFragmentManager(), TAG_SELECT_DIALOG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (INSERT_RESULT_CODE) :
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor =  getContentResolver().query(contactData, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
                        // TODO Change to Uri
                    }
                    cursor.close();
                }
                break;
        }
    }

    @Override
    public void afterActionBirthdayInDatabase(Exception exception) {
        String message;
        if(exception == null)
            message = getString(R.string.activity_list_contacts_success_add_birthday);
        else {
            Log.e(TAG, exception.getMessage());
            message = getString(R.string.activity_list_contacts_error_add_birthday);
        }

        Toast infoToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        infoToast.show();
    }
}
