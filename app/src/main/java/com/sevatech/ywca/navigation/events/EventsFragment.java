package com.sevatech.ywca.navigation.events;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.sevatech.ywca.R;
import com.sevatech.ywca.helper.EventsData;
import com.sevatech.ywca.navigation.aboutUs.AboutUsFragment;
import com.sevatech.ywca.navigation.aboutUs.MembershipFragment;
import com.sevatech.ywca.navigation.contactUs.ContactUsFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

//For Non-members
public class EventsFragment extends Fragment implements EventsAdapter.OnItemLongClickListener {

    private RecyclerView mRecyclerView;
    private EventsAdapter mAdapter;

    private SharedPreferences sp;
    private DatabaseReference mDatabaseRef;
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
    public EventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        sp = getActivity().getSharedPreferences("f1", Context.MODE_PRIVATE);
        boolean firstStart = sp.getBoolean("firstStart", true);
        if (firstStart) {
            showStartDialog();
        }
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
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        progressBar = view.findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.VISIBLE);

        noEventsTextView = view.findViewById(R.id.no_events);
        noEventsTextView.setVisibility(View.GONE);

        setHasOptionsMenu(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("events/everyone");
        DatabaseReference mDatabaseRefBackup = FirebaseDatabase.getInstance().getReference("eventsBackup");
        mDatabaseRef.keepSynced(true);
        Query q =mDatabaseRef;

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mData.clear();
                mDataAll.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    EventsData eventsData = postSnapshot.getValue(EventsData.class);
                    assert eventsData != null;
                    eventsData.setEventKey(postSnapshot.getKey());
                    mData.add(eventsData);
                    mDataAll.add(eventsData);
                }

                Collections.sort(mData, new Comparator<EventsData>() {
                    @Override
                    public int compare(EventsData eventsData, EventsData t1) {

                        if (getDiff(eventsData.getEventDate()) > getDiff(t1.getEventDate())) {
                            return 1;
                        }

                        else if (getDiff(eventsData.getEventDate()) < getDiff(t1.getEventDate())) {
                            return -1;
                        }
                        return 0;
//                        return getDiff(eventsData.getEventDate()) < 0 ? -1 : 1;
                    }
                });
                mDataAll = new ArrayList<>(mData);
                mAdapter.notifyDataSetChanged();
                mAdapter.setOnItemLongClickListener(EventsFragment.this);
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

    private void showStartDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        Button btn_ok = mView.findViewById(R.id.btn_ok);
        TextView textView = mView.findViewById(R.id.pop_up_text);
        String membershipString = getResources().getString(R.string.pop_up_detail);
        int i1 = membershipString.indexOf("[");
        int i2 = membershipString.indexOf("]");
        membershipString = membershipString.replace("[","").replace("]","");
        SpannableString spannableString = new SpannableString(membershipString);

        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                MembershipFragment membershipFragment = new MembershipFragment();
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, membershipFragment).commit();
                alertDialog.dismiss();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan, i1, i2 - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
        sp.edit().putBoolean("firstStart",false).apply();
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
        args.putString("deadline", clickedItem.getEventDeadline());
        args.putString("imageUrl", clickedItem.getEventImageUrl());
        args.putString("key", clickedItem.getEventKey());
        args.putString("type", clickedItem.getEventType());


        detailedFragment.setArguments(args);

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
