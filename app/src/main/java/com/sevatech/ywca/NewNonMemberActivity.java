package com.sevatech.ywca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sevatech.ywca.navigation.FragmentBaseActivity;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NewNonMemberActivity extends AppCompatActivity {

    TextView tvNewVerify, tvCountDown1;
    EditText etNewNmOTP;
    Button btnVerifyNewNMOTP;
    ProgressBar NewNmOtpPb;
    SharedPreferences sp;
    private String verificationId;
    String OTP1 = null;

    private static final long START_TIME = 120000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME;

    SharedPreferences mPrefs;
    Connection_Detector connection_detector;

    // Firebase //
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    FirebaseAuth mAuth;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_non_member);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }

        tvNewVerify=findViewById(R.id.tvNewVerify);
        etNewNmOTP=findViewById(R.id.etNewNmOTP);
        btnVerifyNewNMOTP=findViewById(R.id.btnVerifyNewNMOTP);
        NewNmOtpPb = findViewById(R.id.NewNmOtpPb);
        tvCountDown1=findViewById(R.id.tvCountDown1);


        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef=firebaseDatabase.getReference("NonMember");

        startTimer();
        updateCountDownText();



        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

        sp = getSharedPreferences("f1", MODE_PRIVATE);

        String phoneNumber = sp.getString("phone", "");

        phoneNumber = "+91"+phoneNumber;

        sendVerificationCode(phoneNumber);


        btnVerifyNewNMOTP.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                check_connection();
                mPrefs=getSharedPreferences("Flag", MODE_APPEND);
                int X = mPrefs.getInt("Flag",0);
                if (X==1) {
                    OTP1 = etNewNmOTP.getText().toString();
                    if (OTP1.length() != 6) {
                        etNewNmOTP.setError("Enter the proper number");
                        etNewNmOTP.requestFocus();
                        return;
                    }

                    NewNmOtpPb.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    verifyCode(OTP1);
                }
            }
        });



    }

    private void verifyCode(String code)
    {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("status", "NonMember");
                            editor.apply();


                            String name = sp.getString("name", "");
                            String phone = sp.getString("phone", "");


                            String dob = getIntent().getExtras().getString("dob");
                            String email = getIntent().getExtras().getString("email");
                            String gender = getIntent().getExtras().getString("gender");
                            String institute = getIntent().getExtras().getString("institute");
                            String interest = getIntent().getExtras().getString("interest");
                            String location = getIntent().getExtras().getString("location");
                            String profession = getIntent().getExtras().getString("profession");


                            NonMember n = new NonMember(name, phone, dob, email, gender, institute , location, interest, profession);
                            myRef.push().setValue(n);

                            Query queryRef = myRef.orderByChild("phone").equalTo(phone);
                            queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists())
                                    {
                                        String k = null;

                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            k = ds.getKey();

                                        }
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString("nmKey", k);
                                        editor.apply();
                                    }
//                                    else
//                                    {
//                                        Toast.makeText(NewNonMemberActivity.this, "Key didn't get saved!!", Toast.LENGTH_SHORT).show();
//                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            NewNmOtpPb.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(NewNonMemberActivity.this, "SIGN UP SUCCESSFUL!!", Toast.LENGTH_SHORT).show();

                            etNewNmOTP.setText("");
                            etNewNmOTP.requestFocus();

                            pauseTimer();
                            Intent intent = new Intent(NewNonMemberActivity.this, FragmentBaseActivity.class);
                            startActivity(intent);
                            finishAffinity();

                        }
                        else {
                            // Toast.makeText(Member2Activity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            NewNmOtpPb.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            etNewNmOTP.setText("");
                            etNewNmOTP.requestFocus();

                            Toast.makeText(NewNonMemberActivity.this,"Wrong OTP entered.....Enter your own phone number!!",Toast.LENGTH_LONG).show();
                            // pauseTimer();

                            //Intent i = new Intent(getApplicationContext(), NMActivity.class);
                            //startActivity(i);
                            //finish();
                            onBackPressed();
                        }
                    }
                });
    }



    private void sendVerificationCode(String number) {

        NewNmOtpPb.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        Toast.makeText(this, "The otp might take a few seconds to come", Toast.LENGTH_LONG).show();



        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                50,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s ;
            NewNmOtpPb.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code!=null)
            {
                NewNmOtpPb.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(NewNonMemberActivity.this, "Verifying code automatically!", Toast.LENGTH_LONG).show();

                etNewNmOTP.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            NewNmOtpPb.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            etNewNmOTP.setText("");
            etNewNmOTP.requestFocus();


            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(NewNonMemberActivity.this, "Invalid Request! ", Toast.LENGTH_LONG).show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(NewNonMemberActivity.this, "This number has been blocked due to too many otp requests!! Try again tomorrow!!", Toast.LENGTH_LONG).show();

            }
            else
            {
                Toast.makeText(NewNonMemberActivity.this,"Verification Failed.....Try again",Toast.LENGTH_SHORT).show();
            }

            // pauseTimer();
            // Intent i = new Intent(NewNonMemberActivity.this, NMActivity.class);
            //startActivity(i);
            //finish();

            onBackPressed();
        }


    };

    private void startTimer()
    {
        mCountDownTimer= new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis=millisUntilFinished;
                updateCountDownText();

            }

            @Override
            public void onFinish() {

                mTimerRunning=false;
                Toast.makeText(NewNonMemberActivity.this, "OTP expired.....Try again!", Toast.LENGTH_SHORT).show();
                onBackPressed();


            }
        }.start();

        mTimerRunning = true;
    }

    private void pauseTimer(){
        mCountDownTimer.cancel();
        mTimerRunning = false;
    }


    @SuppressLint("SetTextI18n")
    private void updateCountDownText()
    {
        int minutes = (int)(mTimeLeftInMillis / 1000)/ 60;
        int seconds = (int)(mTimeLeftInMillis / 1000)% 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvCountDown1.setText("OTP expires in: " + timeLeftFormatted);
    }


    public void onBackPressed() {
        super.onBackPressed();
        NewNmOtpPb.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pauseTimer();
        SharedPreferences.Editor editor = sp.edit();
        String status1="";
        editor.putString("phone", status1);
        editor.apply();

        //Intent i = new Intent(NewNonMemberActivity.this, NMActivity.class);
        //startActivity(i);
        //finish();

    }

    public void check_connection()
    {
        connection_detector = new Connection_Detector(this);
        connection_detector.execute();
    }


}
