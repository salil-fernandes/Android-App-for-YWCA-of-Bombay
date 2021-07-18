package com.sevatech.ywca.navigation.adminApproval;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sevatech.ywca.Connection_Detector;
import com.sevatech.ywca.InternetConnection;
import com.sevatech.ywca.R;
import com.sevatech.ywca.helper.ProfileData;
import java.util.HashMap;
import java.util.Map;

import static android.os.ParcelFileDescriptor.MODE_APPEND;


public class AdminApprovalFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textView;
    private DatabaseReference mRef;
    private DatabaseReference memberRef;
    private FirebaseRecyclerAdapter<ProfileData, ApprovalViewHolder> adapter;
    private SharedPreferences sp;
    //    List<String> keys = new ArrayList<>();
    String sr_no = "214";
    SharedPreferences mPrefs;
    Connection_Detector connection_detector;

    public AdminApprovalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_approval, container, false);
        NavigationView navigationView = getActivity().findViewById(R.id.navigation_view);
        Menu drawer = navigationView.getMenu();
        MenuItem menuItem;
        menuItem = drawer.findItem(R.id.nav_admin_approval);
        if (!menuItem.isChecked())
            menuItem.setChecked(true);

        recyclerView = view.findViewById(R.id.approval_rv);
        textView = view.findViewById(R.id.text_no_approval);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRef = FirebaseDatabase.getInstance().getReference("approval");
        memberRef = FirebaseDatabase.getInstance().getReference("Members");

        sp = getActivity().getSharedPreferences("f1", Context.MODE_PRIVATE);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<ProfileData>()
                        .setQuery(mRef, ProfileData.class)
                        .build();


        adapter = new FirebaseRecyclerAdapter<ProfileData, ApprovalViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ApprovalViewHolder holder, final int position, @NonNull ProfileData model) {
                final String key = getRef(position).getKey();
                textView.setVisibility(View.GONE);

//                keys.add(key);

                mRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ProfileData data = snapshot.getValue(ProfileData.class);
                        assert data != null;
                        holder.userName.setText(String.format(getString(R.string.profile_name), data.getName()));
                        holder.userPhone.setText(String.format(getString(R.string.profile_phone), data.getPhone()));
                        holder.userYwca.setText(String.format(getString(R.string.profile_ywca_location), data.getClosest_ywca()));
                        holder.userEmail.setText(String.format(getString(R.string.profile_email), data.getEmail()));
                        holder.userAddress.setText(String.format(getString(R.string.profile_address), data.getAddress()));
//                        sr_no = data.getKey();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.approveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(getContext(), "SetValue " + key, Toast.LENGTH_SHORT).show();
                        InternetConnection intconn = new InternetConnection(getContext());
                        if (!intconn.isNetworkAvailable()) {
                            intconn.Dialog(getContext());
                        } else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                            alertDialogBuilder.setMessage("Do you want to accept this request?");
                            alertDialogBuilder.setCancelable(true);

                            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @SuppressLint("WrongConstant")
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    check_connection();
                                    mPrefs = getContext().getSharedPreferences("Flag", MODE_APPEND);
                                    int X = mPrefs.getInt("Flag", 0);
                                    if (X == 1) {
                                        mRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                ProfileData data = snapshot.getValue(ProfileData.class);

                                                Map<String, Object> updates = new HashMap<String, Object>();
                                                updates.put("name", data.getName());
                                                updates.put("phone", data.getPhone());
                                                updates.put("email", data.getEmail());
                                                updates.put("address", data.getAddress());
                                                updates.put("closest_ywca", data.getClosest_ywca());
                                                memberRef.child(key).updateChildren(updates);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        mRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
//                                        Toast.makeText(getContext(), "Updated key:" + key, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }
                            });

                            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        }
                    }
                });

                holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InternetConnection intconn = new InternetConnection(getContext());
                        if (!intconn.isNetworkAvailable()) {
                            intconn.Dialog(getContext());
                        } else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                            alertDialogBuilder.setMessage("Do you want to remove this request?");
                            alertDialogBuilder.setCancelable(true);

                            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @SuppressLint("WrongConstant")
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    check_connection();
                                    mPrefs = getContext().getSharedPreferences("Flag", MODE_APPEND);
                                    int X = mPrefs.getInt("Flag", 0);
                                    if (X == 1) {
                                        mRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
//                                        Toast.makeText(getContext(), "Removed key:" + key, Toast.LENGTH_SHORT).show();
                                            }

                                        });
                                    }
                                }
                            });
                            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }
                });
            }

            @NonNull
            @Override
            public ApprovalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.approval_item, parent, false);
                return new ApprovalViewHolder(view);
            }
        }

        ;

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ApprovalViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userPhone, userYwca, userEmail, userProfession, userAddress;
        ImageButton approveBtn, rejectBtn;

        ApprovalViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.approval_name);
            userPhone = itemView.findViewById(R.id.approval_phone);
            userYwca = itemView.findViewById(R.id.approval_ywca);
            userEmail = itemView.findViewById(R.id.approval_email);
            userAddress = itemView.findViewById(R.id.approval_address);
//            userProfession = itemView.findViewById(R.id.approval_profession);

            approveBtn = itemView.findViewById(R.id.approveBtn);
            rejectBtn = itemView.findViewById(R.id.rejectBtn);


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    public void check_connection() {
        connection_detector = new Connection_Detector(getContext());
        connection_detector.execute();
    }
}