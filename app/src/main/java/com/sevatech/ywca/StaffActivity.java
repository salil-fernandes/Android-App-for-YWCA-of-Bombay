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



public class StaffActivity extends AppCompatActivity {

    TextView tvStaff;
    EditText etPhoneStaff;
    Button btnAage2;
    DatabaseReference databaseRef;
    DatabaseReference staffRef;
    ProgressBar staffPb;
    String phoneNum = null;
    SharedPreferences sp;
    SharedPreferences mPrefs;
    Connection_Detector connection_detector;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }

        tvStaff = findViewById(R.id.tvStaff);
        etPhoneStaff=findViewById(R.id.etPhoneStaff);
        btnAage2 = findViewById(R.id.btnAage2);
        staffPb=findViewById(R.id.staffPb);

        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

        databaseRef= FirebaseDatabase.getInstance().getReference();
        staffRef = databaseRef.child("staff");

        sp=getSharedPreferences("f1", MODE_PRIVATE);




        btnAage2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {

                check_connection();
                mPrefs=getSharedPreferences("Flag", MODE_APPEND);
                int X = mPrefs.getInt("Flag",0);
                if (X==1) {
                    final String regexStr = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";
                    phoneNum = etPhoneStaff.getText().toString();
                    if (phoneNum.length() != 10 || !phoneNum.matches(regexStr)) {
                        etPhoneStaff.setError("Enter the proper number");
                        etPhoneStaff.requestFocus();
                        return;
                    }



                    staffPb.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    final Query queryRef = staffRef.orderByChild("phone").equalTo(phoneNum);
                    Log.d("Staff", String.valueOf(queryRef));


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
                                    editor.putString("staffName", name1);
                                    editor.putString("staffPhone", phoneNum);
                                    editor.putString("staffKey", k);
                                    editor.apply();


                                    Intent intent = new Intent(getApplicationContext(), Staff2Activity.class);
                                    intent.putExtra("phoneNum", phoneNum);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    staffPb.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Toast.makeText(getApplicationContext(), "Phone number doesn't exists.", Toast.LENGTH_SHORT).show();
                                    etPhoneStaff.setText("");
                                    etPhoneStaff.requestFocus();

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

    public void onBackPressed() {
        super.onBackPressed();
        staffPb.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Intent i = new Intent(StaffActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void check_connection()
    {
        connection_detector = new Connection_Detector(this);
        connection_detector.execute();
    }
}
