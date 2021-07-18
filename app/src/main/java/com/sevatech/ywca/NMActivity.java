package com.sevatech.ywca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NMActivity extends AppCompatActivity {

    TextView tvAssure, tvLocation, tvInterest, tvProfession;
    EditText etPhone, etProfession;
    Spinner spnLocation;
    RadioGroup rgInterest;
    Button btnSubmit;
    ProgressBar nmPb;
    String phoneNum = null;
    FirebaseDatabase database;
    String pNum = null;
    DatabaseReference databaseReference;
    DatabaseReference myRef, memRef, sRef;
    SharedPreferences sp;
    boolean flag, flag1;
    SharedPreferences mPrefs;
    Connection_Detector connection_detector;



    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nm);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }

        tvAssure = findViewById(R.id.tvAssure);
        tvLocation=findViewById(R.id.tvLocation);
        etPhone=findViewById(R.id.etPhone);
        tvInterest=findViewById(R.id.tvInterest);
        spnLocation=findViewById(R.id.spnLocation);
        rgInterest=findViewById(R.id.rgInterest);
        tvProfession=findViewById(R.id.tvProfession);
        etProfession=findViewById(R.id.etProfession);
        btnSubmit  =findViewById(R.id.btnSubmit);
        nmPb= findViewById(R.id.nmPb);


        database=FirebaseDatabase.getInstance();
        myRef= database.getReference("NonMember");
        memRef=database.getReference("Members");
        sRef = database.getReference("staff");

        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);


        final ArrayList<String> s = new ArrayList<>();
        s.add("Chembur");
        s.add("Bandra");
        s.add("Andheri");
        s.add("Fort");
        s.add("Byculla");
        s.add("Borivali");
        s.add("Belapur");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, s);

        spnLocation.setAdapter(arrayAdapter);

        sp=getSharedPreferences("f1", MODE_PRIVATE);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {

                check_connection();
                mPrefs=getSharedPreferences("Flag", MODE_APPEND);
                int X = mPrefs.getInt("Flag",0);
                if (X==1) {

                    final String regexStr = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";
                    final String p = etPhone.getText().toString();
                    if (p.length() != 10 || !p.matches(regexStr)) {
                        etPhone.setError("Invalid phone no.");
                        etPhone.setText("");
                        etPhone.requestFocus();
                        return;
                    }

                    final String prof = etProfession.getText().toString();

                    pNum=p;

                    nmPb.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                    final Query memberRef = memRef.orderByChild("phone").equalTo(pNum);
                    //Log.d("Error", String.valueOf(memberRef));

                    memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                flag = true;
                                Toast.makeText(NMActivity.this, "Already a member! Go and sign in as a member", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                flag = false;
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    final Query staffRef = sRef.orderByChild("phone").equalTo(pNum);

                    staffRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                flag1 = true;
                                Toast.makeText(NMActivity.this, "Already a staff! Go and sign in as a staff", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                flag1 = false;
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    final Query queryRef = myRef.orderByChild("phone").equalTo(pNum);


                    if (pNum != null) {

                        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists() || flag || flag1){

                                    nmPb.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Toast.makeText(getApplicationContext(), "Phone number is already registered!! Go and Sign In!", Toast.LENGTH_LONG).show();
                                    etPhone.setText("");
                                    etPhone.requestFocus();

                                }
                                else
                                {
                                    nmPb.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                                    AlertDialog.Builder builder = new AlertDialog.Builder(NMActivity.this);
                                    builder.setMessage("Shall we submit this information??");
                                    builder.setCancelable(false);

                                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //finish();
                                            //finishAffinity();

                                            int id = rgInterest.getCheckedRadioButtonId();
                                            RadioButton rb = findViewById(id);
                                            String inter = rb.getText().toString();

                                            int spnid = spnLocation.getSelectedItemPosition();
                                            String l = s.get(spnid);

                                            nmPb.setVisibility(View.VISIBLE);
                                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putString("phone", p);
                                            editor.apply();

                                            String dob = getIntent().getExtras().getString("dob");
                                            String email = getIntent().getExtras().getString("email");
                                            String gender = getIntent().getExtras().getString("gender");
                                            String institute = getIntent().getExtras().getString("institute");

                                            nmPb.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                            Intent z = new Intent(NMActivity.this, NewNonMemberActivity.class);
                                            z.putExtra("dob", dob);
                                            z.putExtra("email", email);
                                            z.putExtra("gender", gender);
                                            z.putExtra("institute", institute);
                                            z.putExtra("interest", inter);
                                            z.putExtra("location", l);
                                            z.putExtra("profession", prof);

                                            startActivity(z);
                                            //finish();


                                        }
                                    });

                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });


                                    AlertDialog alert = builder.create();
                                    alert.setTitle("Submit??");
                                    alert.show();
                                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#417eca"));
                                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#417eca"));



                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }



                }


            }
        });

    }

    public void onBackPressed()
    {

        super.onBackPressed();
        nmPb.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        SharedPreferences.Editor editor = sp.edit();
        String status1="";
        editor.putString("name", status1);
        editor.commit();

        // Intent i = new Intent(NMActivity.this, NonMemberActivity.class);
        //startActivity(i);
        //finish();
    }

    public void check_connection()
    {
        connection_detector = new Connection_Detector(this);
        connection_detector.execute();
    }

}
