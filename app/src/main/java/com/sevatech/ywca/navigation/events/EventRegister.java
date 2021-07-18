package com.sevatech.ywca.navigation.events;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.sevatech.ywca.Connection_Detector;
import com.sevatech.ywca.InternetConnection;
import com.sevatech.ywca.R;
import com.sevatech.ywca.helper.UserData;

import java.util.Map;


public class EventRegister extends AppCompatActivity {

    SharedPreferences sp;
    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabaseRef;
    DatabaseReference mBackupRef;
    DatabaseReference databaseReference;
    DatabaseReference myRef;
    DatabaseReference Ref;
    Connection_Detector connection_detector;
    SharedPreferences mPrefs;

    EditText userName, userEmail, userContact;
    Button register;

    String name, contact, email, currentID, eventType;
    String name1, phone1, email1;
    String spPhone;
    String status;

    @SuppressLint("StaticFieldLeak")
    public static Activity eventRegisterActivity = null;
    private boolean eventExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_register);

        eventRegisterActivity = this;
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("registration");
        mBackupRef = FirebaseDatabase.getInstance().getReference("eventsBackup");
        userName = findViewById(R.id.user_name);
        userContact = findViewById(R.id.user_contact);
        userEmail = findViewById(R.id.user_email);
        register = findViewById(R.id.register);

        sp = getSharedPreferences("f1", MODE_PRIVATE);

        status = sp.getString("status", "");

        currentID = firebaseAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        if (status.equals("Member")) {
            String key = sp.getString("memKey", "");
            myRef = databaseReference.child("Members");
            Ref = databaseReference.child("Members").child(key);
            spPhone = sp.getString("memberPhone", "");

        } else if (status.equals("Staff")) {
            String key = sp.getString("staffKey", "");
            myRef = databaseReference.child("staff");
            Ref = databaseReference.child("staff").child(key);
            spPhone = sp.getString("staffPhone", "");
        } else {
            String key = sp.getString("nmKey", "");
            myRef = databaseReference.child("NonMember");
            Ref = databaseReference.child("NonMember").child(key);
            spPhone = sp.getString("phone", "");
        }

        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                Log.d("Register", String.valueOf(value));
                assert value != null;
                name1 = String.valueOf(value.get("name"));
                phone1 = String.valueOf(value.get("phone"));
                email1 = String.valueOf(value.get("email"));


                userName.setText(name1);
                userContact.setText(phone1);
                userEmail.setText(email1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        register.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                name = userName.getText().toString();
                email = userEmail.getText().toString();
                contact = userContact.getText().toString();

                if (name.equals("")) {
                    userName.requestFocus();
                    userName.setError("Enter valid Name");
                    return;
                }

                if (!email.equals("") && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    userEmail.requestFocus();
                    userEmail.setError("Enter valid Email");
                    return;
                }

                if (contact.equals("") || contact.length() != 10) {
                    userContact.requestFocus();
                    userContact.setError("Enter valid Contact");
                    return;
                }

                check_connection();
                checkEventID(new FirebaseCallback() {
                    @Override
                    public void onCallback(boolean check) {
                        if (check) {
                            if (status.equals("Member") || status.equals("Staff")) {
                                mPrefs = getSharedPreferences("Flag", MODE_APPEND);
                                int X = mPrefs.getInt("Flag", 0);
                                if (X == 1) {
                                    Register();
                                }
                            } else {
                                mPrefs = getSharedPreferences("Flag", MODE_APPEND);
                                int X = mPrefs.getInt("Flag", 0);
                                if (X == 1) {
                                    RegisterNonMember();
                                }
                            }
                        }
                    }
                });

            }

        });
    }

    private void checkEventID(final FirebaseCallback firebaseCallback) {
        String selectedKey = getIntent().getStringExtra("eventKey");
        DatabaseReference eventRef = mDatabaseRef;
        Query q = eventRef.orderByKey().equalTo(selectedKey);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    firebaseCallback.onCallback(true);
                } else {
                    firebaseCallback.onCallback(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private interface FirebaseCallback {
        void onCallback(boolean check);
    }


    private void Register() {

//        InternetConnection intconn = new InternetConnection(EventRegister.this);
//        if (!intconn.isNetworkAvailable()) {
//            intconn.Dialog(EventRegister.this);
//        } else {

        final String selectedKey = getIntent().getStringExtra("eventKey");
        final DatabaseReference eventRef = mDatabaseRef.child(selectedKey);

        Query q = eventRef.orderByChild("userID").equalTo(currentID);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
//                    System.out.println(currentID);

                    Query q2 = eventRef.orderByChild("userContact").equalTo(contact);
                    q2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //checking if phone number has already registered
                            if (snapshot.exists()) {
                                Toast.makeText(EventRegister.this, "Already registered Member", Toast.LENGTH_SHORT).show();
                            }
                            //if not registered checking if entered number equals user default number
                            else if (contact.equals(spPhone)) {
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    String uploadType = d.child("userType").getValue(String.class);
                                    String key = d.getKey();
                                    UserData data = new UserData(currentID, name, email, contact, "2");
                                    if (uploadType.equals("1")) {
//                                        Toast.makeText(EventRegister.this, "Registering for self.", Toast.LENGTH_SHORT).show();
                                        eventRef.child(key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(EventRegister.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else
                                                    task.getException().printStackTrace();
                                            }
                                        });
                                        break;
                                    } else {
//                                        Toast.makeText(EventRegister.this, "Registering for self.", Toast.LENGTH_SHORT).show();
                                        eventRef.push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(EventRegister.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else
                                                    task.getException().printStackTrace();
                                            }
                                        });
                                    }
                                    break;
                                }
                            } else {
                                //Registering for some other number
                                //OTP

                                final DatabaseReference mEventMemRef = FirebaseDatabase.getInstance()
                                        .getReference("events").child("member");

                                mEventMemRef.child(selectedKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            Map<String, Object> typeMap = (Map<String, Object>) snapshot.getValue();
                                            eventType = String.valueOf(typeMap.get("eventType"));
                                            Toast.makeText(EventRegister.this, "Please wait!", Toast.LENGTH_SHORT).show();

                                            if (eventType.equals("Everyone")) {
                                                for (DataSnapshot d : dataSnapshot.getChildren()) {
//                                                    Toast.makeText(EventRegister.this, "Registering for else", Toast.LENGTH_SHORT).show();

                                                    String type = d.child("userType").getValue(String.class);
                                                    String key = d.getKey();

//                                    finish();

                                                    Intent i = new Intent(EventRegister.this, EventOTPActivity.class);
                                                    i.putExtra("phoneNum", contact);
                                                    i.putExtra("eventKey", selectedKey);
                                                    i.putExtra("type", type);
                                                    i.putExtra("key", key);
                                                    i.putExtra("name", name);
                                                    i.putExtra("email", email);
                                                    i.putExtra("currentID", currentID);

                                                    startActivity(i);

                                                }
                                            }
                                            //If event type is Members Only, checking if entered number is member
                                            else {
                                                Query query = myRef.orderByChild("phone").equalTo(contact);
                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            for (DataSnapshot d : dataSnapshot.getChildren()) {

                                                                String type = d.child("userType").getValue(String.class);
                                                                String key = d.getKey();

                                                                Intent i = new Intent(EventRegister.this, EventOTPActivity.class);
                                                                i.putExtra("phoneNum", contact);
                                                                i.putExtra("eventKey", selectedKey);
                                                                i.putExtra("type", type);
                                                                i.putExtra("key", key);
                                                                i.putExtra("name", name);
                                                                i.putExtra("email", email);
                                                                i.putExtra("currentID", currentID);

                                                                startActivity(i);
                                                            }
                                                        } else {
                                                            Toast.makeText(EventRegister.this, "Entered mobile number is not a member", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void RegisterNonMember() {
        final String selectedKey = getIntent().getStringExtra("eventKey");
        final DatabaseReference eventRef = mDatabaseRef.child(selectedKey);

        Query q = eventRef.orderByChild("userID").equalTo(currentID);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
//                    System.out.println(currentID);

                    Query q2 = eventRef.orderByChild("userContact").equalTo(contact);
                    q2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (contact.equals(spPhone)) {
                                if (snapshot.exists()) {
                                    Toast.makeText(EventRegister.this, "Already Registered Non member!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    String uploadType = d.child("userType").getValue(String.class);
                                    String key = d.getKey();
                                    UserData data = new UserData(currentID, name, email, contact, "2");
                                    assert uploadType != null;
                                    if (uploadType.equals("1")) {
//                                        Toast.makeText(EventRegister.this, "Registering for self. UserType 1", Toast.LENGTH_SHORT).show();
//                                        System.out.println(key);
                                        assert key != null;
                                        eventRef.child(key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(EventRegister.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else
                                                    task.getException().printStackTrace();
                                            }
                                        });
                                        break;
                                    }
//                                    else {
//                                        Toast.makeText(EventRegister.this, "Already Registered for self. UserType 2", Toast.LENGTH_SHORT).show();
//                                    }
                                    break;
                                }
                            } else {
                                Toast.makeText(EventRegister.this, "NonMembers cannot register for someone else", Toast.LENGTH_SHORT).show();
                                userContact.setError("Cannot register for other number");
                                userContact.requestFocus();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void check_connection() {
        connection_detector = new Connection_Detector(this);
        connection_detector.execute();
    }
}

