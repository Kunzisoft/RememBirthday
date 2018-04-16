package com.kunzisoft.remembirthday.adapter;

import android.view.View;
import android.widget.TextView;

import com.kunzisoft.remembirthday.R;

/**
 * Created by joker on 01/07/17.
 */

public class AutoMessageViewHolder extends ReminderViewHolder {

    TextView messageContent;

    public AutoMessageViewHolder(View itemView) {
        super(itemView);
        messageContent = (TextView) itemView.findViewById(R.id.item_list_auto_message_content);
    }
}
