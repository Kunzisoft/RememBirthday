package com.kunzisoft.remembirthday;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kunzisoft.remembirthday.adapter.BuddyAdapter;
import com.kunzisoft.remembirthday.element.Buddy;

public class BuddyActivity extends AppCompatActivity {

    private static final String TAG = "BuddyActivity";
    public final static String EXTRA_BUDDY = "EXTRA_BUDDY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy);

        // Toolbar generation
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        // Button add
        FloatingActionButton floatingActionButtonAddBuddy = (FloatingActionButton) findViewById(R.id.fab_add_buddy);
        floatingActionButtonAddBuddy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Event add
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

        try {
            // Assign event
            ListBuddyFragment buddyListFragment = (ListBuddyFragment) getSupportFragmentManager().findFragmentById(R.id.activity_buddy_list_fragment);
            BuddyAdapter.OnClickItemBuddyListener onClickItemBuddyListener = new BuddyListener(this);
            buddyListFragment.setOnClickItemBuddyListener(onClickItemBuddyListener);
        } catch(ClassCastException e) {
            Log.e(TAG, e.getMessage());
        }
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


    // TODO Doc
    class BuddyListener implements BuddyAdapter.OnClickItemBuddyListener {
        private Context context;

        public BuddyListener(Context context) {
            this.context = context;
        }

        @Override
        public void onItemBuddyClick(View view, Buddy buddy) {
            // During initial setup, plug in the details fragment.
            DetailsBuddyFragment detailsBuddyFragment =  DetailsBuddyFragment.newInstance(buddy);
            detailsBuddyFragment.setBuddy(buddy);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.activity_buddy_details_fragment, detailsBuddyFragment);
            ft.commit();
        }
    }
}
