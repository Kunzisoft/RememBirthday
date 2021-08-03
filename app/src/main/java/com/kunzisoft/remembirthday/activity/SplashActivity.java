package com.kunzisoft.remembirthday.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.notifications.ContactsProviderIntentService;
import com.kunzisoft.remembirthday.utility.Utility;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * SplashScreen activity, only shows the icon of app and call the main activity
 */
@RuntimePermissions
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23 && Utility.isFirstTime(this)) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.permission_read_contacts_start)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SplashActivityPermissionsDispatcher.showRationalForContactsWithPermissionCheck(SplashActivity.this);
                        }
                    })
                    .show();
        } else
            SplashActivityPermissionsDispatcher.showRationalForContactsWithPermissionCheck(this);
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    public void showRationalForContacts() {
        Intent intentService = new Intent(this, ContactsProviderIntentService.class);
        startService(intentService);

        Intent intent = new Intent(this, BuddyActivity.class);
        startActivity(intent);
        finish();
    }

    @OnShowRationale(Manifest.permission.READ_CONTACTS)
    public void showRationaleForContacts(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_read_contacts_rationale)
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

    @OnPermissionDenied(Manifest.permission.READ_CONTACTS)
    void showDeniedForContacts() {
        Toast.makeText(this, R.string.permission_read_contacts_denied, Toast.LENGTH_LONG).show();
        finish();
    }

    @OnNeverAskAgain(Manifest.permission.READ_CONTACTS)
    void showNeverAskForContacts() {
        Toast.makeText(this, R.string.permission_contacts_never_ask, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}