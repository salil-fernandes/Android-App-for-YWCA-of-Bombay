package com.sevatech.ywca.navigation.analytics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sevatech.ywca.Connection_Detector;
import com.sevatech.ywca.R;
import com.sevatech.ywca.helper.UserData;
import com.sevatech.ywca.navigation.FullScreenActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_APPEND;

public class DetailedAnalyticsFragment extends Fragment {

    private DetailedAnalyticsAdapter mAdapter;
    private ArrayList<UserData> mData = new ArrayList<>();
    private ArrayList<UserData> mDataClick = new ArrayList<>();
    private DatabaseReference mDatabaseRef;
    private TextView textViewClicks;
    private TextView textViewRegistered;
    private String imageUrl, key;
    private int clickCount, registerCount;
    String msg="";
    String title=null;
    SharedPreferences mPrefs;
    Connection_Detector connection_detector;

    public DetailedAnalyticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detailed_analytics, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.registeredUsersList);
        TextView textViewTitle = view.findViewById(R.id.titleTextView);
        TextView textViewDate = view.findViewById(R.id.dateTextView);
        textViewClicks = view.findViewById(R.id.clicksTextView);
        textViewRegistered = view.findViewById(R.id.registeredTextView);
//        imageView = view.findViewById(R.id.image_view_detail);
        //    private ImageView imageView;
        Button buttonShare = view.findViewById(R.id.button_share);

        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(llm);
        mAdapter = new DetailedAnalyticsAdapter(this.getContext(), mData);
        recyclerView.setAdapter(mAdapter);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("registration");
        Bundle args = getArguments();
        if (args != null) {
            title = args.getString("title");
            String date = args.getString("date");
            clickCount = args.getInt("click");
            registerCount = args.getInt("register");
            String click = String.valueOf(clickCount);
            String register = String.valueOf(registerCount);
            imageUrl = args.getString("imageUrl");
            key = args.getString("key");

            textViewTitle.setText(title);
            textViewClicks.setText(click);
            textViewRegistered.setText(register);
            textViewDate.setText(String.format("Date: %s", date));
//          Picasso.get().load(imageUrl).fit().centerCrop().into(imageView);
        }


        buttonShare.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                check_connection();
                mPrefs = getContext().getSharedPreferences("Flag", Context.MODE_APPEND);
                int X = mPrefs.getInt("Flag", 0);
                //Toast.makeText(getContext(),"Value="+X,Toast.LENGTH_LONG).show();
                if (X == 1) {
                    Log.d("Share", msg);
                    Intent s = new Intent(Intent.ACTION_SEND);
                    s.setType("text/plain");
                    s.putExtra(Intent.EXTRA_TEXT, msg);
                    startActivity(s);
                }
            }
        });

//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent fullScreenIntent = new Intent(getContext(), FullScreenActivity.class);
//                fullScreenIntent.putExtra("imageUrl", imageUrl);
//                startActivity(fullScreenIntent);
//            }
//        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mDatabaseRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mData.clear();
                mDataClick.clear();
                msg=title+"\n";

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserData data = postSnapshot.getValue(UserData.class);
                    if (data != null && data.getUserType().equals("2")) {
                        mData.add(data);
                        msg = String.format("%s\n%s\n%s\n%s\n", msg, data.getUserName(), data.getUserContact(), data.getUserEmail());
                    }

                    mDataClick.add(data);
                }

                Log.d("Message", msg);
                int click = mDataClick.size();
                int register = mData.size();
                Log.d("size", String.valueOf(register));
                if (click != clickCount || register != registerCount)
                    updateCount(click, register);
                textViewRegistered.setText(String.valueOf(register));
                textViewClicks.setText(String.valueOf(click));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateCount(final int click, final int register) {
        final DatabaseReference mBackupRef = FirebaseDatabase.getInstance().getReference("eventsBackup");
        Query q = mBackupRef.child(key).orderByChild("eventClickCount");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mBackupRef.child(key).child("eventClickCount").setValue(click);
                mBackupRef.child(key).child("eventRegisterCount").setValue(register);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void check_connection() {
        connection_detector = new Connection_Detector(getContext());
        connection_detector.execute();
    }

}

