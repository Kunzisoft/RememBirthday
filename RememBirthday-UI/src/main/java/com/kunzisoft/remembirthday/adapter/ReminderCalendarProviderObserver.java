package com.kunzisoft.remembirthday.adapter;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.util.Log;

import com.kunzisoft.remembirthday.element.CalendarEvent;
import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.Reminder;
import com.kunzisoft.remembirthday.provider.EventLoader;
import com.kunzisoft.remembirthday.provider.ReminderProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer who do actions for reminders
 * Created by joker on 04/08/17.
 */
public class ReminderCalendarProviderObserver implements AbstractReminderAdapter.ReminderDataObserver<Reminder> {
    
    private Context context;
    private CalendarEvent baseEvent;
    private List<CalendarEvent> afterEvents;
    private ContentResolver contentResolver;
    private ArrayList<ContentProviderOperation> ops;
    
    public ReminderCalendarProviderObserver(Context context, Contact contact, CalendarEvent baseEvent) {
        this.context = context;
        this.afterEvents = EventLoader.getEventsSavedOrCreateNewsForEachYearAfterNextEvent(context, contact);
        this.baseEvent = baseEvent;
        this.contentResolver = context.getContentResolver();
        this.ops = new ArrayList<>();
    }
    
    @Override
    public void onReminderAdded(Reminder reminder) {
        ops.add(ReminderProvider.insert(context, baseEvent.getId(), reminder));
        //TODO Add id to reminder
        applyBatch();
    }

    @Override
    public void onRemindersAdded(List<Reminder> reminders) {
        //TODO Add id to reminder
        for(Reminder reminder : reminders) {
            ops.add(ReminderProvider.insert(context, baseEvent.getId(), reminder));
        }
        applyBatch();
    }

    @Override
    public void onReminderUpdated(Reminder reminder) {
        // TODO with link
        ops.add(ReminderProvider.update(context, baseEvent.getId(), reminder));
        for(CalendarEvent afterEvent : afterEvents) {
            //ops.add(ReminderProvider.updateWithUnknownId(context, afterEvent.getId(), reminder, newMinutes));
        }
        applyBatch();
    }

    @Override
    public void onRemindersUpdated(List<Reminder> reminders) {
        for(Reminder reminder : reminders) {
            ops.add(ReminderProvider.update(context, baseEvent.getId(), reminder));
            for(CalendarEvent afterEvent : afterEvents) {
                // TODO with links
                //ops.add(ReminderProvider.update(context, afterEvent.getId(), reminder));
            }
        }
        applyBatch();
    }

    @Override
    public void onReminderDeleted(Reminder reminder) {
        ops.add(ReminderProvider.delete(context, baseEvent.getId(), reminder));
        for(CalendarEvent afterEvent : afterEvents) {
            ops.add(ReminderProvider.deleteWithUnknownId(context, afterEvent.getId(), reminder));
        }
        applyBatch();
    }

    @Override
    public void onRemindersDeleted(List<Reminder> reminders) {
        // TODO delete
        for(Reminder reminder : reminders) {
            ops.add(ReminderProvider.delete(context, baseEvent.getId(), reminder));
            for(CalendarEvent afterEvent : afterEvents) {
                ops.add(ReminderProvider.deleteWithUnknownId(context, afterEvent.getId(), reminder));
            }
        }
        applyBatch();
    }

    /**
     * Apply operations
     */
    private void applyBatch() {
        try {
            ContentProviderResult[] contentProviderResults =
                    contentResolver.applyBatch(CalendarContract.AUTHORITY, ops);
            for(ContentProviderResult result : contentProviderResults)
                if(result.uri != null)
                    Log.d(this.getClass().getSimpleName(), result.uri.toString());
        } catch (RemoteException|OperationApplicationException e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        } finally {
            ops.clear();
        }
    }

}
