package com.kunzisoft.remembirthday.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunzisoft.autosms.database.AutoSmsDbHelper;
import com.kunzisoft.autosms.model.AutoSms;
import com.kunzisoft.remembirthday.BuildConfig;
import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.adapter.AutoMessageAdapter;
import com.kunzisoft.remembirthday.adapter.MenuAdapter;
import com.kunzisoft.remembirthday.adapter.ReminderCalendarNotificationsAdapter;
import com.kunzisoft.remembirthday.adapter.observer.AutoSmsDatabaseObserver;
import com.kunzisoft.remembirthday.adapter.observer.AutoSmsToastObserver;
import com.kunzisoft.remembirthday.adapter.observer.ReminderCalendarProviderObserver;
import com.kunzisoft.remembirthday.adapter.observer.ReminderToastObserver;
import com.kunzisoft.remembirthday.animation.AnimationViewCircle;
import com.kunzisoft.remembirthday.element.CalendarEvent;
import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.element.PhoneNumber;
import com.kunzisoft.remembirthday.element.ProxyAutoMessage;
import com.kunzisoft.remembirthday.element.Reminder;
import com.kunzisoft.remembirthday.exception.NoPhoneNumberException;
import com.kunzisoft.remembirthday.exception.PhoneNumberNotInitializedException;
import com.kunzisoft.remembirthday.factory.ActionContactMenu;
import com.kunzisoft.remembirthday.factory.MenuAction;
import com.kunzisoft.remembirthday.factory.MenuActionAutoMessage;
import com.kunzisoft.remembirthday.factory.MenuActionCalendar;
import com.kunzisoft.remembirthday.factory.MenuActionCall;
import com.kunzisoft.remembirthday.factory.MenuActionGift;
import com.kunzisoft.remembirthday.factory.MenuActionMessage;
import com.kunzisoft.remembirthday.factory.MenuActionReminder;
import com.kunzisoft.remembirthday.factory.MenuContact;
import com.kunzisoft.remembirthday.factory.MenuContactCreator;
import com.kunzisoft.remembirthday.preference.PreferencesHelper;
import com.kunzisoft.remembirthday.provider.ContactLoader;
import com.kunzisoft.remembirthday.provider.ContactPhoneNumberLoader;
import com.kunzisoft.remembirthday.provider.ContactProvider;
import com.kunzisoft.remembirthday.provider.EventLoader;
import com.kunzisoft.remembirthday.provider.ReminderLoader;
import com.kunzisoft.remembirthday.utility.IntentCall;
import com.kunzisoft.remembirthday.utility.Utility;

import java.util.List;

/**
 * Activity who showMessage the details of buddy selected
 */
public class DetailsBuddyFragment extends Fragment implements ActionContactMenu{

    private static final String TAG = "DetailsBuddyFragment";

    private static final String CONTACT_KEY = "CONTACT_KEY";

    private static final String CALENDAR_DIALOG_TAG = "CALENDAR_DIALOG_TAG";
    private static final String SPECIAL_FEATURES_DIALOG_TAG = "SPECIAL_FEATURES_DIALOG_TAG";

    private Contact contact;

    private TextView dayAndMonthTextView;
    private TextView yearTextView;
    private TextView daysLeftTextView;
    private View selectBirthdayButton;

    protected RecyclerView autoMessagesListView;
    protected AutoMessageAdapter autoMessagesAdapter;

    protected RecyclerView remindersListView;
    protected ReminderCalendarNotificationsAdapter remindersAdapter;

    private RecyclerView menuListView;
    private GridLayoutManager gridLayoutManager;
    private MenuAdapter menuAdapter;
    private int spanCount = 3;
    private MenuContact menuContact;

    private View menuView;
    private View menuViewButton;
    private AnimationViewCircle menuAnimationViewCircle;

    private DialogFragment calendarDialog;
    private DialogFragment specialFeaturesDialog;

    public void setBuddy(Contact currentContact) {
        Bundle args = new Bundle();
        args.putParcelable(BuddyActivity.EXTRA_BUDDY, currentContact);
        setArguments(args);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_details_buddy, container, false);

        dayAndMonthTextView = root.findViewById(R.id.fragment_details_buddy_dayAndMonth);
        yearTextView = root.findViewById(R.id.fragment_details_buddy_year);
        daysLeftTextView = root.findViewById(R.id.fragment_details_buddy_days_left);
        selectBirthdayButton = root.findViewById(R.id.fragment_details_buddy_date);

        // Animation init
        menuView = root.findViewById(R.id.fragment_details_buddy_add_menu);
        menuViewButton = root.findViewById(R.id.fragment_details_buddy_add_button);
        menuAnimationViewCircle = AnimationViewCircle.build(menuView);

        menuListView = root.findViewById(R.id.fragment_details_buddy_menu_list);
        gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        menuListView.setLayoutManager(gridLayoutManager);

        // List of reminders elements
        remindersListView = root.findViewById(R.id.fragment_details_buddy_list_reminders);
        LinearLayoutManager linearLayoutManagerReminder = new LinearLayoutManager(getContext());
        linearLayoutManagerReminder.setOrientation(LinearLayoutManager.VERTICAL);
        remindersListView.setLayoutManager(linearLayoutManagerReminder);

        // List of auto messages elements
        autoMessagesListView = root.findViewById(R.id.fragment_details_buddy_list_auto_messages);
        LinearLayoutManager linearLayoutManagerAutoMessage = new LinearLayoutManager(getContext());
        linearLayoutManagerAutoMessage.setOrientation(LinearLayoutManager.VERTICAL);
        autoMessagesListView.setLayoutManager(linearLayoutManagerAutoMessage);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initialize dialogs
        calendarDialog = new NeedCalendarDialogFragment();
        specialFeaturesDialog = new NeedSpecialFeaturesDialogFragment();

        // Contact attributes
        contact = null;
        if(savedInstanceState != null)
            contact = savedInstanceState.getParcelable(CONTACT_KEY);
        else if(getArguments()!=null) {
            contact = getArguments().getParcelable(BuddyActivity.EXTRA_BUDDY);
        }

        // For insert memory getAutoSmsById RawId only when showMessage details
        setHasOptionsMenu(true);

        // Initialize adapters
        if(contact != null && contact.hasBirthday()) {
            if(PreferencesHelper.isCustomCalendarActive(getContext())) {
                // Add default reminders and link view to adapter
                remindersAdapter = new ReminderCalendarNotificationsAdapter(getContext(), contact.getBirthday());
                remindersListView.setAdapter(remindersAdapter);

                // Build default elements
                try {
                    CalendarEvent event = EventLoader.getNextEventOrCreateNewFromContact(getContext(), contact);
                    List<Reminder> reminders = ReminderLoader.getRemindersFromEvent(getContext(), event);
                    event.addReminders(reminders);
                    Log.d(TAG, "Get event from calendar : " + event.toString());
                    remindersAdapter.addReminders(reminders);

                    // Attach observer for changes
                    // TODO Update each event with all reminders
                    remindersAdapter.registerReminderObserver(
                            new ReminderCalendarProviderObserver(getContext(), contact, event));
                    remindersAdapter.registerReminderObserver(
                            new ReminderToastObserver(getContext()));
                } catch (EventLoader.EventException e) {
                    Log.e(TAG, "Error when build event and reminders : " + e.getLocalizedMessage());
                }
            } else {
                remindersListView.setVisibility(View.GONE);
            }

            // Build adapters only if daemons active
            if(PreferencesHelper.isDaemonsActive(getContext())) {
                // Link auto messages view to adapter
                autoMessagesAdapter = new AutoMessageAdapter(getContext(), contact.getBirthday());
                autoMessagesListView.setAdapter(autoMessagesAdapter);
                // Add elements in database
                List<AutoSms> autoSmsList = AutoSmsDbHelper.getDbHelper(getContext())
                        .getListAutoSmsByLookupKeyAndStatus(contact.getLookUpKey(), AutoSms.Status.PENDING);
                autoMessagesAdapter.addReminders(
                        ProxyAutoMessage.getFromAutoSmsList(contact.getNextBirthdayWithoutHour(), autoSmsList)
                );
                // Attach observers
                autoMessagesAdapter.registerReminderObserver(
                        new AutoSmsDatabaseObserver(contact, getContext()));
                autoMessagesAdapter.registerReminderObserver(
                        new AutoSmsToastObserver(getContext()));
            } else {
                autoMessagesListView.setVisibility(View.GONE);
            }

            // Link menu to adapter
            menuContact = MenuContactCreator.emptyMenuContact();
            menuAdapter = new MenuAdapter(getContext(), menuContact);
            menuListView.setAdapter(menuAdapter);

            // Retrieve the phone number
            if(!contact.isPhoneNumberInit()) {
                retrievePhoneNumber();
            } else {
                try {
                    defineMenuContact(contact.getPhoneNumbers());
                } catch (PhoneNumberNotInitializedException e) {
                    Log.e(TAG, "Error to getAutoSmsById phone number : " + e.getLocalizedMessage());
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(contact != null) {
            ContactLoader.assignRawContactIdToContact(getContext(), contact);

            selectBirthdayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO by activity creation
                    ((AnniversaryDialogOpen) getActivity()).openAnniversaryDialogSelection(contact);
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

                // Animation for menu
                menuViewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viewButton) {
                        viewButton.setActivated(!viewButton.isActivated());
                        menuAnimationViewCircle
                                .startPoint(menuView.getWidth() - 80, 0)
                                .animate();
                    }
                });
            } else {
                //TODO Error
            }
        }

        // Open the menu if no reminder
        if((remindersAdapter == null || remindersAdapter.getItemCount() < 1)
                && (autoMessagesAdapter == null || autoMessagesAdapter.getItemCount() < 1)) {
            menuViewButton.setActivated(true);
            menuView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Define action menu of current contact
     * @param phoneNumberList Phone numbers for dynamically create menu
     */
    private void defineMenuContact(List<PhoneNumber> phoneNumberList) {
        menuContact = new MenuContactCreator(
                getContext(),
                !phoneNumberList.isEmpty())
                .create();
        menuContact.setActionContactMenu(DetailsBuddyFragment.this);
        // Manage grid for buttons
        if(menuContact.getMenuCount() % 4 == 0)
            spanCount = 2;
        else if(menuContact.getMenuCount() % 3 == 0)
            spanCount = 3;
        else if(menuContact.getMenuCount() % 2 == 0)
            spanCount = 2;
        else if(menuContact.getMenuCount() == 1)
            spanCount = 1;
        gridLayoutManager.setSpanCount(spanCount);
        menuAdapter.setMenuContact(menuContact);
        menuAdapter.notifyDataSetChanged();
    }

    /**
     * Retrieve the phone number with the LookUpKey of contact,
     * Assign the list of phone numbers to "phoneNumbers"
     */
    private void retrievePhoneNumber() {
        ContactPhoneNumberLoader phoneNumberLoader = new ContactPhoneNumberLoader(getContext(), contact);
        phoneNumberLoader.setCallbackActionPhoneNumber(new ContactPhoneNumberLoader.CallbackActionPhoneNumber() {
            @Override
            public void afterActionPhoneNumberInDatabase(List<PhoneNumber> phoneNumberList) {
                defineMenuContact(phoneNumberList);
                // Assign phone numbers to current contact
                contact.setPhoneNumbers(phoneNumberList);
            }
        });
        try {
            LoaderManager.getInstance(this).initLoader(0, null, phoneNumberLoader);
        } catch (Exception exception) {
            Log.e(TAG, "Unable to get phone number", exception);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(CONTACT_KEY, contact);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void doActionMenu(MenuAction menuAction) {
        if(menuAction.getState() == MenuAction.STATE.NOT_AVAILABLE) {
            if (!BuildConfig.FULL_VERSION)
                new ProFeatureDialogFragment().show(getFragmentManager(), "PRO_FEATURE_DIALOG_TAG");
        } else {
            menuViewButton.setActivated(false);
            menuAnimationViewCircle.hide();
            switch (menuAction.getItemId()) {
                case MenuActionCalendar.ITEM_ID :
                    IntentCall.openCalendarAt(getActivity(), contact.getNextBirthday());
                    break;
                case MenuActionMessage.ITEM_ID :
                    try {
                        IntentCall.openSMSApp(getContext(),
                                contact.getMainPhoneNumber().getNumber(),
                                ""); // TODO Default message
                    } catch (PhoneNumberNotInitializedException | NoPhoneNumberException e) {
                        Log.e(TAG, "Unable to call message action : " + e.getLocalizedMessage());
                    }
                    break;
                case MenuActionCall.ITEM_ID :
                    try {
                        IntentCall.openCallApp(getContext(),
                                contact.getMainPhoneNumber().getNumber());
                    } catch (PhoneNumberNotInitializedException | NoPhoneNumberException e) {
                        Log.e(TAG, "Unable to make call action : " + e.getLocalizedMessage());
                    }
                    break;
                case MenuActionReminder.ITEM_ID :
                    if(PreferencesHelper.isCustomCalendarActive(getContext())) {
                        remindersAdapter.addDefaultItem();
                    } else {
                        calendarDialog.show(getFragmentManager(), CALENDAR_DIALOG_TAG);
                    }
                    break;
                case MenuActionGift.ITEM_ID :
                    if(PreferencesHelper.isDaemonsActive(getContext())) {
                        // TODO Gift for pro
                    } else {
                        specialFeaturesDialog.show(getFragmentManager(), SPECIAL_FEATURES_DIALOG_TAG);
                    }
                    break;
                case MenuActionAutoMessage.ITEM_ID :
                    if(PreferencesHelper.isDaemonsActive(getContext())) {
                        autoMessagesAdapter.addDefaultItem();
                        // TODO Auto-message for pro
                    } else {
                        specialFeaturesDialog.show(getFragmentManager(), SPECIAL_FEATURES_DIALOG_TAG);
                    }
                    break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_details_buddy, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
                getActivity().finish();
                break;
            case R.id.action_modify_contact:
                if(contact != null) {
                    ((IntentCall.OnContactModify) getActivity()).onEventContactModify();
                }
                break;
            case R.id.action_delete:
                if(contact != null) {
                    AlertDialog.Builder builderDialog = new AlertDialog.Builder(getContext());
                    builderDialog.setMessage(R.string.dialog_delete_birthday_message);
                    builderDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Delete anniversary in database
                            ContactProvider.RemoveBirthdayContactProvider removeBirthdayContactProvider =
                                    new ContactProvider.RemoveBirthdayContactProvider(
                                            getActivity(),
                                            contact.getDataAnniversaryId(),
                                            contact.getBirthday());
                            // Response in activity
                            removeBirthdayContactProvider.setCallbackActionBirthday(
                                    (ContactProvider.CallbackActionBirthday) getActivity());
                            removeBirthdayContactProvider.execute();

                            // Delete event in calendar
                            if(PreferencesHelper.isCustomCalendarActive(getContext())) {
                                EventLoader.deleteEventsFromContact(getContext(), contact);
                            }
                        }
                    });
                    builderDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    builderDialog.create().show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
