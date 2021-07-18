package com.sevatech.ywca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sevatech.ywca.helper.ProfileData;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    EditText etUpdateName, etUpdatePhone, etUpdateLocation, etUpdateEmail, etUpdateProf, etUpdateAddress;
    Button btnUpdate;
    ProgressBar profilePb;
    DatabaseReference databaseReference;
    DatabaseReference approvalRef;
    DatabaseReference memberRef;
    DatabaseReference myRef;
    SharedPreferences sp;
    String name1 = null;
    String phone1 = null;
    String loc1 = null;
    String add1 = null;
    String prof1 = null;
    String email1 = null;
    SharedPreferences mPrefs;
    Connection_Detector connection_detector;
    Map<String, Object> map = new HashMap<>();

    @SuppressLint("StaticFieldLeak")
    public static Activity profileActivity;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }

        profileActivity = this;
        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

        sp = getSharedPreferences("f1", MODE_PRIVATE);

        etUpdateName = findViewById(R.id.etUpdateName);
        etUpdatePhone = findViewById(R.id.etUpdatePhone);
        etUpdateLocation = findViewById(R.id.etUpdateLocation);
        etUpdateEmail = findViewById(R.id.etUpdateEmail);
        etUpdateProf = findViewById(R.id.etUpdateProf);
        etUpdateAddress = findViewById(R.id.etUpdateAddress);


        btnUpdate = findViewById(R.id.btnUpdate);

        profilePb = findViewById(R.id.profilePb);


        final String k = sp.getString("memKey", "");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        myRef = databaseReference.child("Members");
        memberRef = databaseReference.child("Members").child(k);
        approvalRef = databaseReference.child("approval");
        Log.d("Key", String.valueOf(k));


        memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                Log.d("Profile", String.valueOf(value));
                name1 = String.valueOf(value.get("name"));
                phone1 = String.valueOf(value.get("phone"));
                loc1 = String.valueOf(value.get("closest_ywca"));
                email1 = String.valueOf(value.get("email"));
                prof1 = String.valueOf(value.get("profession"));
                add1 = String.valueOf(value.get("address"));

                etUpdateName.setText(name1);
                etUpdatePhone.setText(phone1);
                etUpdateLocation.setText(loc1);
                etUpdateEmail.setText(email1);
                etUpdateProf.setText(prof1);
                etUpdateAddress.setText(add1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                check_connection();
                mPrefs = getSharedPreferences("Flag", MODE_APPEND);
                int X = mPrefs.getInt("Flag", 0);
                if (X == 1) {
                    final String n = etUpdateName.getText().toString();
                    if (n.length() == 0) {
                        etUpdateName.setError("Enter a proper name");
                        etUpdateName.requestFocus();
                        return;
                    }

                    final String regexStr = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";
                    final String phoneNum = etUpdatePhone.getText().toString();
                    if (phoneNum.length() != 10 || !phoneNum.matches(regexStr)) {
                        etUpdatePhone.setError("Enter the proper number");
                        etUpdatePhone.requestFocus();
                        return;
                    }


                    final String l = etUpdateLocation.getText().toString();
                    if (l.length() == 0) {
                        etUpdateLocation.setError("Location cannot be empty");
                        etUpdateLocation.requestFocus();
                        return;
                    }

                    final String address = etUpdateAddress.getText().toString();
                    if (address.length() == 0) {
                        etUpdateAddress.setError("Enter your profession!");
                        etUpdateAddress.requestFocus();
                        return;
                    }

                    final String profession = etUpdateProf.getText().toString();
                    final String e = etUpdateEmail.getText().toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setMessage("Thank you for submitting your details! The fields in the profile will be modified automatically once the details entered are cross checked by the admin. \n\n Do you want to continue?");
                    builder.setCancelable(false);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        profilePb.setVisibility(View.VISIBLE);
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        if (!prof1.equals(profession)) {
                            memberRef.child("profession").setValue(profession);
                            etUpdateProf.setText(profession);
                        }

                            if (!name1.equals(n) || !loc1.equals(l) || !email1.equals(e) || !add1.equals(address)
                                    || !phone1.equals(phoneNum)) {
                                ProfileData data = new ProfileData(k, name1, phone1, loc1, email1, add1);
                                approvalRef.child(k).setValue(data);
                            }


                        if (!name1.equals(n)) {
                            map.put("name", n);
                            etUpdateName.setText(n);
//                    memberRef.child("name").setValue(n);
//                    SharedPreferences.Editor editor = sp.edit();
//                    editor.putString("memberName", n);
//                    editor.apply();

                            }

                            if (!loc1.equals(l)) {
                                map.put("closest_ywca", l);
//                    memberRef.child("closest_ywca").setValue(l);
                                etUpdateLocation.setText(l);
                            }

                            if (!email1.equals(e)) {
                                final Query queryRef = myRef.orderByChild("email").equalTo(e);
                                queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            profilePb.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Toast.makeText(getApplicationContext(), "Email ID belongs to someone else!", Toast.LENGTH_SHORT).show();
                                            etUpdateEmail.setText("");
                                            etUpdateEmail.requestFocus();

                                        } else {
                                            map.put("email", e);
                                            approvalRef.child(k).child("email").setValue(e);
//                                memberRef.child("email").setValue(e);
                                            etUpdateEmail.setText(e);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }


                            if (!add1.equals(address)) {
                                map.put("address", address);
//                    memberRef.child("address").setValue(address);
                                etUpdateAddress.setText(address);

                            }

                            if (!phone1.equals(phoneNum)) {
                                final Query queryRef = myRef.orderByChild("phone").equalTo(phoneNum);
                                queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            profilePb.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Toast.makeText(getApplicationContext(), "Phone no. belongs to someone else!", Toast.LENGTH_SHORT).show();
                                            etUpdatePhone.setText("");
                                            etUpdatePhone.requestFocus();

                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), ProfileOtpActivity.class);
                                            intent.putExtra("phoneNum", phoneNum);
                                            startActivity(intent);
//                                finish();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }

//                        System.out.println(map);

                            if (map.values().size() != 0) {
                                approvalRef.child(k).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

//                            map.clear();
                                        profilePb.setVisibility(View.GONE);
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        if (imm != null) {
                                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                                        }
                                        finish();

                                    }
                                });

                            }

                        profilePb.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                    }
                });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });


                    AlertDialog alert = builder.create();
                    alert.setTitle("Update profile");
                    alert.show();
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#417eca"));
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#417eca"));


                }
            }
        });

        profilePb.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    public void onBackPressed() {
        super.onBackPressed();
        profilePb.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//        Intent i = new Intent(ProfileActivity.this, PreProfileActivity.class);
//        startActivity(i);
        finish();
    }

    public void check_connection() {
        connection_detector = new Connection_Detector(this);
        connection_detector.execute();
    }
}
