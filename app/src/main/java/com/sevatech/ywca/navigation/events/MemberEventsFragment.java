package com.sevatech.ywca.navigation.events;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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

//For Member and Staff
public class MemberEventsFragment extends Fragment implements EventsAdapter.OnItemLongClickListener {

    private RecyclerView mRecyclerView;
    private EventsAdapter mAdapter;

    SharedPreferences sp;
    FirebaseAuth firebaseAuth;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseRefBackup;
    private ValueEventListener mDBListener;
    private ArrayList<EventsData> mData = new ArrayList<>();
    private ArrayList<EventsData> mDataAll = new ArrayList<>();
    private DetailedEventFragment detailedFragment = new DetailedEventFragment();
    private TextView noEventsTextView;
    private ProgressBar progressBar;

    private String[] mLocations = new String[]{"Andheri", "Bandra",
            "Belapur", "Borivali", "Byculla",
            "Chembur", "Fort", "Thane"
    };

    public MemberEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_events, container, false);
        NavigationView navigationView = getActivity().findViewById(R.id.navigation_view);
        Menu drawer = navigationView.getMenu();
        MenuItem menuItem;
        menuItem = drawer.findItem(R.id.nav_events);
        if(!menuItem.isChecked())
            menuItem.setChecked(true);

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new EventsAdapter(this.getContext(), mData);
//        llm.setReverseLayout(true);
//        llm.setStackFromEnd(true);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        progressBar = view.findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.VISIBLE);
        noEventsTextView = view.findViewById(R.id.no_events);
        noEventsTextView.setVisibility(View.GONE);

        setHasOptionsMenu(true);

        firebaseAuth = FirebaseAuth.getInstance();
        sp = getActivity().getSharedPreferences("f1", Context.MODE_PRIVATE);
        String status = sp.getString("status", "");
        String userName = status.equals("Member") ? sp.getString("memberName", "") : sp.getString("staffName", "");
        System.out.println(userName);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("events/member");
        mDatabaseRefBackup = FirebaseDatabase.getInstance().getReference("eventsBackup");
        mDatabaseRef.keepSynced(true);

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mData.clear();
                mDataAll.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    EventsData eventsData = postSnapshot.getValue(EventsData.class);
                    eventsData.setEventKey(postSnapshot.getKey());
                    mData.add(eventsData);
                    mDataAll.add(eventsData);
                }

                Collections.sort(mData, new Comparator<EventsData>() {
                    @Override
                    public int compare(EventsData eventsData, EventsData t1) {

                        if (getDiff(eventsData.getEventDate()) > getDiff(t1.getEventDate())) {
                            return 1;
                        } else if (getDiff(eventsData.getEventDate()) < getDiff(t1.getEventDate())) {
                            return -1;
                        }
                        return 0;
//                        return getDiff(eventsData.getEventDate()) < 0 ? -1 : 1;
                    }
                });
                mDataAll = new ArrayList<>(mData);
                mAdapter.notifyDataSetChanged();
                mAdapter.setOnItemLongClickListener(MemberEventsFragment.this);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();

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
        int diff = currentDate - d;
        return diff;
    }

    @Override
    public void OnItemClick(int position) {

        EventsData clickedItem = mData.get(position);

        Bundle args = new Bundle();
        args.putString("title", clickedItem.getEventTitle());
        args.putString("desc", clickedItem.getEventDescription());
        args.putString("venue", clickedItem.getEventVenue());
        args.putString("amount", clickedItem.getEventAmount());
        args.putString("time", clickedItem.getEventTime());
        args.putString("date", clickedItem.getEventDate());
        args.putString("deadline",clickedItem.getEventDeadline());
        args.putString("imageUrl", clickedItem.getEventImageUrl());
        args.putString("key", clickedItem.getEventKey());
        args.putString("type", clickedItem.getEventType());


        detailedFragment.setArguments(args);

        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailedFragment)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void OnDelete(final int position) {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.filter_menu, menu);
        final MenuItem item = menu.findItem(R.id.filter_location);
        final SearchView searchView = (SearchView) item.getActionView();
        final SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, mLocations);
        searchAutoComplete.setAdapter(searchAdapter);
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String searchString = (String) parent.getItemAtPosition(position);
                searchAutoComplete.setText(searchString);
            }
        });
        searchView.setIconified(true);

        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);

        searchView.setQueryHint("Search by Event Venue");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mAdapter.getFilter().filter(newText.toLowerCase().trim());
                return false;

            }

        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mAdapter = new EventsAdapter(getActivity(), mDataAll);
                mRecyclerView.setAdapter(mAdapter);
                //when canceling the search
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}
