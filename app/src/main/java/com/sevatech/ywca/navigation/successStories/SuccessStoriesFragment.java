package com.sevatech.ywca.navigation.successStories;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.sevatech.ywca.R;


public class SuccessStoriesFragment extends Fragment {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotsLayout;

    private TextView[] mDots;

    private SliderAdapter sliderAdapter;


    private int mCurrentPage;
    int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    public SuccessStoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_success_stories, container, false);
        NavigationView navigationView = getActivity().findViewById(R.id.navigation_view);
        Menu drawer = navigationView.getMenu();
        MenuItem menuItem;
        menuItem = drawer.findItem(R.id.nav_success_stories);
        if(!menuItem.isChecked())
            menuItem.setChecked(true);

        mSlideViewPager = view.findViewById(R.id.slideViewPager);
        mDotsLayout = view.findViewById(R.id.dotsLayout);


        sliderAdapter = new SliderAdapter(this.getContext());

        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(ViewListener);

        return view;
    }

    private void addDotsIndicator(int position) {
        mDots = new TextView[9];
        mDotsLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this.getContext());
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.gray));

            mDotsLayout.addView(mDots[i]);
        }
        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.white));
        }
    }

    ViewPager.OnPageChangeListener ViewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {

            addDotsIndicator(i);
            mCurrentPage = i;


        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
}
