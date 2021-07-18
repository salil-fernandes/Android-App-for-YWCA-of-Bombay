package com.sevatech.ywca.navigation.successStories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.sevatech.ywca.R;

class SliderAdapter extends PagerAdapter {

    private Context mContext;
    private String[] slide_headings;
    private String[] slide_descs;

    SliderAdapter(Context context) {
        this.mContext = context;

        //Arrays

        slide_headings = new String[]{
                mContext.getResources().getString(R.string.heading1),
                mContext.getResources().getString(R.string.heading2),
                mContext.getResources().getString(R.string.heading3),
                mContext.getResources().getString(R.string.heading4),
                mContext.getResources().getString(R.string.heading5),
                mContext.getResources().getString(R.string.heading6),
                mContext.getResources().getString(R.string.heading7),
                mContext.getResources().getString(R.string.heading8),
                mContext.getResources().getString(R.string.heading9)
        };

        slide_descs = new String[]{
                mContext.getResources().getString(R.string.desc1),
                mContext.getResources().getString(R.string.desc2),
                mContext.getResources().getString(R.string.desc3),
                mContext.getResources().getString(R.string.desc4),
                mContext.getResources().getString(R.string.desc5),
                mContext.getResources().getString(R.string.desc6),
                mContext.getResources().getString(R.string.desc7),
                mContext.getResources().getString(R.string.desc8),
                mContext.getResources().getString(R.string.desc9)
        };
    }

    private int[] slide_images = {
            R.drawable.succ,
            R.drawable.asliceofsupport,
            R.drawable.astitchintime,
            R.drawable.beautywithbrains,
            R.drawable.foodforthought,
            R.drawable.hairtales,
            R.drawable.nursingaid,
            R.drawable.promotingeducation,
            R.drawable.aasra
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = view.findViewById(R.id.slide_image);
        TextView slideHeading = view.findViewById(R.id.slide_heading);
        TextView slideDescription = view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descs[position]);

        container.addView(view);


        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}

