package com.sevatech.ywca.navigation.events;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sevatech.ywca.R;
import com.sevatech.ywca.helper.EventsData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;


public class AdminEventsFragment extends Fragment implements EventsAdapter.OnItemLongClickListener {

    private RecyclerView mRecyclerView;
    private EventsAdapter mAdapter;

    private SharedPreferences sp;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseRefBackup;
    private ValueEventListener mDBListener;
    private ArrayList<EventsData> mData = new ArrayList<>();
    private ArrayList<EventsData> mDataAll = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView noEventsTextView;
    private DetailedEventFragment detailedFragment = new DetailedEventFragment();


    //    public SearchView searchView = null;
    private String[] mLocations = new String[]{"Andheri", "Bandra",
            "Belapur", "Borivali", "Byculla",
            "Chembur", "Fort", "Thane"
    };

    public AdminEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_events, container, false);
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

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getContext());
                alertDialogBuilder.setMessage("Do you want to exit?");
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this,callback);

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
                    if (eventsData != null) {
                        eventsData.setEventKey(postSnapshot.getKey());
                    }
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
                mAdapter.setOnItemLongClickListener(AdminEventsFragment.this);
                progressBar.setVisibility(View.GONE);
                if(mData.size()==0)
                    noEventsTextView.setVisibility(View.VISIBLE);
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
        return currentDate - d;
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
        args.putString("deadline", clickedItem.getEventDeadline());
        args.putString("imageUrl", clickedItem.getEventImageUrl());
        args.putString("key", clickedItem.getEventKey());
        args.putString("type", clickedItem.getEventType());


        detailedFragment.setArguments(args);

        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailedFragment)
                    .addToBackStack(null)
                    .commit();
        }

    }


    @Override
    public void OnDelete(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Do you want to delete this Event?");
        alertDialogBuilder.setCancelable(true);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String eventType = mData.get(position).getEventType().equals("Members Only") ? "member" : "everyone";
                final DatabaseReference mEveryoneRef = FirebaseDatabase.getInstance().getReference("events/everyone");
                final DatabaseReference mRegistrationRef = FirebaseDatabase.getInstance().getReference("registration");
                EventsData selectedEvent = mData.get(position);
                final String selectedKey = selectedEvent.getEventKey();

                StorageReference imageRef = mStorage.getReferenceFromUrl(selectedEvent.getEventImageUrl());
                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (eventType.equals("everyone"))
                            mEveryoneRef.child(selectedKey).removeValue();
                        mDatabaseRef.child(selectedKey).removeValue();
                        mDatabaseRefBackup.child(selectedKey).removeValue();
                        mRegistrationRef.child(selectedKey).removeValue();
                        Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#417eca"));
        alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#417eca"));
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
        searchView.setIconified(true);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String searchString = (String) parent.getItemAtPosition(position);
                searchAutoComplete.setText(searchString);
            }
        });
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
