package com.kunzisoft.remembirthday;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kunzisoft.remembirthday.adapter.BuddyAdapter;
import com.kunzisoft.remembirthday.element.Buddy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by joker on 14/12/16.
 */

public class ListBuddyActivity extends AppCompatActivity {

    private static final String TAG = "ListBuddyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_buddy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_buddy);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_buddy);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // TODO List users
        List<Buddy> listBuddy = new ArrayList<>();
        Buddy michel = new Buddy();
        michel.setName("michmich");
        michel.setDate(new Date());
        listBuddy.add(michel);

        Buddy jeanmichel = new Buddy();
        jeanmichel.setName("jeanmichmich");
        jeanmichel.setDate(new Date());
        listBuddy.add(jeanmichel);

        Buddy bernadette = new Buddy();
        bernadette.setName("berna dettes");
        bernadette.setDate(new Date());
        listBuddy.add(bernadette);

        BuddyAdapter buddyAdapter = new BuddyAdapter(listBuddy);
        recyclerView.setAdapter(buddyAdapter);
    }
}
