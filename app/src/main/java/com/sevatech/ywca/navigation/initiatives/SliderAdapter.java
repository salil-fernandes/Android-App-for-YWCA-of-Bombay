package com.sevatech.ywca.navigation.initiatives;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sevatech.ywca.R;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private ArrayList<Integer> imageList = new ArrayList<>();
    private ArrayList<String> textList = new ArrayList<>();
    private Context context;

    SliderAdapter(Context context, ArrayList<Integer> imageList, ArrayList<String> textList) {
        this.imageList = imageList;
        this.textList = textList;
        this.context = context;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.slide_item,parent,false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, final int position) {
        holder.imageView.setImageResource(imageList.get(position));
        holder.textView.setText(textList.get(position));

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fullIntent = new Intent(context, InitiativesFullScreenActivity.class);
                fullIntent.putExtra("image",imageList.get(position));
                fullIntent.putExtra("text",textList.get(position));
                context.startActivity(fullIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(imageList!=null)
            return imageList.size();
        else
            return 0;
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.text_view);
        }
    }
}
