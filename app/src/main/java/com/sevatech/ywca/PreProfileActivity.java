package com.sevatech.ywca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sevatech.ywca.navigation.FragmentBaseActivity;

import java.util.Map;

public class PreProfileActivity extends AppCompatActivity {

    TextView preTextView1, preTextView2, preTextView4, preTextView5, preTextView6, preTextView7, preTextView9, preTextView10;
    //TextView  preTextView3, preTextView8;
    Button btnPreUpdate;
    DatabaseReference databaseReference;
    DatabaseReference memberRef;
    DatabaseReference staffRef;
    SharedPreferences sp;
    SharedPreferences mPrefs;
    Connection_Detector connection_detector;

    @SuppressLint("WrongConstant")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_profile);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }

        preTextView1 = findViewById(R.id.preTextView1);
        preTextView2 = findViewById(R.id.preTextView2);
        //preTextView3=findViewById(R.id.preTextView3);
        preTextView4 = findViewById(R.id.preTextView4);
        preTextView5 = findViewById(R.id.preTextView5);
        preTextView6 = findViewById(R.id.preTextView6);
        preTextView7 = findViewById(R.id.preTextView7);
        //preTextView8=findViewById(R.id.preTextView8);
        preTextView9 = findViewById(R.id.preTextView9);
        preTextView10 = findViewById(R.id.preTextView10);
        btnPreUpdate = findViewById(R.id.btnPreUpdate);

        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

        sp = getSharedPreferences("f1", MODE_PRIVATE);

        check_connection();
        mPrefs = getSharedPreferences("Flag", MODE_APPEND);
        int X = mPrefs.getInt("Flag", 0);
        if (X == 1) {

            String status = sp.getString("status", "");
            String k = sp.getString("memKey", "");
            String key = sp.getString("staffKey", "");

            databaseReference = FirebaseDatabase.getInstance().getReference();


            if (status.equals("Member")) {
                memberRef = databaseReference.child("Members").child(k);
                memberRef.keepSynced(true);
                memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                        Log.d("Profile", String.valueOf(value));
                        final String name1 = String.valueOf(value.get("name"));
                        final String phone1 = String.valueOf(value.get("phone"));
                        //final String date1 =  String.valueOf(value.get("dob"));
                        final String loc1 = String.valueOf(value.get("closest_ywca"));
                        final String email1 = String.valueOf(value.get("email"));
                        final String stat1 = String.valueOf(value.get("status"));
                        final String doj1 = String.valueOf(value.get("year_of_joining"));
                        //final String ID1 = String.valueOf(value.get("id"));
                        final String prof1 = String.valueOf(value.get("profession"));
                        final String add1 = String.valueOf(value.get("address"));

                        preTextView1.setText("Name: " + name1);
                        preTextView2.setText("Phone no: " + phone1);
                        //preTextView3.setText("Date Of Birth: " + date1);
                        preTextView4.setText("Closest Ywca Location: " + loc1);
                        preTextView5.setText("Email ID: " + email1);
                        preTextView6.setText("Current Status: " + stat1);
                        preTextView7.setText("Year of joining: " + doj1);
                        //preTextView8.setText("Member ID: " + ID1);
                        preTextView9.setText("Profession: " + prof1);
                        preTextView10.setText("Address: " + add1);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                btnPreUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(PreProfileActivity.this, ProfileActivity.class);
                        startActivity(i);


                    }
                });
            } else {
                staffRef = databaseReference.child("staff").child(key);
                staffRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                        Log.d("ProfileStaff", String.valueOf(value));
                        final String name1 = String.valueOf(value.get("name"));
                        final String phone1 = String.valueOf(value.get("phone"));
                        //final String date1 =  String.valueOf(value.get("dateofbirth"));
                        final String loc1 = String.valueOf(value.get("location"));
                        final String email1 = String.valueOf(value.get("email"));
                        final String stat1 = String.valueOf(value.get("department"));
                        final String doj1 = String.valueOf(value.get("designation"));
                        //final String ID1 = String.valueOf(value.get("empcode"));

                        preTextView1.setText("Name: " + name1);
                        preTextView2.setText("Phone no: " + phone1);
                        //preTextView3.setText("Date Of Birth: " + date1);
                        preTextView4.setText("Closest Ywca Location: " + loc1);
                        preTextView5.setText("Email ID: " + email1);
                        preTextView6.setText("Department: " + stat1);
                        preTextView7.setText("Designation: " + doj1);
                        //preTextView8.setText("Staff ID: " + ID1);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                btnPreUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(PreProfileActivity.this, "Contact the admin if you want to make changes in your profile!!!", Toast.LENGTH_LONG).show();

                    }
                });
            }
        }
    }


    public void onBackPressed() {
        super.onBackPressed();
//        Intent i = new Intent(PreProfileActivity.this, FragmentBaseActivity.class);
//        startActivity(i);
        finish();
    }

    public void check_connection() {
        connection_detector = new Connection_Detector(this);
        connection_detector.execute();
    }
}
