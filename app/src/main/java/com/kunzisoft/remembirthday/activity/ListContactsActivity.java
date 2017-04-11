package com.kunzisoft.remembirthday.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kunzisoft.remembirthday.R;

/**
 * Created by joker on 19/01/17.
 */
public class ListContactsActivity extends AppCompatActivity{

    private static final String TAG = "ListContactsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contacts);

        // Toolbar generation
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        //TODO Add button contact
    }

}
