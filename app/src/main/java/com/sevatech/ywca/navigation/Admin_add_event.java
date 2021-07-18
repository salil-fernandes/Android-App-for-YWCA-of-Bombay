package com.sevatech.ywca.navigation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.sevatech.ywca.Connection_Detector;
import com.sevatech.ywca.MainActivity;
import com.sevatech.ywca.R;
import com.sevatech.ywca.helper.EventsData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Admin_add_event extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView mTextViewShowUploads;
    private EditText mEditTextTitle, mEditTextDescription, mEditTextVenue, mEditTextTime, mEditTextAmount, mEditTextDate, mEditTextDeadline;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private RadioGroup mEventTypeGroup;
    private RadioButton mEventTypeButton;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseRefBackup;
    private DatabaseReference mRegistrationRef;
    Connection_Detector connection_detector;
    SharedPreferences mPrefs;
    Calendar calendar = Calendar.getInstance();
    int hour, min;

    private String title, description, imageUrl, venue, amount, time, eventDate, eventDeadline, eventType;

    private StorageTask mUploadTask;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_event);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }


        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

        Button mButtonChooseImage = findViewById(R.id.btnchoose);
        Button mButtonUpload = findViewById(R.id.btnupload);
        mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        mEditTextTitle = findViewById(R.id.title);
        mEditTextDescription = findViewById(R.id.description);
        mEditTextVenue = findViewById(R.id.venue);
        mEditTextTime = findViewById(R.id.time);
        mEditTextAmount = findViewById(R.id.amount);
        mEditTextDate = findViewById(R.id.date);
        mEditTextDeadline = findViewById(R.id.deadline);
        mImageView = findViewById(R.id.imgview);
        mEventTypeGroup = findViewById(R.id.event_type);
        mProgressBar = findViewById(R.id.progress_bar);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                updateLabelDate();
            }
        };

        final DatePickerDialog.OnDateSetListener deadline = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                updateLabelDeadline();
            }
        };

        mEditTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(Admin_add_event.this, R.style.DialogTheme, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mEditTextDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(Admin_add_event.this,R.style.DialogTheme, deadline, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        mEditTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        Admin_add_event.this,
                        R.style.DialogTheme,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                hour = hourOfDay;
                                min = minute;
                                String time = hour + ":" + min;
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat f24format = new SimpleDateFormat("HH:mm");
                                try {
                                    Date date = f24format.parse(time);
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat f12format = new SimpleDateFormat("hh:mm aa");
                                    assert date != null;
                                    mEditTextTime.setText(f12format.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 12, 0, false
                );
                timePickerDialog.updateTime(hour,min);
                timePickerDialog.show();
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference("events");
        StorageReference mStorageRefBackup = FirebaseStorage.getInstance().getReference("eventsBackup");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("events");
        mDatabaseRefBackup = FirebaseDatabase.getInstance().getReference("eventsBackup");
        mRegistrationRef = FirebaseDatabase.getInstance().getReference("registration");


        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = mEventTypeGroup.getCheckedRadioButtonId();
                mEventTypeButton = findViewById(selectedId);

                title = mEditTextTitle.getText().toString();
                description = mEditTextDescription.getText().toString();
                venue = mEditTextVenue.getText().toString();
                time = mEditTextTime.getText().toString();
                amount = mEditTextAmount.getText().toString();
                eventDate = mEditTextDate.getText().toString();

//                System.out.println("Selected id: " + selectedId);

                if (title.equals("") || title.length() < 3) {
                    mEditTextTitle.requestFocus();
                    mEditTextTitle.setError("Enter valid Title");
                    return;
                }

                if (description.equals("") || description.length() < 4) {
                    mEditTextDescription.requestFocus();
                    mEditTextDescription.setError("Enter valid Description");
                    return;
                }

                if (venue.equals("") || venue.length() < 3) {
                    mEditTextVenue.requestFocus();
                    mEditTextVenue.setError("Enter valid Venue");
                    return;
                }

                if(amount.equals(""))
                {
                    amount = "-";
                }

                if (time.equals("")) {
                    mEditTextTime.requestFocus();
                    mEditTextTime.setError("Select valid Time");
                    return;
                }

                if (eventDate.equals("")) {
                    mEditTextDate.requestFocus();
                    mEditTextDate.setError("Select valid Date");
                    return;
                }

                if (selectedId == -1) {
                    Toast.makeText(getApplicationContext(), "Select Event Type", Toast.LENGTH_LONG).show();
                    return;
                }


                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(Admin_add_event.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
                } else {
//                    hideKeyboard();
                    uploadFile();
                }
            }
        });

        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesActivity();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            mImageView.setImageURI(mImageUri);
        }
    }

    private void updateLabelDate() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        /* Checking if selected date is before current date */
//        System.out.println("Selected Date: " + calendar.getTime().toString());

        if (calendar.getTime().before(Calendar.getInstance().getTime())) {
            Toast.makeText(this.getApplicationContext(), "Selected date is before Current Date", Toast.LENGTH_SHORT).show();
            mEditTextDate.requestFocus();
            mEditTextDate.setError("Invalid Date");
        } else
            mEditTextDate.setText(sdf.format(calendar.getTime()));
    }

    private void updateLabelDeadline() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        if(mEditTextDate.getText().toString().length() == 0)
        {
            Toast.makeText(this.getApplicationContext(), "Select Event Date first", Toast.LENGTH_SHORT).show();
            mEditTextDeadline.requestFocus();
            mEditTextDeadline.setError("Select Event Date first");
            return;
        }

        Date deadlineDate = null;
        try {
            deadlineDate = sdf.parse(mEditTextDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /* Checking if selected date is before current date */
//        System.out.println("Selected Date: " + calendar.getTime().toString());

        if (calendar.getTime().before(Calendar.getInstance().getTime()) || calendar.getTime().after(deadlineDate)) {
            Toast.makeText(this.getApplicationContext(), "Choose correct date", Toast.LENGTH_SHORT).show();
            mEditTextDeadline.requestFocus();
            mEditTextDeadline.setError("Invalid Date");
        } else
            mEditTextDeadline.setText(sdf.format(calendar.getTime()));
        }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @SuppressLint("WrongConstant")
    private void uploadFile() {

        check_connection();
        mPrefs = getSharedPreferences("Flag", MODE_APPEND);
        int X = mPrefs.getInt("Flag", 0);
        if (X == 1) {
            if (mImageUri != null) {
                StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

                mUploadTask = fileReference.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressBar.setProgress(0);
                                    }
                                }, 500);

                            Toast.makeText(Admin_add_event.this, "Upload Successful", Toast.LENGTH_LONG).show();
                            mTextViewShowUploads.setClickable(true);
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();

                            title = mEditTextTitle.getText().toString().trim();
                            description = mEditTextDescription.getText().toString().trim();
                            assert downloadUrl != null;
                            imageUrl = downloadUrl.toString();
                            venue = mEditTextVenue.getText().toString().trim();
                            amount = mEditTextAmount.getText().toString().trim();
                            time = mEditTextTime.getText().toString().trim();
                            eventDate = mEditTextDate.getText().toString().trim();
                            eventDeadline = mEditTextDeadline.getText().toString().trim();
                            eventType = mEventTypeButton.getText().toString().trim();

                            DatabaseReference mMemberRef = mDatabaseRef.child("member");
                            DatabaseReference mNonMemberRef = mDatabaseRef.child("everyone");

                            EventsData data = new EventsData(title, description, venue, amount, time, eventDate, eventDeadline, eventType, imageUrl);
                            EventsData dataBackup = new EventsData(title, eventDate, imageUrl, 0, 0);
                            String uploadId;

                            if (eventType.equals("Members Only")) {
                                uploadId = mMemberRef.push().getKey();

                            } else {
                                uploadId = mNonMemberRef.push().getKey();
                                assert uploadId != null;
                                mNonMemberRef.child(uploadId).setValue(data);
                            }
                                assert uploadId != null;
                                mMemberRef.child(uploadId).setValue(data);

                                //                            String uploadIdBackup = mDatabaseRefBackup.push().getKey();
                                mRegistrationRef.child(uploadId).setValue(0);
                                mDatabaseRefBackup.child(uploadId).setValue(dataBackup);

                                //                            notification();
                                clearFields();
                            }
                        })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Admin_add_event.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })

                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                mProgressBar.setProgress((int) progress);
                            }
                        });
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void clearFields() {
        mEditTextTitle.setText("");
        mEditTextDescription.setText("");
        mEditTextVenue.setText("");
        mEditTextTime.setText("");
        mEditTextAmount.setText("");
        mEditTextDate.setText("");
        mEditTextDeadline.setText("");
        mEventTypeGroup.clearCheck();
        mImageView.setImageResource(0);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    private void openImagesActivity() {
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            Toast.makeText(Admin_add_event.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
            mTextViewShowUploads.setClickable(false);
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mUploadTask!=null && mUploadTask.isInProgress()) {
            Toast.makeText(Admin_add_event.this, "Upload in Progress. Please wait.", Toast.LENGTH_SHORT).show();
        }

    }

    public void check_connection()
    {
        connection_detector = new Connection_Detector(this);
        connection_detector.execute();
    }
}
