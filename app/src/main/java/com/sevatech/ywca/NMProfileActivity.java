package com.sevatech.ywca;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class NMProfileActivity extends AppCompatActivity {

    EditText etNMUpdateName, etNMUpdatePhone, etNMUpdateDob, etNMUpdateLocation, etNMUpdateEmail, etNMUpdateGender, etNMUpdateInstitute, etNMUpdateInterest, etNMUpdateProfession;
    Button btnNMUpdate;
    ProgressBar NmProfilePb;
    DatabaseReference databaseReference;
    DatabaseReference NMRef;
    DatabaseReference testRef;
    SharedPreferences sp;
    String name1 = null;
    String phone1 = null;
    String date1 = null;
    String email1 = null;
    String gender1 = null;
    String loc1 = null;
    String institute1 = null;
    String interest1 = null;
    String prof1 = null;

    SharedPreferences mPrefs;
    Connection_Detector connection_detector;
    Calendar calendar = Calendar.getInstance();
    private DatePickerDialog datePickerDialog;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_n_m_profile);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }

        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

        sp = getSharedPreferences("f1", MODE_PRIVATE);

        etNMUpdateName = findViewById(R.id.etNMUpdateName);
        etNMUpdatePhone = findViewById(R.id.etNMUpdatePhone);
        etNMUpdateDob = findViewById(R.id.etNMUpdateDob);
        etNMUpdateLocation = findViewById(R.id.etNMUpdateLocation);
        etNMUpdateEmail = findViewById(R.id.etNMUpdateEmail);
        etNMUpdateGender = findViewById(R.id.etNMUpdateGender);
        etNMUpdateInstitute = findViewById(R.id.etNMUpdateInstitute);
        etNMUpdateInterest = findViewById(R.id.etNMUpdateInterest);
        etNMUpdateProfession = findViewById(R.id.etNMUpdateProfession);

        btnNMUpdate = findViewById(R.id.btnNMUpdate);

        NmProfilePb = findViewById(R.id.NmProfilePb);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                updateLabel();
            }
        };

        etNMUpdateDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(NMProfileActivity.this, R.style.DialogTheme, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        String key = sp.getString("nmKey", "");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        NMRef = databaseReference.child("NonMember").child(key);
        testRef = databaseReference.child("NonMember");


        NMRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                Log.d("Profile", String.valueOf(value));

                name1 = String.valueOf(value.get("name"));
                phone1 = String.valueOf(value.get("phone"));
                email1 = String.valueOf(value.get("email"));
                date1 = String.valueOf(value.get("dob"));
                gender1 = String.valueOf(value.get("gender"));
                loc1 = String.valueOf(value.get("location"));
                institute1 = String.valueOf(value.get("institute"));
                interest1 = String.valueOf(value.get("interest"));
                prof1 = String.valueOf(value.get("profession"));

                etNMUpdateName.setText(name1);
                Log.d("Phone123", phone1);
                etNMUpdatePhone.setText(phone1);
                etNMUpdateDob.setText(date1);
                etNMUpdateLocation.setText(loc1);
                etNMUpdateEmail.setText(email1);
                etNMUpdateGender.setText(gender1);
                etNMUpdateInstitute.setText(institute1);
                etNMUpdateInterest.setText(interest1);
                etNMUpdateProfession.setText(prof1);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnNMUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Check 1", "aaa");

                check_connection();
                mPrefs = getSharedPreferences("Flag", MODE_APPEND);
                int X = mPrefs.getInt("Flag", 0);
                if (X == 1) {

                    String n = etNMUpdateName.getText().toString();
                    if (n.length() == 0 || !Pattern.matches("[a-zA-Z][a-zA-Z ]+[a-zA-Z ]$", n)) {
                        etNMUpdateName.setError("Enter a proper name");
                        etNMUpdateName.requestFocus();
                        return;
                    }

                    final String regexStr = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";
                    final String phoneNum = etNMUpdatePhone.getText().toString();
                    if (phoneNum.length() != 10 || !phoneNum.matches(regexStr)) {
                        etNMUpdatePhone.setError("Enter the proper number");
                        etNMUpdatePhone.requestFocus();
                        return;
                    }

                    String d = etNMUpdateDob.getText().toString();
                    if (d.length() == 0) {
                        etNMUpdateDob.setError("Invalid Date");
                        etNMUpdateDob.setText("");
                        etNMUpdateDob.requestFocus();
                        return;
                    }

                    final String e = etNMUpdateEmail.getText().toString();
                    if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                        etNMUpdateEmail.setError("Invalid email address");
                        etNMUpdateEmail.setText("");
                        etNMUpdateEmail.requestFocus();
                        return;
                    }

                    String g = etNMUpdateGender.getText().toString();
                    if (g.length() == 0) {
                        etNMUpdateGender.setError("Specify your gender!");
                        etNMUpdateGender.requestFocus();
                        return;
                    }

                    String l = etNMUpdateLocation.getText().toString();
                    if (l.length() == 0) {
                        etNMUpdateLocation.setError("Location cannot be empty");
                        etNMUpdateLocation.requestFocus();
                        return;
                    }

                    String i = etNMUpdateInstitute.getText().toString();
                    String prof = etNMUpdateProfession.getText().toString();

                    String inter = etNMUpdateInterest.getText().toString();
                    if (inter.length() == 0) {
                        etNMUpdateInterest.setError("Interested in being a member?");
                        etNMUpdateInterest.requestFocus();
                        return;
                    }

                    NmProfilePb.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    if (!name1.equals(n)) {
                        NMRef.child("name").setValue(n);
                        etNMUpdateName.setText(n);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("name", n);
                        editor.apply();

                    }
                    Log.d("Check 2", "aaa");

                    if (!date1.equals(d)) {
                        NMRef.child("dob").setValue(d);
                        etNMUpdateDob.setText(d);
                    }

                    if (!email1.equals(e)) {
                        final Query queryRef = testRef.orderByChild("email").equalTo(e);
                        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    NmProfilePb.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Toast.makeText(getApplicationContext(), "Email ID belongs to someone else !!", Toast.LENGTH_SHORT).show();
                                    etNMUpdateEmail.setText("");
                                    etNMUpdateEmail.requestFocus();

                                } else {
                                    NMRef.child("email").setValue(e);
                                    etNMUpdateEmail.setText(e);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    if (!gender1.equals(g)) {
                        NMRef.child("gender").setValue(g);
                        etNMUpdateGender.setText(g);
                    }

                    if (!loc1.equals(l)) {
                        NMRef.child("location").setValue(l);
                        etNMUpdateLocation.setText(l);
                    }
                    if (!institute1.equals(i)) {
                        NMRef.child("institute").setValue(i);
                        etNMUpdateInstitute.setText(i);
                    }
                    if (!interest1.equals(inter)) {
                        NMRef.child("interest").setValue(inter);
                        etNMUpdateInterest.setText(inter);
                    }

                    if (!prof1.equals(prof)) {
                        NMRef.child("profession").setValue(prof);
                        etNMUpdateProfession.setText(prof);
                    }

                    if (!phone1.equals(phoneNum)) {
                        Log.d("Check 3", "aaa");
                        final Query queryRef = testRef.orderByChild("phone").equalTo(phoneNum);
                        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    NmProfilePb.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Toast.makeText(getApplicationContext(), "Phone no. belongs to someone else !!", Toast.LENGTH_SHORT).show();
                                    etNMUpdatePhone.setText("");
                                    etNMUpdatePhone.requestFocus();

                                } else {
                                    Intent intent = new Intent(getApplicationContext(), ProfileOtpActivity.class);
                                    intent.putExtra("phoneNum", phoneNum);
                                    startActivity(intent);
                                    finish();

                                /*
                                Log.d("Check 4", "aaa");
                                NMRef.child("phone").setValue(phoneNum);
                                Log.d("Phone456",phoneNum);
                                etNMUpdatePhone.setText(phoneNum);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("phone", phoneNum);
                                editor.commit();

                                 */
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {


                            }

                        });
                    }

                }


                NmProfilePb.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Toast.makeText(NMProfileActivity.this, "Data has been updated", Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(NMProfileActivity.this, PreNMProfileActivity.class);
                //startActivity(intent);
                //finish();


            }
        });


    }


    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        etNMUpdateDob.setText(sdf.format(calendar.getTime()));
    }

    public void onBackPressed() {
        super.onBackPressed();
        NmProfilePb.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Intent i = new Intent(NMProfileActivity.this, PreNMProfileActivity.class);
        startActivity(i);
        finish();
    }

    public void check_connection() {
        connection_detector = new Connection_Detector(this);
        connection_detector.execute();
    }


}
