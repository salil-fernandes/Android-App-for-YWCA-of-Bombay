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



public class MemberActivity extends AppCompatActivity {

    TextView tvMember;
    EditText etPhoneMem;
    Button btnAage;
    ProgressBar memberPb;
    DatabaseReference databaseReference;
    DatabaseReference memberRef;
    String phoneNum = null;
    SharedPreferences sp;
    SharedPreferences mPrefs;
    Connection_Detector connection_detector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }

        tvMember = findViewById(R.id.tvMember);
        etPhoneMem=findViewById(R.id.etPhoneMem);
        btnAage = findViewById(R.id.btnAage);
        memberPb=findViewById(R.id.memberPb);

        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

        sp=getSharedPreferences("f1", MODE_PRIVATE);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        memberRef = databaseReference.child("Members");


        Log.d("CHECK1", "1");


        btnAage.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {

                check_connection();
                mPrefs=getSharedPreferences("Flag", MODE_APPEND);
                int X = mPrefs.getInt("Flag",0);
                if (X==1) {
                    final String regexStr = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";
                    phoneNum = etPhoneMem.getText().toString();
                    if (phoneNum.length() != 10 || !phoneNum.matches(regexStr)) {
                        etPhoneMem.setError("Enter the proper number");
                        etPhoneMem.requestFocus();
                        return;
                    }


                    memberPb.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    final Query queryRef = memberRef.orderByChild("phone").equalTo(phoneNum);
                    Log.d("Error", String.valueOf(queryRef));

                    if (phoneNum != null) {

                        Log.d("CHECK2", "2");

                        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {

                                    String name1 = null;
                                    String k = null;

                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        k = ds.getKey();
                                        name1 = ds.child("name").getValue(String.class);
                                    }


                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("memberName", name1);
                                    editor.putString("memberPhone", phoneNum);
                                    editor.putString("memKey", k);
                                    editor.apply();

                                    Intent intent = new Intent(getApplicationContext(), Member2Activity.class);
                                    intent.putExtra("phoneNum", phoneNum);
                                    startActivity(intent);
                                    finish();

                                }
                                else
                                {
                                    memberPb.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Toast.makeText(getApplicationContext(), "Phone number doesn't exist.", Toast.LENGTH_SHORT).show();
                                    etPhoneMem.setText("");
                                    etPhoneMem.requestFocus();

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
        memberPb.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Intent i = new Intent(MemberActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void check_connection()
    {
        connection_detector = new Connection_Detector(this);
        connection_detector.execute();
    }
}
