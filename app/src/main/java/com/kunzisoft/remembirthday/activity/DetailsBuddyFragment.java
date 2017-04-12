package com.kunzisoft.remembirthday.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.Utility;
import com.kunzisoft.remembirthday.element.Contact;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity who show the details of buddy selected
 */
public class DetailsBuddyFragment extends Fragment {

    private static final String TAG = "DETAILS_BUDDY_FRAGMENT";

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

        Contact contact = null;
        if(getArguments()!=null) {
            contact = getArguments().getParcelable(BuddyActivity.EXTRA_BUDDY);
        }
        if(contact != null) {
            // Display date
            Date currentBuddyBirthday = contact.getBirthday().getDate();
            SimpleDateFormat dayAndMonthSimpleDateFormat = new SimpleDateFormat("dd MMMM", Locale.FRENCH);
            SimpleDateFormat yearSimpleDateFormat = new SimpleDateFormat("yyyy", Locale.FRENCH);

            dayAndMonthTextView.setText(dayAndMonthSimpleDateFormat.format(currentBuddyBirthday));

            if(contact.getBirthday().containsYear()) {
                yearTextView.setVisibility(View.VISIBLE);
                yearTextView.setText(yearSimpleDateFormat.format(currentBuddyBirthday));
            } else {
                yearTextView.setVisibility(View.GONE);
                yearTextView.setText("");
                //TODO Add button for year
            }

            // Number days left before birthday
            Utility.assignDaysRemainingInTextView(daysLeftTextView, contact.getBirthdayDaysRemaining());

        }
        return root;
    }
}
