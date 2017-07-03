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

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
        listReminders = new ArrayList<>();
        //TODO Init list of messages
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
        holder.unitsBefore.setSelection(
                Days.daysBetween(
                        new DateTime(anniversary.getDate()),
                        new DateTime(currentReminder.getDate())).getDays());
        holder.unitsBefore.setOnItemSelectedListener(new OnDaySelected(currentReminder, listDays));

        holder.deleteButton.setOnClickListener(new OnClickRemoveButton(position));
    }

    @Override
    public int getItemCount() {
        return listReminders.size();
    }

    /**
     * Class for manage remove listener of select reminder
     */
    private class OnClickRemoveButton implements View.OnClickListener {

        private int positionInList;

        OnClickRemoveButton(int position) {
            positionInList = position;
        }

        @Override
        public void onClick(View view) {
            listReminders.remove(positionInList);
            notifyDataSetChanged();
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
            final Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(reminder.getDate());

            TimePickerDialog timePickerDialog;
            timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    // Change hour in reminder
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    reminder.setDate(calendar.getTime());
                    Log.d(this.getClass().getSimpleName(), "Assign new hour for reminder : " + reminder.getDate().toString());

                    // Set text in view
                    ((TextView) view).setText(reminderDateFormatter.format(reminder.getDate()));
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
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
            DateTime anniversaryDate = new DateTime(anniversary.getDate());
            DateTime initDate = new DateTime(reminder.getDate());
            initDate = initDate.withDate(anniversaryDate.getYear(), anniversaryDate.getMonthOfYear(), anniversaryDate.getDayOfMonth());
            reminder.setDate(
                    (initDate)
                            .minusDays(listDays.get(position))
                            .toDate());
            Log.d(this.getClass().getSimpleName(), "Assign new date for reminder : " + reminder.getDate().toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {}
    }
}
