package com.kunzisoft.remembirthday.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.DateUnknownYear;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Activity for showMessage list and details (depend of screen) of buddies
 */
@RuntimePermissions
public class BuddyActivity extends AbstractBuddyActivity {

    private static final String TAG = "BuddyActivity";
    public final static String EXTRA_BUDDY = "EXTRA_BUDDY";

    private static final int ADD_BIRTHDAY_RESULT_CODE = 1946;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy);

        // Toolbar generation
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.anniversaries_title));
        setSupportActionBar(toolbar);

        // Button add
        FloatingActionButton addEventButton = (FloatingActionButton) findViewById(R.id.fab_add_event);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuddyActivityPermissionsDispatcher.showRationalForContactsWithCheck(BuddyActivity.this);
            }
        });

        //TODO BUG
        initDialogSelection();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BuddyActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.WRITE_CONTACTS)
    public void showRationalForContacts() {
        deselectContactInList();
        Intent intent = new Intent(BuddyActivity.this, ListContactsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buddy, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (DetailsBuddyFragment.MODIFY_RESULT_CODE) :
                if (resultCode == Activity.RESULT_OK) {
                /*
                    Uri contactData = data.getData();
                    Cursor cursor =  getContentResolver().query(contactData, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
                        // TODO Update View
                    }
                    cursor.close();
                    */
                }
                break;
        }
    }

    @Override
    public void afterActionBirthdayInDatabase(DateUnknownYear birthday, Action action, Exception exception) {
        super.afterActionBirthdayInDatabase(birthday, action, exception);
        // After remove and update deselect
        deselectContactInList();
    }

    /**
     * Deselect any contact in ListBuddiesFragment
     */
    private void deselectContactInList() {
        ListBuddiesFragment listBuddiesFragment =
                (ListBuddiesFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.activity_buddy_fragment_list_buddies);
        if(listBuddiesFragment!=null) {
            Log.e(TAG, listBuddiesFragment + "");
            listBuddiesFragment.deselect();
        }
    }

    @OnShowRationale(Manifest.permission.WRITE_CONTACTS)
    public void showRationaleForContacts(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_write_contacts_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_CONTACTS)
    void showDeniedForContacts() {
        Toast.makeText(this, R.string.permission_write_contacts_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_CONTACTS)
    void showNeverAskForContacts() {
        Toast.makeText(this, R.string.permission_contacts_neverask, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
