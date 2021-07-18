package com.sevatech.ywca.navigation.events;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sevatech.ywca.InternetConnection;
import com.sevatech.ywca.R;
import com.sevatech.ywca.helper.UserData;
import com.sevatech.ywca.navigation.FullScreenActivity;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class DetailedEventFragment extends Fragment {

    private String imageUrl, key, status, currentID, type;

    private FirebaseAuth firebaseAuth;
    SharedPreferences sp;
    private DatabaseReference mDatabaseRef, mBackupRef;
    private long click = 0, register = 0;


    public DetailedEventFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_event, container, false);

        TextView textViewTitle = view.findViewById(R.id.text_event_name_detail);
        TextView textViewDesc = view.findViewById(R.id.text_event_desc_detail);
        TextView textViewVenue = view.findViewById(R.id.text_event_venue_detail);
        TextView textViewDate = view.findViewById(R.id.text_event_date_detail);
        TextView textViewType = view.findViewById(R.id.text_event_type);
        TextView textViewPostDeadline = view.findViewById(R.id.text_event_post_deadline);
        TextView textViewPostDate = view.findViewById(R.id.text_event_post_date);
        TextView textViewAmount = view.findViewById(R.id.text_event_amount_detail);
        TextView textViewTime = view.findViewById(R.id.text_event_time_detail);
        ImageView imageView = view.findViewById(R.id.image_view_detail);
        Button buttonRegister = view.findViewById(R.id.button_event_register);

        firebaseAuth = FirebaseAuth.getInstance();
        sp = getActivity().getSharedPreferences("f1", Context.MODE_PRIVATE);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("registration");
        mBackupRef = FirebaseDatabase.getInstance().getReference("eventsBackup");
        status = sp.getString("status", "");
        currentID = firebaseAuth.getCurrentUser().getUid();


        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title");
            String description = args.getString("desc");
            String venue = args.getString("venue");
            String amount = args.getString("amount");
            String time = args.getString("time");
            final String date = args.getString("date");
            String deadline = args.getString("deadline");
            key = args.getString("key");
            imageUrl = args.getString("imageUrl");
            type = args.getString("type");

            assert type != null;
            if (type.equals("Members Only"))
                textViewType.setVisibility(View.VISIBLE);

            if (status.equals("Admin")) {
                buttonRegister.setVisibility(View.GONE);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getCounts();
                    }
                });
                thread.start();
            } else {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        registeredEvent();
                    }
                });
                thread.start();
            }

            textViewTitle.setText(title);
            textViewDesc.setText(description);
            textViewVenue.setText(String.format("Venue: %s", venue));
            textViewAmount.setText(String.format("Amount: %s", amount));
            textViewTime.setText(String.format("Time: %s", time));
            textViewDate.setText(String.format("Date: %s", date));
            Picasso.get().load(imageUrl).fit().centerCrop().into(imageView);

            //Checking if deadline to register has passed

            Date eventDeadline = null;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            try {
                eventDeadline = sdf.parse(deadline);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Date eventDate = null;
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            try {
                eventDate = sdf1.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (Calendar.getInstance().getTime().after(eventDeadline)) {
//                Toast.makeText(getContext(),"Deadline to register has passed ",Toast.LENGTH_SHORT).show();
                buttonRegister.setVisibility(View.GONE);
                if (!status.equals("Admin")) {
                    textViewPostDeadline.setVisibility(View.VISIBLE);
                    textViewPostDate.setVisibility(View.GONE);
                }
            }

            if (Calendar.getInstance().getTime().after(eventDate)) {
                buttonRegister.setVisibility(View.GONE);
                if (!status.equals("Admin")) {
                    textViewPostDate.setVisibility(View.VISIBLE);
                    textViewPostDeadline.setVisibility(View.GONE);
                }
            }


            buttonRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InternetConnection intconn = new InternetConnection(getContext());
                    if (!intconn.isNetworkAvailable()) {
                        intconn.Dialog(getContext());
                    } else {
                        //Toast.makeText(getContext(),"Value="+X,Toast.LENGTH_LONG).show();
                        Intent registerIntent = new Intent(getContext(), EventRegister.class);
                        registerIntent.putExtra("eventKey", key);
                        startActivity(registerIntent);
                    }
                }
            });

        }


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fullScreenIntent = new Intent(getContext(), FullScreenActivity.class);
                fullScreenIntent.putExtra("imageUrl", imageUrl);
                startActivity(fullScreenIntent);
            }
        });
        return view;
    }


    private void registeredEvent() {
        Query q = mDatabaseRef.child(key).orderByChild("userID").equalTo(currentID);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               /*
                if user is clicking on event for first time,
                push userId and type = 1
                */
                if (!dataSnapshot.exists()) {

                    UserData data = new UserData(currentID, "1");
                    mDatabaseRef.child(key).push().setValue(data);

                    getCounts();
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getCounts() {
        Query q = mDatabaseRef.child(key).orderByChild("userType");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                click = dataSnapshot.getChildrenCount();
                updateCounts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        q.equalTo("2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                register = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updateCounts() {
//        System.out.println(click + " " + register);
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


}
