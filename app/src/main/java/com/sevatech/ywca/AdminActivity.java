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
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sevatech.ywca.navigation.FragmentBaseActivity;

public class AdminActivity extends AppCompatActivity {

    TextView tvAdmin;
    EditText etEmailAdmin, etPassAdmin;
    Button btnAdmin, btnForgotPassword;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    ProgressBar adminPb;
    SharedPreferences sp;
    SharedPreferences mPrefs;
    Connection_Detector connection_detector;


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }

        tvAdmin=findViewById(R.id.tvAdmin);
        etEmailAdmin=findViewById(R.id.etEmailAdmin);
        etPassAdmin =findViewById(R.id.etPassAdmin);
        btnAdmin=findViewById(R.id.btnAdmin);
        adminPb = findViewById(R.id.adminPb);
        firebaseAuth=FirebaseAuth.getInstance();

        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

        btnForgotPassword=findViewById(R.id.btnForgotPassword);

        user= firebaseAuth.getCurrentUser();


        sp=getSharedPreferences("f1", MODE_PRIVATE);

        if (user !=null)
        {
            Intent a =new Intent(AdminActivity.this, FragmentBaseActivity.class);
            startActivity(a);
            finish();
        }




        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {

                check_connection();
                mPrefs=getSharedPreferences("Flag", MODE_APPEND);
                int X = mPrefs.getInt("Flag",0);
                if (X==1) {
                    String un = etEmailAdmin.getText().toString();
                    if (!Patterns.EMAIL_ADDRESS.matcher(un).matches()) {
                        etEmailAdmin.setError("Invalid email address");
                        etEmailAdmin.setText("");
                        etEmailAdmin.requestFocus();
                        return;
                    }
                    String pw = etPassAdmin.getText().toString();

                    if (etPassAdmin.length() == 0) {
                        etPassAdmin.setError("Enter the password");
                        etPassAdmin.requestFocus();
                        return;
                    }

                    adminPb.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                    firebaseAuth.signInWithEmailAndPassword(un, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("status", "Admin");
                                editor.apply();

                                adminPb.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                etPassAdmin.setText("");
                                etEmailAdmin.setText("");
                                etEmailAdmin.requestFocus();

                                Intent i = new Intent(AdminActivity.this, FragmentBaseActivity.class);
                                startActivity(i);
                                finish();
                            } else {

                                adminPb.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                //Toast.makeText(AdminActivity.this, "Failure "+task.getException(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(AdminActivity.this, "Enter the correct email id and password!!", Toast.LENGTH_SHORT).show();
                                etPassAdmin.setText("");
                                etEmailAdmin.setText("");
                                etEmailAdmin.requestFocus();
                            }

                        }
                    });
                }
            }
        });


        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });




    }

    public void onBackPressed() {
        super.onBackPressed();
        adminPb.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Intent i = new Intent(AdminActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void check_connection()
    {
        connection_detector = new Connection_Detector(this);
        connection_detector.execute();
    }
}
