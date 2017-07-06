package com.kunzisoft.remembirthday.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joaquimley.faboptions.FabOptions;
import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.Utility;
import com.kunzisoft.remembirthday.adapter.AutoMessageAdapter;
import com.kunzisoft.remembirthday.adapter.ReminderNotificationsAdapter;
import com.kunzisoft.remembirthday.database.ContactBuild;
import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.preference.PreferencesManager;

/**
 * Activity who show the details of buddy selected
 */
public class DetailsBuddyFragment extends Fragment {

    private static final String TAG = "DETAILS_BUDDY_FRAGMENT";

    private Contact contact;

    protected RecyclerView autoMessagesListView;
    protected AutoMessageAdapter autoMessagesAdapter;

    protected RecyclerView remindersListView;
    protected ReminderNotificationsAdapter remindersAdapter;

    public void setBuddy(Contact currentContact) {
        Bundle args = new Bundle();
        args.putParcelable(BuddyActivity.EXTRA_BUDDY, currentContact);
        setArguments(args);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_details_buddy, container, false);

        TextView dayAndMonthTextView = (TextView) root.findViewById(R.id.fragment_details_buddy_dayAndMonth);
        TextView yearTextView = (TextView) root.findViewById(R.id.fragment_details_buddy_year);
        TextView daysLeftTextView = (TextView) root.findViewById(R.id.fragment_details_buddy_days_left);
        View selectBirthdayButton = root.findViewById(R.id.fragment_details_buddy_container_date);

        // List of reminders elements
        remindersListView = (RecyclerView) root.findViewById(R.id.fragment_details_buddy_list_reminders);
        LinearLayoutManager linearLayoutManagerReminder = new LinearLayoutManager(getContext());
        linearLayoutManagerReminder.setOrientation(LinearLayoutManager.VERTICAL);
        remindersListView.setLayoutManager(linearLayoutManagerReminder);

        // List of auto messages elements
        autoMessagesListView = (RecyclerView) root.findViewById(R.id.fragment_details_buddy_list_auto_messages);
        LinearLayoutManager linearLayoutManagerAutoMessage = new LinearLayoutManager(getContext());
        linearLayoutManagerAutoMessage.setOrientation(LinearLayoutManager.VERTICAL);
        autoMessagesListView.setLayoutManager(linearLayoutManagerAutoMessage);

        // Contact attributes
        contact = null;
        if(getArguments()!=null) {
            contact = getArguments().getParcelable(BuddyActivity.EXTRA_BUDDY);
        }
        if(contact != null) {
            // For save memory get RawId only when show details
            ContactBuild.assignRawContactIdToContact(getContext(), contact);

            selectBirthdayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO better way to call method of activity
                    ((DetailsBuddyActivity) getActivity()).openDialogSelection(contact.getRawId());
                }
            });

            if(contact.hasBirthday()) {
                // Display date
                DateUnknownYear currentBuddyBirthday = contact.getBirthday();

                // Assign text for day and month
                dayAndMonthTextView.setText(currentBuddyBirthday.toStringMonthAndDay(java.text.DateFormat.FULL));

                // Assign text for year
                if (contact.getBirthday().containsYear()) {
                    yearTextView.setVisibility(View.VISIBLE);
                    yearTextView.setText(currentBuddyBirthday.toStringYear());
                } else {
                    yearTextView.setVisibility(View.GONE);
                    yearTextView.setText("");
                }
                // Number days left before birthday
                Utility.assignDaysRemainingInTextView(daysLeftTextView, contact.getBirthdayDaysRemaining());

                FabOptions fabOptions = (FabOptions) root.findViewById(R.id.fragment_details_buddy_fab_options);
                fabOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.faboptions_reminder:
                                remindersAdapter.addDefaultItem();
                                break;

                            case R.id.faboptions_auto_message:
                                autoMessagesAdapter.addDefaultItem();
                                break;

                            default:
                        }
                    }
                });
            } else {
                //TODO Error
            }
        }
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(contact != null && contact.hasBirthday()) {
            // Add default reminders and link view to adapter
            remindersAdapter = new ReminderNotificationsAdapter(getContext(), contact.getBirthday());
            int[] defaultDays = PreferencesManager.getDefaultDays(getContext());
            // TODO get items from saved element
            for(int day : defaultDays) {
                remindersAdapter.addDefaultItem(day);
            }
            remindersListView.setAdapter(remindersAdapter);

            // Link auto messages view to adapter
            autoMessagesAdapter = new AutoMessageAdapter(getContext(), contact.getBirthday());
            autoMessagesListView.setAdapter(autoMessagesAdapter);
        }
    }
}
