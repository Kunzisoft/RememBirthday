package com.kunzisoft.remembirthday;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kunzisoft.remembirthday.element.Buddy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by joker on 08/01/17.
 */

public class DetailsBuddyFragment extends Fragment {

    private static final String TAG = "DETAILS_BUDDY_FRAGMENT";

    public void setBuddy(Buddy currentBuddy) {
        Bundle args = new Bundle();
        args.putParcelable(BuddyActivity.EXTRA_BUDDY, currentBuddy);
        setArguments(args);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_details_buddy, container, false);

        ImageView avatarImageView = (ImageView) root.findViewById(R.id.fragment_details_buddy_avatar);
        TextView dayAndMonthTextView = (TextView) root.findViewById(R.id.fragment_details_buddy_dayAndMonth);
        TextView yearTextView = (TextView) root.findViewById(R.id.fragment_details_buddy_year);

        Buddy buddy = null;
        if(getArguments()!=null) {
            buddy = getArguments().getParcelable(BuddyActivity.EXTRA_BUDDY);
        }
        if(buddy != null) {
            Date currentBuddyBirthday = buddy.getBirthday();
            SimpleDateFormat dayAndMonthSimpleDateFormat = new SimpleDateFormat("dd MMMM", Locale.FRENCH);
            SimpleDateFormat yearSimpleDateFormat = new SimpleDateFormat("yyyy", Locale.FRENCH);
            dayAndMonthTextView.setText(dayAndMonthSimpleDateFormat.format(currentBuddyBirthday));
            yearTextView.setText(yearSimpleDateFormat.format(currentBuddyBirthday));
        }

        return root;
    }
}
