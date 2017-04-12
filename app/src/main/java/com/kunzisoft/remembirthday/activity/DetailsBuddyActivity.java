package com.kunzisoft.remembirthday.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.Contact;

/**
 * Activity that displays the details of a buddy
 */
public class DetailsBuddyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_buddy);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        // Retrieve contact
        Contact contact = getIntent().getExtras().getParcelable(BuddyActivity.EXTRA_BUDDY);

        // Add name in title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(contact != null)
        toolbar.setTitle(contact.getName());
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Display avatar
        ImageView avatarImageView = (ImageView) findViewById(R.id.fragment_details_buddy_avatar);
        if(contact != null && avatarImageView != null && contact.containsImage())
            avatarImageView.setImageURI(contact.getImageUri());

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            DetailsBuddyFragment details = new DetailsBuddyFragment();
            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.activity_details_buddy_fragment_details_buddy, details).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details_buddy, menu);
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
            case android.R.id.home:
                finish();
                break;
            case R.id.action_modify:

                break;
            case R.id.action_settings:

                break;
            case R.id.action_delete:

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
