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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ProfileOtpActivity extends AppCompatActivity {
    TextView tvProfileOtp,  tvCountDownProfileOtp;
    EditText etProfileOtp;
    Button btnProfileOtp;
    ProgressBar ProfileOtpPb;
    SharedPreferences sp;
    private String verificationId;
    String OTP1 = null;
    String phoneNum=null;
    String status=null;
    SharedPreferences mPrefs;
    Connection_Detector connection_detector;


    private static final long START_TIME = 120000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference NMRef;
    DatabaseReference memberRef;
    DatabaseReference approvalRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_otp);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        sp = getSharedPreferences("f1", MODE_PRIVATE);

        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

        tvCountDownProfileOtp = findViewById(R.id.tvCountDownProfileOtp);
        tvProfileOtp=findViewById(R.id.tvProfileOtp);
        etProfileOtp=findViewById(R.id.etProfileOtp);
        btnProfileOtp=findViewById(R.id.btnProfileOtp);
        ProfileOtpPb=findViewById(R.id.ProfileOtpPb);

        startTimer();
        updateCountDownText();

        status = sp.getString("status", "");
        String key = sp.getString("nmKey", "");
        String k = sp.getString("memKey", "");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        NMRef = databaseReference.child("NonMember").child(key);
        memberRef = databaseReference.child("Members").child(k);
        approvalRef = databaseReference.child("approval").child(k);

        String phoneNumber = getIntent().getExtras().getString("phoneNum");
        phoneNum = phoneNumber;

        phoneNumber = "+91"+phoneNumber;

        sendVerificationCode(phoneNumber);

        btnProfileOtp.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                check_connection();
                mPrefs=getSharedPreferences("Flag", MODE_APPEND);
                int X = mPrefs.getInt("Flag",0);
                if (X==1) {
                    OTP1 = etProfileOtp.getText().toString();
                    if (OTP1.length() != 6) {
                        etProfileOtp.setError("Enter the proper number");
                        etProfileOtp.requestFocus();
                        return;
                    }

                    ProfileOtpPb.setVisibility(View.VISIBLE);
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
                            if(status.equals("NonMember"))
                            {
                                NMRef.child("phone").setValue(phoneNum);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("phone", phoneNum);
                                editor.apply();

                                ProfileOtpPb.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(ProfileOtpActivity.this, "Phone no. updated!", Toast.LENGTH_SHORT).show();
                                etProfileOtp.setText("");
                                etProfileOtp.requestFocus();

                                pauseTimer();
                                Intent intent = new Intent(ProfileOtpActivity.this, PreNMProfileActivity.class);
                                startActivity(intent);
                                finish();


                            }
                            else
                            {
//                                memberRef.child("phone").setValue(phoneNum);
                                approvalRef.child("phone").setValue(phoneNum);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("memberPhone", phoneNum);
                                editor.apply();

                                ProfileOtpPb.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(ProfileOtpActivity.this, "Phone no. updated!", Toast.LENGTH_SHORT).show();
                                etProfileOtp.setText("");
                                etProfileOtp.requestFocus();

                                pauseTimer();
//                                Intent intent = new Intent(ProfileOtpActivity.this, PreProfileActivity.class);
//                                startActivity(intent);
                                ProfileActivity.profileActivity.finish();
                                finish();

                            }



                        }
                        else {

                            ProfileOtpPb.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            etProfileOtp.setText("");
                            etProfileOtp.requestFocus();

                            Toast.makeText(ProfileOtpActivity.this,"Wrong OTP entered.....Try again",Toast.LENGTH_SHORT).show();
                            pauseTimer();

                            if(status.equals("NonMember"))
                            {
                                Intent i = new Intent(ProfileOtpActivity.this, NMProfileActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else
                            {
//                                Intent i = new Intent(ProfileOtpActivity.this, ProfileActivity.class);
//                                startActivity(i);
                                finish();
                            }

                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {

        ProfileOtpPb.setVisibility(View.VISIBLE);
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
            ProfileOtpPb.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code!=null)
            {
                ProfileOtpPb.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(ProfileOtpActivity.this, "Verifying code automatically!", Toast.LENGTH_LONG).show();

                etProfileOtp.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            ProfileOtpPb.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            etProfileOtp.setText("");
            etProfileOtp.requestFocus();


            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(ProfileOtpActivity.this, "Invalid Request! ", Toast.LENGTH_LONG).show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(ProfileOtpActivity.this, "This number has been blocked due to too many otp requests!! Try again tomorrow!!", Toast.LENGTH_LONG).show();

            }
            else
            {
                Toast.makeText(ProfileOtpActivity.this,"Verification Failed.....Try again",Toast.LENGTH_SHORT).show();
            }

            pauseTimer();

            if(status.equals("NonMember")){
                Intent i = new Intent(ProfileOtpActivity.this, NMProfileActivity.class);
                startActivity(i);
                finish();
            }
            else
            {
//                Intent i = new Intent(ProfileOtpActivity.this, ProfileActivity.class);
//                startActivity(i);
                finish();
            }

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
                Toast.makeText(ProfileOtpActivity.this, "OTP expired.....Try again!", Toast.LENGTH_SHORT).show();
                if(status.equals("NonMember"))
                {
                    Intent i = new Intent(ProfileOtpActivity.this, NMProfileActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
//                    Intent i = new Intent(ProfileOtpActivity.this, ProfileActivity.class);
//                    startActivity(i);
                    finish();
                }



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
        tvCountDownProfileOtp.setText("OTP expires in: " + timeLeftFormatted);
    }

    public void onBackPressed() {
        super.onBackPressed();
        ProfileOtpPb.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pauseTimer();

        if(status.equals("NonMember"))
        {
            Intent i = new Intent(ProfileOtpActivity.this, NMProfileActivity.class);
            startActivity(i);
            finish();
        }
        else
        {
//            Intent i = new Intent(ProfileOtpActivity.this, ProfileActivity.class);
//            startActivity(i);
            finish();
        }


    }

    public void check_connection()
    {
        connection_detector = new Connection_Detector(this);
        connection_detector.execute();
    }



}
