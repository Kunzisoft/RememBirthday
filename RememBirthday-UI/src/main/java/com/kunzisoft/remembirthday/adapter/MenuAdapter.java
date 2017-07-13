package com.kunzisoft.remembirthday.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.factory.MenuAction;
import com.kunzisoft.remembirthday.factory.MenuFactory;

/**
 * Adapter that handles actions in the contact menu
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder> {

    private static final int MENU_ACTIVE = 0;
    private static final int MENU_NOT_ACTIVE = 1;

    private Context context;
    private MenuFactory menuFactory;

    public MenuAdapter(Context context,
                       MenuFactory menuFactory) {
        this.context = context;
        this.menuFactory = menuFactory;
    }

    @Override
    public int getItemViewType(int position) {
        if(menuFactory.getMenu(position).isActive())
            return MENU_ACTIVE;
        else
            return MENU_NOT_ACTIVE;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemListBuddyView;
        switch (viewType) {
            default:
            case MENU_ACTIVE:
                itemListBuddyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_menu, parent, false);
                break;
            case MENU_NOT_ACTIVE:
                itemListBuddyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_not_active_list_menu, parent, false);
                break;
        }
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
