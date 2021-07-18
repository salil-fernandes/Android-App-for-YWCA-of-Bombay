package com.sevatech.ywca.navigation.analytics;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sevatech.ywca.R;
import com.sevatech.ywca.helper.EventsData;
import com.sevatech.ywca.navigation.FullScreenActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class AnalyticsAdapter extends RecyclerView.Adapter<AnalyticsAdapter.AnalyticsViewHolder> {

    private Context mContext;
    private List<EventsData> mUploads;
    private OnItemClickListener mListener;
    private DatabaseReference mDataRef;
    private DatabaseReference mBackupRef;
    private String key;

    public AnalyticsAdapter(Context mContext, ArrayList<EventsData> mUploads) {
        this.mContext = mContext;
        this.mUploads = mUploads;
    }

    @NonNull
    @Override
    public AnalyticsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.analytics_item, parent, false);
        mDataRef = FirebaseDatabase.getInstance().getReference("registration");
        mBackupRef = FirebaseDatabase.getInstance().getReference("eventsBackup");
        return new AnalyticsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AnalyticsViewHolder viewHolder, final int position) {

        final EventsData uploadCurrent = mUploads.get(position);
        key = uploadCurrent.getEventKey();

//        String count = String.valueOf(click);
//        String registered = String.valueOf(register);
        viewHolder.textViewTitle.setText(uploadCurrent.getEventTitle());
        viewHolder.textViewDate.setText(uploadCurrent.getEventDate());
        viewHolder.textViewClicks.setText(String.valueOf(uploadCurrent.getEventClickCount()));
        viewHolder.textViewRegistered.setText(String.valueOf(uploadCurrent.getEventRegisterCount()));
//        Picasso.get()
//                .load(uploadCurrent.getEventImageUrl())
//                .fit()
//                .centerCrop()
//                .into(viewHolder.imageView);

//        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent fullScreenIntent = new Intent(mContext, FullScreenActivity.class);
//                fullScreenIntent.putExtra("imageUrl", uploadCurrent.getEventImageUrl());
//                mContext.startActivity(fullScreenIntent);
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class AnalyticsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        ImageView imageView;
        TextView textViewTitle, textViewDate, textViewClicks, textViewRegistered;

        AnalyticsViewHolder(@NonNull View itemView) {
            super(itemView);
//            imageView = itemView.findViewById(R.id.eventImage);
            textViewTitle = itemView.findViewById(R.id.eventTitle);
            textViewDate = itemView.findViewById(R.id.eventDate);
            textViewClicks = itemView.findViewById(R.id.eventClicks);
            textViewRegistered = itemView.findViewById(R.id.eventRegistered);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.OnItemCLick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void OnItemCLick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
