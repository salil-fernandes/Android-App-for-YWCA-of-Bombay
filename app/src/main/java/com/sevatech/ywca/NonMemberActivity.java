package com.sevatech.ywca;

import androidx.annotation.NonNull;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class NonMemberActivity extends AppCompatActivity {

    Button btnForward;
    RadioGroup rgGender;
    EditText etName, etEmail, etDobNM, etInstitute;
    TextView tvNonMember, tvGender, tvInstitute, tvBlank;
    FirebaseDatabase database;
    String pEmail = null;
    DatabaseReference myRef;
    ProgressBar nonmemberPb;
    SharedPreferences sp;
    Connection_Detector connection_detector;
    SharedPreferences mPrefs;
    Calendar calendar = Calendar.getInstance();
    private DatePickerDialog datePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_member);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }

        btnForward=findViewById(R.id.btnForward);
        rgGender=findViewById(R.id.rgGender);
        tvBlank= findViewById(R.id.tvBlank);
        etName=findViewById(R.id.etName);
        etDobNM = findViewById(R.id.etDobNM);
        etEmail=findViewById(R.id.etEmail);
        tvNonMember=findViewById(R.id.tvNonMember);
        tvInstitute = findViewById(R.id.tvInstitute);
        etInstitute=findViewById(R.id.etInstitute);
        nonmemberPb = findViewById(R.id.nonmemberPb);
        tvGender=findViewById(R.id.tvGender);

        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("NonMember");

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                updateLabel();
            }
        };

        etDobNM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(NonMemberActivity.this,R.style.DialogTheme, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        sp=getSharedPreferences("f1", MODE_PRIVATE);

        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

        btnForward.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {

                check_connection();
                mPrefs=getSharedPreferences("Flag", MODE_APPEND);
                int X = mPrefs.getInt("Flag",0);
                if (X==1) {
                    final String n = etName.getText().toString();
                    if (n.length() == 0 || !Pattern.matches("[a-zA-Z][a-zA-Z ]+[a-zA-Z ]$", n)) {
                        etName.setError("Enter a proper name");
                        etName.requestFocus();
                        return;
                    }

                    final String d = etDobNM.getText().toString();
                    if (d.length() == 0) {
                        etDobNM.setError("Invalid Date");
                        etDobNM.setText("");
                        etDobNM.requestFocus();
                        return;
                    }
                    final String e = etEmail.getText().toString();
                    if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                        etEmail.setError("Invalid email address");
                        etEmail.setText("");
                        etEmail.requestFocus();
                        return;
                    }



                    final String in = etInstitute.getText().toString();

                    int id = rgGender.getCheckedRadioButtonId();
                    RadioButton rb = findViewById(id);
                    final String g = rb.getText().toString();


                    pEmail=e;

                    nonmemberPb.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    final Query queryRef = myRef.orderByChild("email").equalTo(pEmail);
                    Log.d("Error", String.valueOf(queryRef));

                    if(pEmail!=null)
                    {
                        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists()){

                                    nonmemberPb.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Toast.makeText(getApplicationContext(), "Email is already registered!!", Toast.LENGTH_SHORT).show();
                                    etEmail.setText("");
                                    etEmail.requestFocus();

                                }
                                else
                                {

                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("name", n);
                                    editor.commit();

                                    nonmemberPb.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    Intent intent = new Intent(NonMemberActivity.this, NMActivity.class);
                                    intent.putExtra("dob", d);
                                    intent.putExtra("email", e);
                                    intent.putExtra("gender", g);
                                    intent.putExtra("institute", in);
                                    startActivity(intent);
                                    //finish();

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

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        etDobNM.setText(sdf.format(calendar.getTime()));
    }





    public void onBackPressed() {
        super.onBackPressed();
        nonmemberPb.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Intent i = new Intent(NonMemberActivity.this, NMPhoneActivity.class);
        startActivity(i);
        finish();
    }

    public void check_connection()
    {
        connection_detector = new Connection_Detector(this);
        connection_detector.execute();
    }
}
