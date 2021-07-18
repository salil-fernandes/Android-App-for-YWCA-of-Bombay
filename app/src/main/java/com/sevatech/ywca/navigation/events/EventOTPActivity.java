package com.sevatech.ywca.navigation.events;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
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
import com.sevatech.ywca.InternetConnection;
import com.sevatech.ywca.NMProfileActivity;
import com.sevatech.ywca.PreNMProfileActivity;
import com.sevatech.ywca.PreProfileActivity;
import com.sevatech.ywca.ProfileActivity;
import com.sevatech.ywca.ProfileOtpActivity;
import com.sevatech.ywca.R;
import com.sevatech.ywca.helper.UserData;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EventOTPActivity extends AppCompatActivity {

    EditText etEventOTP;
    Button btnVerifyEventOTP;
    TextView tvEventCountDown;
    ProgressBar eventOtpPb;

    SharedPreferences sp;
    private String verificationId;
    String OTP1 = null;
    String phoneNum = null;

    private static final long START_TIME = 120000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabaseRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_o_t_p);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        sp = getSharedPreferences("f1", MODE_PRIVATE);

        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

        tvEventCountDown = findViewById(R.id.tvEventCountDown);
        etEventOTP = findViewById(R.id.etEventOTP);
        btnVerifyEventOTP = findViewById(R.id.btnVerifyEventOTP);
        eventOtpPb = findViewById(R.id.eventOtpPb);

        startTimer();
        updateCountDownText();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("registration");

        String phoneNumber = getIntent().getExtras().getString("phoneNum");
        phoneNum = phoneNumber;

        phoneNumber = "+91" + phoneNumber;

        sendVerificationCode(phoneNumber);


        btnVerifyEventOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InternetConnection intconn = new InternetConnection(EventOTPActivity.this);
                if (!intconn.isNetworkAvailable()) {
                    intconn.Dialog(EventOTPActivity.this);
                } else {
                    OTP1 = etEventOTP.getText().toString();
                    if (OTP1.length() != 6) {
                        etEventOTP.setError("Enter the proper number");
                        etEventOTP.requestFocus();
                        return;
                    }

                    eventOtpPb.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    verifyCode(OTP1);
                }
            }
        });


    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //check for duplicate email
                            final String selectedKey = getIntent().getExtras().getString("eventKey");
                            final String type = getIntent().getExtras().getString("type");
                            final String key = getIntent().getExtras().getString("key");
                            final String name = getIntent().getExtras().getString("name");
                            final String email = getIntent().getExtras().getString("email");
                            final String currentID = getIntent().getExtras().getString("currentID");

                            final DatabaseReference eventRef = mDatabaseRef.child(selectedKey);

                            UserData data = new UserData(currentID, name, email, phoneNum, "2");

                            if (type.equals("1")) {
                                assert key != null;
                                eventRef.child(key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            eventOtpPb.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Toast.makeText(EventOTPActivity.this, "Successfully Registered!!", Toast.LENGTH_SHORT).show();
                                            etEventOTP.setText("");

                                            pauseTimer();
                                            finish();
                                        } else
                                            task.getException().printStackTrace();
                                    }
                                });

                            } else if (type.equals("2")) {
                                eventRef.push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            eventOtpPb.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Toast.makeText(EventOTPActivity.this, "Successfully Registered!!", Toast.LENGTH_SHORT).show();
                                            etEventOTP.setText("");

                                            pauseTimer();

                                            EventRegister.eventRegisterActivity.finish();
                                            finish();

                                        } else
                                            task.getException().printStackTrace();
                                    }
                                });

                            }

                        } else {

                            eventOtpPb.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            etEventOTP.setText("");
                            etEventOTP.requestFocus();

                            Toast.makeText(EventOTPActivity.this, "Wrong OTP entered.....Try again", Toast.LENGTH_SHORT).show();
                            pauseTimer();

                            //Intent i = new Intent(EventOTPActivity.this, EventRegister.class);
                            //startActivity(i);
                            finish();


                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {

        eventOtpPb.setVisibility(View.VISIBLE);
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
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            eventOtpPb.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                eventOtpPb.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(EventOTPActivity.this, "Verifying code automatically!", Toast.LENGTH_LONG).show();

                etEventOTP.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            eventOtpPb.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            etEventOTP.setText("");
            etEventOTP.requestFocus();


            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(EventOTPActivity.this, "Invalid Request! ", Toast.LENGTH_LONG).show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(EventOTPActivity.this, "This number has been blocked due to too many otp requests!! Try again tomorrow!!", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(EventOTPActivity.this, "Verification Failed.....Try again", Toast.LENGTH_SHORT).show();
            }

            pauseTimer();

            //Intent i = new Intent(EventOTPActivity.this, EventRegister.class);
            //startActivity(i);
            finish();


        }
    };

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();

            }

            @Override
            public void onFinish() {

                mTimerRunning = false;
                Toast.makeText(EventOTPActivity.this, "OTP expired.....Try again!", Toast.LENGTH_SHORT).show();

                //Intent i = new Intent(EventOTPActivity.this, EventRegister.class);
                //startActivity(i);
                finish();

            }
        }.start();

        mTimerRunning = true;
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
    }

    @SuppressLint("SetTextI18n")
    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvEventCountDown.setText("OTP expires in: " + timeLeftFormatted);
    }


    public void onBackPressed() {
        super.onBackPressed();
        eventOtpPb.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pauseTimer();


//        Intent i = new Intent(EventOTPActivity.this, EventRegister.class);
//        startActivity(i);
        DetailedEventFragment detailedEventFragment = new DetailedEventFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,detailedEventFragment);
        finish();
    }

}
