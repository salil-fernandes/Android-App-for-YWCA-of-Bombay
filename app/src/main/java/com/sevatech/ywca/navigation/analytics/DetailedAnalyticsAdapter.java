package com.sevatech.ywca.navigation.analytics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sevatech.ywca.R;
import com.sevatech.ywca.helper.UserData;

import java.util.List;

public class DetailedAnalyticsAdapter extends RecyclerView.Adapter<DetailedAnalyticsAdapter.DetailedAnalyticsViewHolder> {
    private Context mContext;
    private List<UserData> mUploads;

    public DetailedAnalyticsAdapter(Context mContext, List<UserData> mUploads) {
        this.mContext = mContext;
        this.mUploads = mUploads;
    }

    @NonNull
    @Override
    public DetailedAnalyticsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.register_item,parent,false);
        return new DetailedAnalyticsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailedAnalyticsViewHolder holder, int position) {
        UserData uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(String.format("Name: %s", uploadCurrent.getUserName()));
        holder.textViewEmail.setText(String.format("Email: %s", uploadCurrent.getUserEmail()));
        holder.textViewContact.setText(String.format("Contact: %s", uploadCurrent.getUserContact()));
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    static class DetailedAnalyticsViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewEmail, textViewContact;

        DetailedAnalyticsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.registered_name);
            textViewEmail = itemView.findViewById(R.id.registered_email);
            textViewContact = itemView.findViewById(R.id.registered_contact);
        }
    }
}
