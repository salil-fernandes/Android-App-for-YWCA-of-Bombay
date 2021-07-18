package com.sevatech.ywca.navigation.analytics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.sevatech.ywca.R;
import com.sevatech.ywca.helper.EventsData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class AnalyticsFragment extends Fragment implements AnalyticsAdapter.OnItemClickListener {

    private RecyclerView mListView;
    private ProgressBar progressBar;
    private AnalyticsAdapter mAdapter;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;

    private ArrayList<EventsData> mData = new ArrayList<>();

    public AnalyticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_analytics,container,false);
        NavigationView navigationView = getActivity().findViewById(R.id.navigation_view);
        Menu drawer = navigationView.getMenu();
        MenuItem menuItem;
        menuItem = drawer.findItem(R.id.nav_analytics);
        if(!menuItem.isChecked())
            menuItem.setChecked(true);

        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        progressBar = view.findViewById(R.id.progress_bar);
        mListView = view.findViewById(R.id.listView);
//        if(mData.size()<10)
//            mListView.setItemViewCacheSize(0);
        mListView.setLayoutManager(llm);
        mAdapter = new AnalyticsAdapter(this.getContext(),mData);
        mListView.setAdapter(mAdapter);

        progressBar.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("eventsBackup");
        mDatabaseRef.keepSynced(true);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mData.clear();
                if(dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        EventsData eventsData = postSnapshot.getValue(EventsData.class);
                        assert eventsData != null;
                        eventsData.setEventKey(postSnapshot.getKey());
                        mData.add(eventsData);
                    }
                    if(mData!=null) {
                        Collections.sort(mData, new Comparator<EventsData>() {
                            @Override
                            public int compare(EventsData eventsData, EventsData t1) {

                                if (getDiff(eventsData.getEventDate()) > getDiff(t1.getEventDate())) {
                                    return 1;
                                } else if (getDiff(eventsData.getEventDate()) < getDiff(t1.getEventDate())) {
                                    return -1;
                                }
                                return 0;
                            }
                        });
                        mAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                    mAdapter.setOnItemClickListener(AnalyticsFragment.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }

    private int getDiff(String date) {
        String[] dateParts = date.split("/");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int d = month * 31 + day;
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int currentDate = currentMonth * 31 + currentDay;
        return currentDate - d;
    }

    @Override
    public void OnItemCLick(int position) {

        EventsData clickedItem = mData.get(position);

        Bundle args = new Bundle();
        args.putString("title", clickedItem.getEventTitle());
        args.putString("date", clickedItem.getEventDate());
        args.putInt("click", clickedItem.getEventClickCount());
        args.putInt("register", clickedItem.getEventRegisterCount());
        args.putString("imageUrl", clickedItem.getEventImageUrl());
        args.putString("key",clickedItem.getEventKey());

        DetailedAnalyticsFragment detailedAnalyticsFragment = new DetailedAnalyticsFragment();
        detailedAnalyticsFragment.setArguments(args);

        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailedAnalyticsFragment)
                .addToBackStack(null)
                .commit();
    }
}
