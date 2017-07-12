package com.kunzisoft.remembirthday.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.element.Reminder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Abstract class for reminders adapter
 * Created by joker on 01/07/17.
 */
public abstract class AbstractReminderAdapter<E extends Reminder, T extends ReminderViewHolder> extends RecyclerView.Adapter<T>{

    protected Context context;
    protected DateUnknownYear anniversary;
    protected List<E> listReminders;

    private SimpleDateFormat reminderDateFormatter;

    public AbstractReminderAdapter(Context context, DateUnknownYear anniversary) {
        this.context = context;
        this.anniversary = anniversary;
        listReminders = new LinkedList<>();
    }

    /**
     * Add list of reminders at the end of current list
     * @param reminders
     */
    public void addReminders(List<E> reminders) {
        int start = listReminders.size();
        if(!reminders.isEmpty()) {
            listReminders.addAll(reminders);
            this.notifyItemRangeChanged(start, reminders.size() - 1);
        }
    }

    /**
     * Add a reminder to the list
     * @param reminder The reminder to add
     */
    public void addReminder(E reminder) {
        listReminders.add(reminder);
        this.notifyItemChanged(listReminders.size() - 1);
    }

    /**
     * Add a default reminder to the list, init day with delta of anniversary, hour and minute to default
     */
    public abstract void addDefaultItem(int deltaDay);

    /**
     * Add a default reminder to the list with the day of anniversary, init hour and minute to default
     */
    public abstract void addDefaultItem();

    /**
     * Get list of reminders generated
     * @return The list
     */
    public List<E> getListReminders() {
        return listReminders;
    }

    @Override
    public void onBindViewHolder(T holder, int position) {
        E currentReminder = listReminders.get(position);

        reminderDateFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.dateNotification.setText(reminderDateFormatter.format(currentReminder.getDate()));
        holder.dateNotification.setOnClickListener(new OnClickValidateHour(currentReminder));

        // Construct spinner for 364 days
        List<Integer> listDays = new ArrayList<>();
        for(int i = 0; i<=364; i++) {
            listDays.add(i);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item,
                listDays);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.unitsBefore.setAdapter(adapter);
        holder.unitsBefore.setSelection(currentReminder.getDeltaDay());
        holder.unitsBefore.setOnItemSelectedListener(new OnDaySelected(currentReminder, listDays));

        holder.deleteButton.setOnClickListener(new OnClickRemoveButton(currentReminder));
    }

    @Override
    public int getItemCount() {
        return listReminders.size();
    }

    /**
     * Class for manage remove listener of select reminder
     */
    private class OnClickRemoveButton implements View.OnClickListener {

        private E reminder;

        OnClickRemoveButton(E reminder) {
            this.reminder = reminder;
        }

        @Override
        public void onClick(View view) {
            int position = listReminders.indexOf(reminder);
            notifyItemRemoved(position);
            listReminders.remove(position);
        }
    }

    /**
     * Class for manage validation of hour and minute in dialog
     */
    private class OnClickValidateHour implements View.OnClickListener {

        private E reminder;

        OnClickValidateHour(E reminder) {
            this.reminder = reminder;
        }

        @Override
        public void onClick(final View view) {
            TimePickerDialog timePickerDialog;
            timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    // Change hours and minutes in reminder
                    reminder.setHourOfDay(hourOfDay);
                    reminder.setMinuteOfHour(minute);
                    Log.d(this.getClass().getSimpleName(), "Assign new hour for reminder : " + reminder.getDate().toString());

                    // Set text in view
                    ((TextView) view).setText(reminderDateFormatter.format(reminder.getDate()));
                }
            }, reminder.getHourOfDay(), reminder.getMinuteOfHour(), true);
            timePickerDialog.show();
        }
    }

    /**
     * Class for manage select day in list
     */
    private class OnDaySelected implements AdapterView.OnItemSelectedListener {

        private E reminder;
        private List<Integer> listDays;

        public OnDaySelected(E reminder, List<Integer> listDays) {
            this.reminder = reminder;
            this.listDays = listDays;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            // New Date when delta days is selected
            reminder.setDeltaDay(listDays.get(position));
            Log.d(this.getClass().getSimpleName(), "Assign new date for reminder : " + reminder.getDate().toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {}
    }
}
