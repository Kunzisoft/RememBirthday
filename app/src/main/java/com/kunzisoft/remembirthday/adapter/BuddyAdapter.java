package com.kunzisoft.remembirthday.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.Buddy;

import java.util.List;

/**
 * Created by joker on 15/12/16.
 */

public class BuddyAdapter extends RecyclerView.Adapter<BuddyAdapter.BuddyViewHolder>{

    private static final String TAG = "BuddyAdapter";

    private OnClickItemBuddyListener onClickItemBuddyListener;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };
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
        holder.stayDays.setText(String.valueOf(Buddy.getStayDays(currentBuddy.getBirthday())));

        if(onClickItemBuddyListener != null) {
            holder.container.setOnClickListener(new BufferBuddyClickListener(currentBuddy));
        }
    }

    public OnClickItemBuddyListener getOnClickItemBuddyListener() {
        return onClickItemBuddyListener;
    }

    /**
     * Add click buddy listener to each item
     * @param onClickItemBuddyListener
     */
    public void setOnClickItemBuddyListener(OnClickItemBuddyListener onClickItemBuddyListener) {
        this.onClickItemBuddyListener = onClickItemBuddyListener;
    }

    public void setItemChecked(Buddy buddy) {
        // TODO
    }

    @Override
    public int getItemCount() {
        return listBuddy.size();
    }

    /**
     * Holder for buddy list
     */
    class BuddyViewHolder extends RecyclerView.ViewHolder {

        protected ViewGroup container;

        protected CircularImageView icon;
        protected TextView name;
        protected TextView stayDays;

        public BuddyViewHolder(View itemView) {
            super(itemView);

            container = (ViewGroup) itemView.findViewById(R.id.buddy_item_container);

            icon = (CircularImageView) itemView.findViewById(R.id.buddy_icon);
            name = (TextView) itemView.findViewById(R.id.buddy_name);
            stayDays = (TextView) itemView.findViewById(R.id.buddy_stay_days);
        }
    }

    /**
     * Listener when a click on buddy item is performed
     */
    public interface OnClickItemBuddyListener {
        void onItemBuddyClick(View view, Buddy buddy);
    }

    private class BufferBuddyClickListener implements View.OnClickListener {

        private Buddy buddy;

        public BufferBuddyClickListener(Buddy buddy) {
            this.buddy = buddy;
        }

        @Override
        public void onClick(View view) {
            onClickItemBuddyListener.onItemBuddyClick(view, buddy);
        }
    }
}
