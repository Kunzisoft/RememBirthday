package com.kunzisoft.remembirthday.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kunzisoft.remembirthday.R;

/**
 * Activity for show list and details (depend of screen) of buddies
 */
public class BuddyActivity extends AppCompatActivity {

    private static final String TAG = "BuddyActivity";
    public final static String EXTRA_BUDDY = "EXTRA_BUDDY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy);

        // Toolbar generation
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        // Button add
        FloatingActionButton floatingActionButtonAddBuddy = (FloatingActionButton) findViewById(R.id.fab_add_buddy);
        floatingActionButtonAddBuddy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuddyActivity.this, ListContactsActivity.class);
                startActivity(intent);
            }
        });

        // Button modify
        /*
        //TODO Settings toolbar
        FloatingActionButton floatingActionButtonModifyDetailsBuddy = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButtonModifyDetailsBuddy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buddy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
