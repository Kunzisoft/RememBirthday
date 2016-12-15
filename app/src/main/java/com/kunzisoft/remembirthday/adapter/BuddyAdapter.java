package com.kunzisoft.remembirthday.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.Buddy;

import java.util.List;

/**
 * Created by joker on 15/12/16.
 */

public class BuddyAdapter extends RecyclerView.Adapter<BuddyAdapter.BuddyViewHolder>{

    private static final String TAG = "BuddyAdapter";

    private List<Buddy> listBuddy;

    public BuddyAdapter(List<Buddy> listBuddy) {
        this.listBuddy = listBuddy;
    }

    @Override
    public BuddyAdapter.BuddyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemListBuddyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_buddy, parent, false);
        return new BuddyViewHolder(itemListBuddyView);
    }

    @Override
    public void onBindViewHolder(BuddyViewHolder holder, int position) {
        Buddy currentBuddy = listBuddy.get(position);
        // TODO icon
        //holder.icon.
        holder.name.setText(currentBuddy.getName());
        holder.stayDays.setText(String.valueOf(Buddy.getStayDays(currentBuddy.getDate())));
    }

    @Override
    public int getItemCount() {
        return listBuddy.size();
    }

    /**
     * Holder for buddy list
     */
    class BuddyViewHolder extends RecyclerView.ViewHolder {

        protected ImageView icon;
        protected TextView name;
        protected TextView stayDays;

        public BuddyViewHolder(View itemView) {
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.buddy_icon);
            name = (TextView) itemView.findViewById(R.id.buddy_name);
            stayDays = (TextView) itemView.findViewById(R.id.buddy_stay_days);
        }
    }
}
