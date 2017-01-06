package com.kunzisoft.remembirthday;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kunzisoft.remembirthday.element.Buddy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BuddyActivity extends AppCompatActivity {

    private static final String TAG = "BuddyActivity";
    public final static String EXTRA_BUDDY = "EXTRA_BUDDY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy);

        Buddy currentBuddy = getIntent().getParcelableExtra(EXTRA_BUDDY);
        Log.d(TAG, "CurrentBuddy : " + currentBuddy);

        // Toolbar generation
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(currentBuddy.getName());
        setSupportActionBar(toolbar);

        // Button event
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ImageView avatarImageView = (ImageView) findViewById(R.id.content_buddy_avatar);
        TextView dayAndMonthTextView = (TextView) findViewById(R.id.content_buddy_dayAndMonth);
        TextView yearTextView = (TextView) findViewById(R.id.content_buddy_year);

        // TODO Titre
        currentBuddy.getName();
        Date currentBuddyBirthday = currentBuddy.getBirthday();
        SimpleDateFormat dayAndMonthSimpleDateFormat = new SimpleDateFormat("dd MMMM", Locale.FRENCH);
        SimpleDateFormat yearSimpleDateFormat = new SimpleDateFormat("yyyy", Locale.FRENCH);
        dayAndMonthTextView.setText(dayAndMonthSimpleDateFormat.format(currentBuddyBirthday));
        yearTextView.setText(yearSimpleDateFormat.format(currentBuddyBirthday));


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
}
