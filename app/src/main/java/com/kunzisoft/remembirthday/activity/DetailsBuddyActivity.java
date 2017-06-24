package com.kunzisoft.remembirthday.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.task.ActionBirthdayInDatabaseTask;
import com.kunzisoft.remembirthday.task.RemoveBirthdayFromContactTask;
import com.kunzisoft.remembirthday.task.UpdateBirthdayToContactTask;
import com.squareup.picasso.Picasso;

/**
 * Activity that displays the details of a buddy
 */
public class DetailsBuddyActivity extends AppCompatActivity
        implements ActionBirthdayInDatabaseTask.CallbackActionBirthday, SelectBirthdayDialogOpen {

    private Contact contact;

    private SelectBirthdayDialogFragment dialogSelection;
    private static final String TAG_SELECT_DIALOG = "TAG_SELECT_DIALOG";

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
        contact = getIntent().getExtras().getParcelable(BuddyActivity.EXTRA_BUDDY);

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
        if(avatarImageView != null) {
            if (contact != null && contact.containsImage()) {
                Picasso.with(this).load(contact.getImageUri()).into(avatarImageView);
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
        }

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            DetailsBuddyFragment details = new DetailsBuddyFragment();
            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.activity_details_buddy_fragment_details_buddy, details).commit();
        }

        // Initialize dialog for birthday selection
        dialogSelection = (SelectBirthdayDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_SELECT_DIALOG);
        if(dialogSelection == null)
            dialogSelection = new SelectBirthdayDialogFragment();

        dialogSelection.setDefaultBirthday(contact.getBirthday());
        dialogSelection.setOnClickListener(new SelectBirthdayDialogFragment.OnClickBirthdayListener() {
            @Override
            public void onClickPositiveButton(DateUnknownYear dateUnknownYear) {
                UpdateBirthdayToContactTask updateBirthdayToContactTask =
                        new UpdateBirthdayToContactTask(contact.getId(), contact.getBirthday(), dateUnknownYear, DetailsBuddyActivity.this);
                updateBirthdayToContactTask.setCallbackActionBirthday(DetailsBuddyActivity.this);
                updateBirthdayToContactTask.execute();
                //TODO remove notification after update

                finish();
            }

            @Override
            public void onClickNegativeButton(DateUnknownYear selectedDate) {
            }
        });
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
            case R.id.action_change_anniversary:
                openDialogSelection(contact.getId());
                break;
            case R.id.action_modify_contact:
                Intent editIntent = new Intent(Intent.ACTION_EDIT);
                Uri contactUri = ContactsContract.Contacts.getLookupUri(contact.getId(), contact.getLookUp());
                editIntent.setDataAndType(contactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                startActivity(editIntent);
                break;
            case R.id.action_delete:

                AlertDialog.Builder builderDialog = new AlertDialog.Builder(this);
                builderDialog.setTitle(R.string.dialog_select_birthday_title);
                builderDialog.setMessage(R.string.dialog_delete_birthday_message);
                builderDialog.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Delete anniversary in database
                        RemoveBirthdayFromContactTask removeBirthdayFromContactTask =
                                new RemoveBirthdayFromContactTask(
                                        contact.getId(),
                                        contact.getBirthday(),
                                        DetailsBuddyActivity.this);
                        removeBirthdayFromContactTask.setCallbackActionBirthday(DetailsBuddyActivity.this);
                        removeBirthdayFromContactTask.execute();
                        finish();
                    }
                });
                builderDialog.setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builderDialog.create().show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void afterActionBirthdayInDatabase(Exception exception) {}

    @Override
    public void openDialogSelection(long contactId) {
        dialogSelection.show(getSupportFragmentManager(), TAG_SELECT_DIALOG);
    }
}
