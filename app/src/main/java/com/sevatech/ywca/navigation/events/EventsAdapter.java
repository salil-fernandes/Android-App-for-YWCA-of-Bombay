package com.sevatech.ywca.navigation.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sevatech.ywca.InternetConnection;
import com.sevatech.ywca.R;
import com.sevatech.ywca.helper.EventsData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> implements Filterable {
    private Context mContext;
    private List<EventsData> mUploads;
    private List<EventsData> mUploadsAll;
    private OnItemLongClickListener mListener;

    View view;

    public EventsAdapter(Context context, ArrayList<EventsData> uploads) {
        mContext = context;
        this.mUploads = uploads;
        this.mUploadsAll = uploads;
    }

    public class EventsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView textViewTitle, textViewDescription, textViewVenue, textViewDate, textViewAmount, textViewTime,textViewType;
        ImageView imageView;

        EventsViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            textViewTitle = itemView.findViewById(R.id.text_event_name);
            textViewDescription = itemView.findViewById(R.id.text_event_desc);
            textViewVenue = itemView.findViewById(R.id.text_event_venue);
            textViewDate = itemView.findViewById(R.id.text_event_date);
            textViewAmount = itemView.findViewById(R.id.text_event_amount);
            textViewTime = itemView.findViewById(R.id.text_event_time);
            textViewType = itemView.findViewById(R.id.text_event_type);
            imageView = itemView.findViewById(R.id.image_view);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {

            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.OnItemClick(position);
                }
            }
        }


        @Override
        public boolean onLongClick(View view) {
            InternetConnection intconn = new InternetConnection(mContext);
            if (!intconn.isNetworkAvailable()) {
                intconn.Dialog(mContext);
            }
            else {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.OnDelete(position);
                        return true;
                    }
                }
            }
            return  false;
        }
    }


    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.event_item, parent, false);

        return new EventsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, final int position) {

        //Collections.sort(mUploads);
        EventsData uploadCurrent = mUploadsAll.get(position);
        final String eventType;
        eventType = uploadCurrent.getEventType().equals("Members Only") ? "member" : "everyone";

        String[] dateParts = uploadCurrent.getEventDate().split("/");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

        int d = month * 30 + day;
        int currentDate = currentMonth * 30 + currentDay;
        int diff = currentDate - d;

        if (diff >= 30) {
            final String selectedKey = uploadCurrent.getEventKey();
            final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("events/everyone");
            final DatabaseReference mMemberDatabaseRef = FirebaseDatabase.getInstance().getReference("events/member");
            FirebaseStorage mStorage = FirebaseStorage.getInstance();

            StorageReference imageRef = mStorage.getReferenceFromUrl(uploadCurrent.getEventImageUrl());
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (eventType.equals("everyone"))
                        mDatabaseRef.child(selectedKey).removeValue();
                    mMemberDatabaseRef.child(selectedKey).removeValue();
//                    Toast.makeText(mContext, "Item deleted", Toast.LENGTH_SHORT).show();
                }
            });
        }


        holder.textViewTitle.setText(uploadCurrent.getEventTitle());
        holder.textViewDescription.setText(uploadCurrent.getEventDescription());
        holder.textViewVenue.setText(String.format("Venue: %s", uploadCurrent.getEventVenue()));
        holder.textViewAmount.setText(String.format("Amount: %s", uploadCurrent.getEventAmount()));
        holder.textViewTime.setText(String.format("Time: %s", uploadCurrent.getEventTime()));
        holder.textViewDate.setText(String.format("Date: %s", uploadCurrent.getEventDate()));

        holder.textViewVenue.setText(String.format("Venue: %s", uploadCurrent.getEventVenue()));
        holder.textViewDate.setText(String.format("Date: %s", uploadCurrent.getEventDate()));
//        System.out.println(uploadCurrent.getEventType());
        if(uploadCurrent.getEventType().equals("Members Only"))
            holder.textViewType.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(uploadCurrent.getEventImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);

    }


    @Override
    public int getItemCount() {
        if (mUploadsAll != null) {
            return mUploadsAll.size();
        } else
            return 0;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<EventsData> filteredList = new ArrayList<>();
            FilterResults filterResults = new FilterResults();


            if (charSequence.toString().isEmpty() || charSequence.length() == 0) {
                filteredList.addAll(mUploads);
                filterResults.count = filteredList.size();
                filterResults.values = filteredList;
            } else {
                for (EventsData event : mUploads) {
                    if (event.getEventVenue().toLowerCase().trim().contains(charSequence.toString().trim()))
                        filteredList.add(event);


                    filterResults.count = filteredList.size();
                    filterResults.values = filteredList;
                }
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//            mUploads.clear();
            mUploadsAll = ((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public interface OnItemLongClickListener {
        void OnItemClick(int position);

        void OnDelete(int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mListener = listener;
    }
}
