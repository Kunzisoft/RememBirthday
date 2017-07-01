package com.kunzisoft.remembirthday.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.AutoMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter who manage list of auto messages
 */
public class AutoMessagesAdapter extends RecyclerView.Adapter<AutoMessagesViewHolder>{

    private List<AutoMessage> listAutoMessages;

    public AutoMessagesAdapter() {
        listAutoMessages = new ArrayList<>();
        //TODO Init list of messages
    }

    public void addAutoMessage(AutoMessage autoMessage) {
        listAutoMessages.add(autoMessage);
        this.notifyItemChanged(listAutoMessages.size() - 1);
    }

    public void addDefaultAutoMessage() {
        listAutoMessages.add(new AutoMessage());
        this.notifyItemChanged(listAutoMessages.size() - 1);
    }

    @Override
    public AutoMessagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_auto_messages, parent, false);
        return new AutoMessagesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AutoMessagesViewHolder holder, int position) {

        AutoMessage currentAutoMessage = listAutoMessages.get(position);
        // TODO assign content of message
        holder.messageContent.setText(currentAutoMessage.getContent());
        holder.dateNotification.setText(currentAutoMessage.getDate().toString());
        holder.deleteButton.setOnClickListener(new OnClickRemoveButton(position));
    }

    @Override
    public int getItemCount() {
        return listAutoMessages.size();
    }

    /**
     * Class for manage remove listener of select message
     */
    private class OnClickRemoveButton implements View.OnClickListener {

        private int positionInList;

        OnClickRemoveButton(int position) {
            positionInList = position;
        }

        @Override
        public void onClick(View view) {
            listAutoMessages.remove(positionInList);
            notifyDataSetChanged();
        }
    }
}
