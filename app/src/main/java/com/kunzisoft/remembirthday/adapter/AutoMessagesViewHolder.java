package com.kunzisoft.remembirthday.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kunzisoft.remembirthday.R;

/**
 * Created by joker on 01/07/17.
 */

public class AutoMessagesViewHolder extends RecyclerView.ViewHolder {

    TextView messageContent;
    TextView dateNotification;
    ImageView deleteButton;

    public AutoMessagesViewHolder(View itemView) {
        super(itemView);

        messageContent = (TextView) itemView.findViewById(R.id.item_list_auto_message_content);
        dateNotification = (TextView) itemView.findViewById(R.id.item_list_auto_message_date_notification);
        deleteButton = (ImageView) itemView.findViewById(R.id.item_list_auto_message_button_delete);
    }
}
