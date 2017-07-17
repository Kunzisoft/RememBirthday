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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.utility.Utility;

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
    public static final String EXTRA_BUDDY = "EXTRA_BUDDY";
    public static final String TAG_STARTUP_FRAGMENT = "TAG_STARTUP_FRAGMENT";

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

        if (Utility.isFirstTime(this))
            new StartupDialogFragment().show(getSupportFragmentManager(), TAG_STARTUP_FRAGMENT);

        //TODO BUG
        initDialogSelection(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BuddyActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.WRITE_CONTACTS)
    public void showRationalForContacts() {
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                startActivityForResult(
                        new Intent(this, SettingsActivity.class),
                        SettingsFragment.SETTING_REQUEST_CODE);
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
            case (DetailsBuddyFragment.MODIFY_CONTACT_RESULT_CODE) :
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
            case (DetailsBuddyActivity.UPDATE_BIRTHDAY_RESULT_CODE) :
                if (resultCode == Activity.RESULT_OK) {
                    deselectContactInListForDualPanel();
                }
                break;
            case (SettingsFragment.SETTING_REQUEST_CODE) :
                if (resultCode == Activity.RESULT_OK) {
                    // Restart after change settings
                    finish();
                    Intent intent = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void afterActionBirthdayInDatabase(DateUnknownYear birthday, Action action, Exception exception) {
        super.afterActionBirthdayInDatabase(birthday, action, exception);
        // After remove and update deselectForDualPanel
        deselectContactInListForDualPanel();
    }

    /**
     * Deselect any contact in ListBuddiesFragment
     */
    private void deselectContactInListForDualPanel() {
        ListBuddiesFragment listBuddiesFragment =
                (ListBuddiesFragment) getSupportFragmentManager()
                        .findFragmentByTag(getString(R.string.tag_list_birthdays));
        if(listBuddiesFragment!=null) {
            listBuddiesFragment.deselectForDualPanel();
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
