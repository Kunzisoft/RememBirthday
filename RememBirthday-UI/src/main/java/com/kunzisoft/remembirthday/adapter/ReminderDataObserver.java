package com.kunzisoft.remembirthday.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.kunzisoft.remembirthday.element.CalendarEvent;

/**
 * Created by joker on 02/08/17.
 */
public class ReminderDataObserver extends RecyclerView.AdapterDataObserver {

    // TODO reminders

    private Context context;
    private CalendarEvent calendarEvent;

    public ReminderDataObserver(Context context, CalendarEvent event) {
        this.context = context;
        this.calendarEvent = event;
    }

    @Override
    public void onChanged() {
        super.onChanged();

    }
}
