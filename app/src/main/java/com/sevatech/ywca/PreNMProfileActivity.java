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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sevatech.ywca.navigation.FragmentBaseActivity;

import java.util.Map;

public class PreNMProfileActivity extends AppCompatActivity {

    TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9;
    Button btnPreNMUpdate;
    DatabaseReference databaseReference;
    DatabaseReference NMRef;
    SharedPreferences sp;
    SharedPreferences mPrefs;
    Connection_Detector connection_detector;

    @SuppressLint({"WrongConstant", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_n_m_profile);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);
        tv6 = findViewById(R.id.tv6);
        tv7 = findViewById(R.id.tv7);
        tv8 = findViewById(R.id.tv8);
        tv9 = findViewById(R.id.tv9);
        btnPreNMUpdate = findViewById(R.id.btnPreNMUpdate);

        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

        sp = getSharedPreferences("f1", MODE_PRIVATE);

        check_connection();
        mPrefs = getSharedPreferences("Flag", MODE_APPEND);
        int X = mPrefs.getInt("Flag", 0);
        if (X == 1) {

        databaseReference = FirebaseDatabase.getInstance().getReference();
        String key = sp.getString("nmKey", "");

        NMRef = databaseReference.child("NonMember").child(key);

        NMRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                Log.d("Profile", String.valueOf(value));

                final String name1 = String.valueOf(value.get("name"));
                final String phone1 = String.valueOf(value.get("phone"));
                final String email1 = String.valueOf(value.get("email"));
                final String date1 = String.valueOf(value.get("dob"));
                final String gender1 = String.valueOf(value.get("gender"));
                final String loc1 = String.valueOf(value.get("location"));
                final String institute1 = String.valueOf(value.get("institute"));
                final String interest1 = String.valueOf(value.get("interest"));
                final String prof1 = String.valueOf(value.get("profession"));

                Log.d("Phone", phone1);

                tv1.setText("Name: " + name1);
                tv2.setText("Phone no: " + phone1);
                tv3.setText("Email ID: " + email1);
                tv4.setText("Date Of Birth: " + date1);
                tv5.setText("Gender: " + gender1);
                tv6.setText("Closest Ywca Location: " + loc1);
                tv7.setText("Institute/Organisation: " + institute1);
                tv8.setText("Interested in Being a member: " + interest1);
                tv9.setText("Profession: " + prof1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnPreNMUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PreNMProfileActivity.this, NMProfileActivity.class);
                startActivity(i);
                finish();
            }
        });

    }


    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void check_connection()
    {
        connection_detector = new Connection_Detector(this);
        connection_detector.execute();
    }
}
