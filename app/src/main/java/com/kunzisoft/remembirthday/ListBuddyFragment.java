package com.kunzisoft.remembirthday;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kunzisoft.remembirthday.adapter.BuddyAdapter;
import com.kunzisoft.remembirthday.element.Buddy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by joker on 08/01/17.
 */

public class ListBuddyFragment extends Fragment {

    private BuddyAdapter buddyAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO List users
        List<Buddy> listBuddy = new ArrayList<>();
        Buddy michel = new Buddy("michmich", new Date());
        listBuddy.add(michel);

        Buddy jeanmichel = new Buddy("jeanmichmich", new Date());
        listBuddy.add(jeanmichel);

        Buddy bernadette = new Buddy("berna dettes", new Date());
        listBuddy.add(bernadette);

        buddyAdapter = new BuddyAdapter(listBuddy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_buddy, container, false);

        // List buddies
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.list_buddy);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(buddyAdapter);

        return rootView;
    }

    /**
     * Assign onClickItemBuddyListener to adapter
     * @param onClickItemBuddyListener
     */
    void setOnClickItemBuddyListener(BuddyAdapter.OnClickItemBuddyListener onClickItemBuddyListener) {
        buddyAdapter.setOnClickItemBuddyListener(onClickItemBuddyListener);
    }
}
