package com.sevatech.ywca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etForgotEmailId;
    Button btnResetPassword;
    FirebaseAuth firebaseAuth;
    ProgressBar forgotPb;
    SharedPreferences mPrefs;
    Connection_Detector connection_detector;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }

        etForgotEmailId= findViewById(R.id.etForgotEmailId);
        btnResetPassword=findViewById(R.id.btnResetPassword);
        forgotPb = findViewById(R.id.forgotPb);
        firebaseAuth=FirebaseAuth.getInstance();

        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {

                check_connection();
                mPrefs=getSharedPreferences("Flag", MODE_APPEND);
                int X = mPrefs.getInt("Flag",0);
                if (X==1) {
                    String un = etForgotEmailId.getText().toString();

                    if (!Patterns.EMAIL_ADDRESS.matcher(un).matches() || etForgotEmailId.length() == 0) {
                        etForgotEmailId.setError("Invalid email address");
                        etForgotEmailId.setText("");
                        etForgotEmailId.requestFocus();
                        return;
                    }

                    forgotPb.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    firebaseAuth.sendPasswordResetEmail(un).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                forgotPb.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                etForgotEmailId.setText("");
                                etForgotEmailId.requestFocus();

                                Toast.makeText(ForgotPasswordActivity.this, "Check Email", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(ForgotPasswordActivity.this, AdminActivity.class);
                                startActivity(i);
                            } else {
                                forgotPb.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                //Toast.makeText(ForgotPasswordActivity.this, "Failure " + task.getException(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(ForgotPasswordActivity.this, "Enter the registered email id!!", Toast.LENGTH_SHORT).show();
                                etForgotEmailId.setText("");
                                etForgotEmailId.requestFocus();
                            }
                        }
                    });
                }

            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        forgotPb.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Intent i = new Intent(ForgotPasswordActivity.this, AdminActivity.class);
        startActivity(i);
        finish();
    }

    public void check_connection()
    {
        connection_detector = new Connection_Detector(this);
        connection_detector.execute();
    }
}