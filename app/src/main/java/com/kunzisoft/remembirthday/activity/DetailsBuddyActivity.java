package com.kunzisoft.remembirthday.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.utility.IntentCall;
import com.squareup.picasso.Picasso;

/**
 * Activity that displays the details of a buddy
 */
public class DetailsBuddyActivity extends AbstractBuddyActivity implements IntentCall.OnContactModify {

    private static final String TAG_DETAILS_FRAGMENT = "TAG_DETAILS_FRAGMENT";

    public static final int UPDATE_BIRTHDAY_RESULT_CODE = 1786;

    private ImageView avatarImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_buddy);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can showMessage the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        // Retrieve contact
        if (getIntent() != null
                && getIntent().getExtras() != null)
            contactSelected = getIntent().getExtras().getParcelable(BuddyActivity.EXTRA_BUDDY);

        // Add name in title
        Toolbar toolbar = findViewById(R.id.toolbar);
        if(contactSelected != null)
        toolbar.setTitle(contactSelected.getName());
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Display avatar
        avatarImageView = findViewById(R.id.fragment_details_buddy_avatar);

        // Build dialog
        initDialogSelection(savedInstanceState);

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            DetailsBuddyFragment details = new DetailsBuddyFragment();
            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_details_buddy_fragment_details_buddy, details, TAG_DETAILS_FRAGMENT)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(avatarImageView != null) {
            if (contactSelected != null && contactSelected.containsImage()) {
                Picasso.with(this).load(contactSelected.getImageUri()).into(avatarImageView);
            } else {
                // Get the sec text color of the theme
                //TODO color secondary
                TypedValue typedValueSecondary = new TypedValue();
                getTheme().resolveAttribute(android.R.attr.textColorPrimaryInverse, typedValueSecondary, true);
                TypedArray arr = obtainStyledAttributes(
                        typedValueSecondary.data, new int[]{android.R.attr.textColorPrimaryInverse});
                int colorSecondary = arr.getColor(0, -1);
                arr.recycle();
                avatarImageView.setColorFilter(colorSecondary);
            }
            avatarImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    IntentCall.openAppForContactModifications(DetailsBuddyActivity.this, contactSelected);
                }
            });
        }
    }

    @Override
    public void onEventContactModify() {
        IntentCall.openAppForContactModifications(DetailsBuddyActivity.this, contactSelected);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (IntentCall.MODIFY_CONTACT_RESULT_CODE) :
                //if (resultCode == Activity.RESULT_OK) {
                    finish();
                /*
                    Uri contactData = data.getData();
                    Cursor cursor =  getContentResolver().query(contactData, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
                        // TODO Update View
                    }
                    cursor.close();
                    */
                //}
                break;
        }
    }

    @Override
    public void afterActionBirthdayInDatabase(DateUnknownYear birthday, Action action, Exception exception) {
        super.afterActionBirthdayInDatabase(birthday, action, exception);

        switch (action) {
            case UPDATE:
                /*
                contactSelected.setBirthday(birthday);
                DetailsBuddyFragment details = new DetailsBuddyFragment();
                details.setBuddy(contactSelected);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_details_buddy_fragment_details_buddy, details, TAG_DETAILS_FRAGMENT)
                        .commit();
                        */
                setResult(Activity.RESULT_OK);
                finish();
                break;
            case REMOVE:
                finish();
                break;
        }
    }
}
