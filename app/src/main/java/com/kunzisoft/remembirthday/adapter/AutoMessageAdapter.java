package com.kunzisoft.remembirthday.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.AutoMessage;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.preference.PreferencesManager;


/**
 * Adapter who manage list of auto messages
 */
public class AutoMessageAdapter extends AbstractReminderAdapter<AutoMessage, AutoMessageViewHolder> {

    private static final String TAG = "AutoMessageAdapter";

    public AutoMessageAdapter(Context context, DateUnknownYear anniversary) {
        super(context, anniversary);
    }

    @Override
    public void addDefaultItem() {
        int[] defaultTime = PreferencesManager.getDefaultTime(context);
        addReminder(new AutoMessage(anniversary.getDate(), defaultTime[0], defaultTime[1], 0));
    }

    @Override
    public AutoMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_auto_messages, parent, false);
        return new AutoMessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AutoMessageViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        AutoMessage currentAutoMessage = listReminders.get(position);
        // TODO assign content of message
        holder.messageContent.setText(currentAutoMessage.getContent());
        holder.messageContent.addTextChangedListener(new OnTextChanged(currentAutoMessage, holder.saveButton));

        holder.saveButton.setVisibility(View.GONE);
        holder.saveButton.setOnClickListener(new OnClickSaveButton(currentAutoMessage));
    }

    private class OnTextChanged implements TextWatcher {

        private AutoMessage autoMessage;
        private View textView;

        private OnTextChanged(AutoMessage autoMessage, View textView) {
            this.autoMessage = autoMessage;
            this.textView = textView;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            autoMessage.setContent(charSequence.toString());
        }
        @Override
        public void afterTextChanged(Editable editable) {
            textView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Class for manage insert listener of select reminder
     */
    private class OnClickSaveButton implements View.OnClickListener {

        private AutoMessage autoMessage;

        OnClickSaveButton(AutoMessage autoMessage) {
            this.autoMessage = autoMessage;
        }

        @Override
        public void onClick(View view) {
            int position = listReminders.indexOf(autoMessage);
            // Notify observable
            for(ReminderDataObserver<AutoMessage> observer : reminderDataObservers) {
                observer.onReminderUpdated(autoMessage);
            }
            notifyItemChanged(position);
        }
    }
}
