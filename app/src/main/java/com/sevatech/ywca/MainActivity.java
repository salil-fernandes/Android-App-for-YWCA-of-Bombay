package com.sevatech.ywca;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.sevatech.ywca.navigation.FragmentBaseActivity;


public class MainActivity extends AppCompatActivity {

    RadioGroup rgStatus;
    Button btnNext;
    TextView tvText;
    SharedPreferences sp;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));

        }


        rgStatus = findViewById(R.id.rgStatus);
        btnNext = findViewById(R.id.btnNext);
        tvText = findViewById(R.id.tvText);

        sp = getSharedPreferences("f1", MODE_PRIVATE);

        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);


        final String status = sp.getString("status", "");

        if (status.length() == 0) {
            btnNext.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("WrongConstant")
                @Override
                public void onClick(View v) {

                    String firebaseAuth = FirebaseAuth.getInstance().getUid();
                    System.out.println("Uid" + firebaseAuth);

                    int id = rgStatus.getCheckedRadioButtonId();
                    RadioButton rb = findViewById(id);
                    String status1 = rb.getText().toString();

                    if (status1.equals("Member")) {

                        Intent i = new Intent(MainActivity.this, MemberActivity.class);
                        startActivity(i);
                        finish();
                    } else if (status1.equals("Staff")) {
                        Intent s = new Intent(MainActivity.this, StaffActivity.class);
                        startActivity(s);
                        finish();

                    } else if (status1.equals("Admin")) {
                        Intent q = new Intent(MainActivity.this, AdminActivity.class);
                        startActivity(q);
                        finish();

                    } else {
                        Intent z = new Intent(MainActivity.this, NMPhoneActivity.class);
                        startActivity(z);
                        finish();
                    }

                }
            });

        } else {

            Intent i = new Intent(MainActivity.this, FragmentBaseActivity.class);
            startActivity(i);
            finish();
/*

            if(status.equals("Member"))
            {
            Intent i = new Intent(MainActivity.this, MEMBERMAINActivity.class);
            startActivity(i);
            finish();
             }
             else if(status.equals("Staff"))
             {
            Intent i = new Intent(MainActivity.this, MEMBERMAINActivity.class);
            startActivity(i);
            finish();
             }
             else if(status.equals("Admin"))
             {
            Intent i = new Intent(MainActivity.this, AdminMainActivity.class);
            startActivity(i);
            finish();
             }
             else
            {
            Intent i = new Intent(MainActivity.this, NONMEMBERMAINActivity.class);
            startActivity(i);
            finish();

        }
*/
        }

    }

    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to close this application?");
        builder.setCancelable(false);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //finish();
                finishAffinity();

            }
        });


        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.setTitle("Exit?");
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#417eca"));
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#417eca"));

    }

}
