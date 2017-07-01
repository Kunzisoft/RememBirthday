package com.kunzisoft.remembirthday.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kunzisoft.remembirthday.element.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joker on 01/07/17.
 */

public abstract class AbstractReminderAdapter<E extends Reminder, T extends ReminderViewHolder> extends RecyclerView.Adapter<T>{

    protected List<E> listReminders;

    public AbstractReminderAdapter() {
        listReminders = new ArrayList<>();
        //TODO Init list of messages
    }

    public void addReminder(E reminder) {
        listReminders.add(reminder);
        this.notifyItemChanged(listReminders.size() - 1);
    }

    public abstract void addDefaultItem();

    @Override
    public void onBindViewHolder(T holder, int position) {
        E currentReminder = listReminders.get(position);
        holder.dateNotification.setText(currentReminder.getDate().toString());
        holder.deleteButton.setOnClickListener(new OnClickRemoveButton(position));
    }

    @Override
    public int getItemCount() {
        return listReminders.size();
    }

    /**
     * Class for manage remove listener of select reminder
     */
    protected class OnClickRemoveButton implements View.OnClickListener {

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
}
