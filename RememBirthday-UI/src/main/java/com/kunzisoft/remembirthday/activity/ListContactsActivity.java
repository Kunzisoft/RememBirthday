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
import android.view.MenuItem;
import android.view.View;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.task.ActionBirthdayInDatabaseTask;
import com.kunzisoft.remembirthday.task.AddBirthdayToContactTask;

/**
 * Created by joker on 19/01/17.
 */
public class ListContactsActivity extends AppCompatActivity
        implements ActionBirthdayInDatabaseTask.CallbackActionBirthday, BirthdayDialogOpen {

    private static final int INSERT_RESULT_CODE = 1567;

    // Dialog for birthday selection
    private static final String TAG_SELECT_DIALOG = "TAG_SELECT_DIALOG";
    private BirthdayDialogFragment dialogSelection;
    private long rawContactId;

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
        dialogSelection = (BirthdayDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_SELECT_DIALOG);
        if(dialogSelection == null)
            dialogSelection = new BirthdayDialogFragment();

        dialogSelection.setOnClickListener(new BirthdayDialogFragment.OnClickBirthdayListener() {
            @Override
            public void onClickPositiveButton(DateUnknownYear dateUnknownYear) {
                // Add new birthday in database
                AddBirthdayToContactTask addBirthdayToContactTask =
                        new AddBirthdayToContactTask(
                                ListContactsActivity.this,
                                rawContactId,
                                dateUnknownYear);
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
    public void openDialogSelection(long rawContactId) {
        this.rawContactId = rawContactId;
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
                        rawContactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
                        // TODO Change to Uri
                    }
                    cursor.close();
                }
                break;
        }
    }

    @Override
    public void afterActionBirthdayInDatabase(DateUnknownYear birthday, Action action, Exception exception) {
        CallbackAction.showMessage(this, action, exception);
    }
}
