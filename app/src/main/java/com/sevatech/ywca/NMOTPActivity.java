package com.sevatech.ywca;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import com.sevatech.ywca.helper.DetailsHelperClass;
import com.sevatech.ywca.navigation.FragmentBaseActivity;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NMOTPActivity extends AppCompatActivity {

    TextView tvVerify, tvCountDown2;
    EditText etNmOTP;
    Button btnVerifyNMOTP;
    SharedPreferences sp;
    ProgressBar NmOtpPb;
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
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    private DetailsHelperClass detailsHelperClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_n_m_o_t_p);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

        etNmOTP = findViewById(R.id.etNmOTP);
        btnVerifyNMOTP = findViewById(R.id.btnVerifyNMOTP);
        NmOtpPb = findViewById(R.id.NmOtpPb);
        tvCountDown2=findViewById(R.id.tvCountDown2);

        startTimer();
        updateCountDownText();

        sp = getSharedPreferences("f1", MODE_PRIVATE);


        String phoneNumber = getIntent().getExtras().getString("phoneNum");


        phoneNumber = "+91"+phoneNumber;

        //detailsHelperClass = new DetailsHelperClass(name, DOB, email, phoneNumber, gender);

        sendVerificationCode(phoneNumber);

        btnVerifyNMOTP.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                check_connection();
                mPrefs=getSharedPreferences("Flag", MODE_APPEND);
                int X = mPrefs.getInt("Flag",0);
                if (X==1) {
                    OTP1 = etNmOTP.getText().toString();
                    if (OTP1.length() != 6) {
                        etNmOTP.setError("Enter the proper number");
                        etNmOTP.requestFocus();
                        return;
                    }

                    NmOtpPb.setVisibility(View.VISIBLE);
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
                            editor.commit();

                            NmOtpPb.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(NMOTPActivity.this, "SIGN IN SUCCESSFUL!!", Toast.LENGTH_SHORT).show();
                            etNmOTP.setText("");
                            etNmOTP.requestFocus();


                            pauseTimer();
                            Intent intent = new Intent(NMOTPActivity.this, FragmentBaseActivity.class);

                            startActivity(intent);
                            finish();
                        }
                        else {
                            // Toast.makeText(Member2Activity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            NmOtpPb.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            etNmOTP.setText("");
                            etNmOTP.requestFocus();

                            Toast.makeText(NMOTPActivity.this,"Wrong OTP entered.....Try again",Toast.LENGTH_SHORT).show();
                            pauseTimer();
                            Intent i = new Intent(NMOTPActivity.this, NMPhoneActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });
    }



    private void sendVerificationCode(String number) {

        NmOtpPb.setVisibility(View.VISIBLE);
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
            NmOtpPb.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code!=null)
            {
                NmOtpPb.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(NMOTPActivity.this, "Verifying code automatically!", Toast.LENGTH_LONG).show();

                etNmOTP.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            NmOtpPb.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            etNmOTP.setText("");
            etNmOTP.requestFocus();


            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(NMOTPActivity.this, "Invalid Request! ", Toast.LENGTH_LONG).show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(NMOTPActivity.this, "This number has been blocked due to too many otp requests!! Try again tomorrow!!", Toast.LENGTH_LONG).show();

            }
            else
            {
                Toast.makeText(NMOTPActivity.this,"Verification Failed.....Try again",Toast.LENGTH_SHORT).show();
            }


            pauseTimer();
            Intent i = new Intent(NMOTPActivity.this, NMPhoneActivity.class);
            startActivity(i);
            finish();
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
                Toast.makeText(NMOTPActivity.this, "OTP expired.....Try again!", Toast.LENGTH_SHORT).show();
                // onBackPressed();
                Intent i = new Intent(NMOTPActivity.this, NMPhoneActivity.class);
                startActivity(i);
                finish();


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
        tvCountDown2.setText("OTP expires in: " + timeLeftFormatted);
    }


    public void onBackPressed() {
        super.onBackPressed();
        NmOtpPb.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pauseTimer();

        SharedPreferences.Editor editor = sp.edit();
        String status1="";
        editor.putString("name", status1);
        editor.putString("phone", status1);
        editor.putString("nmKey", status1);
        editor.commit();

        Intent i = new Intent(NMOTPActivity.this, NMPhoneActivity.class);
        startActivity(i);
        finish();

    }

    public void check_connection()
    {
        connection_detector = new Connection_Detector(this);
        connection_detector.execute();
    }
}
