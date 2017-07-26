package com.kunzisoft.remembirthday.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.factory.MenuAction;
import com.kunzisoft.remembirthday.factory.MenuContact;

/**
 * Adapter that handles actions in the contact menu
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder> {

    private Context context;
    private MenuContact menuFactory;

    public MenuAdapter(Context context,
                       MenuContact menuFactory) {
        this.context = context;
        this.menuFactory = menuFactory;
    }

    @Override
    public int getItemViewType(int position) {
        return menuFactory.getMenu(position).getState().ordinal();
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemListBuddyView;
        if(viewType == MenuAction.STATE.ACTIVE.ordinal())
            itemListBuddyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_menu, parent, false);
        else if(viewType == MenuAction.STATE.INACTIVE.ordinal())
            itemListBuddyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_menu_inactive, parent, false);
        else
            itemListBuddyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_menu_not_available, parent, false);

        return new MenuViewHolder(itemListBuddyView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        MenuAction menuAction = menuFactory.getMenu(position);
        holder.image.setImageResource(menuAction.getImageId());
        holder.title.setText(context.getString(menuAction.getTitleId()));
        holder.container.setOnClickListener(new BufferMenuClickListener(menuAction, position));
    }

    @Override
    public int getItemCount() {
        return menuFactory.getMenuCount();
    }

    public void setMenuContact(MenuContact menuContact) {
        this.menuFactory = menuContact;
    }

    /**
     * Class manager for add contact and view in listener
     */
    private class BufferMenuClickListener implements View.OnClickListener {

        private MenuAction menuAction;
        private int position;

        BufferMenuClickListener(MenuAction menuAction, int position) {
            this.menuAction = menuAction;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            menuAction.doAction(menuAction, position);
        }
    }
}
