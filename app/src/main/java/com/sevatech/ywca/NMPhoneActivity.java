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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class NMPhoneActivity extends AppCompatActivity {


    TextView tvNMPhone, tvFtu;
    EditText etPhoneNM;
    Button btnNextNM, btnSignUp;
    ProgressBar NMPhonePb;
    DatabaseReference databaseReference;
    DatabaseReference NMRef;
    String phoneNum = null;
    SharedPreferences sp;
    SharedPreferences mPrefs;
    Connection_Detector connection_detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_n_m_phone);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }

        tvNMPhone = findViewById(R.id.tvNMPhone);
        etPhoneNM=findViewById(R.id.etPhoneNM);
        btnNextNM = findViewById(R.id.btnNextNM);
        NMPhonePb=findViewById(R.id.NMPhonePb);
        btnSignUp=findViewById(R.id.btnSignUp);

        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

        sp=getSharedPreferences("f1", MODE_PRIVATE);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        NMRef = databaseReference.child("NonMember");


        btnNextNM.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {

                check_connection();
                mPrefs=getSharedPreferences("Flag", MODE_APPEND);
                int X = mPrefs.getInt("Flag",0);
                if (X==1) {
                    final String regexStr = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";
                    phoneNum = etPhoneNM.getText().toString();
                    if (phoneNum.length() != 10 ||  !phoneNum.matches(regexStr)) {
                        etPhoneNM.setError("Enter the proper number");
                        etPhoneNM.requestFocus();
                        return;
                    }


                    NMPhonePb.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    final Query queryRef = NMRef.orderByChild("phone").equalTo(phoneNum);
                    Log.d("Error", String.valueOf(queryRef));


                    if (phoneNum != null) {
                        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists()){

                                    String name1 = null;
                                    String k = null;

                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        k = ds.getKey();
                                        name1 = ds.child("name").getValue(String.class);
                                    }

                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("name", name1);
                                    editor.putString("phone", phoneNum);
                                    editor.putString("nmKey", k);
                                    editor.apply();

                                    Intent intent = new Intent(getApplicationContext(), NMOTPActivity.class);
                                    intent.putExtra("phoneNum", phoneNum);
                                    startActivity(intent);
                                    finish();

                                }
                                else
                                {
                                    NMPhonePb.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Toast.makeText(getApplicationContext(), "Phone number doesn't exist.", Toast.LENGTH_SHORT).show();
                                    etPhoneNM.setText("");
                                    etPhoneNM.requestFocus();

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

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NMPhoneActivity.this, NonMemberActivity.class);
                startActivity(i);
                finish();
            }
        });

    }


    public void onBackPressed() {
        super.onBackPressed();
        NMPhonePb.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Intent i = new Intent(NMPhoneActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void check_connection()
    {
        connection_detector = new Connection_Detector(this);
        connection_detector.execute();
    }
}
