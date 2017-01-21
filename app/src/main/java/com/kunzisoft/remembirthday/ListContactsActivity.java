package com.kunzisoft.remembirthday;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

/**
 * Created by joker on 19/01/17.
 */

public class ListContactsActivity extends AppCompatActivity{

    private static final String TAG = "ListContactsActivity";
    private static final String SimpleDateFormatSQLite = "yyyy-MM-dd";

    private CallbackContactsPermission callbackContactsPermission;

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contacts);

        // Toolbar generation
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
    }

    /**
     * Show the contacts in the ListView.
     */
    public void doActionGranted() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            callbackContactsPermission.doAfterGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doActionGranted();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setCallbackContactsPermission(CallbackContactsPermission callbackContactsPermission) {
        this.callbackContactsPermission = callbackContactsPermission;
    }

    public interface CallbackContactsPermission {
        void doAfterGranted();
    }


}
