package com.sevatech.ywca.navigation.contactUs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ContactUsAdapter extends FragmentPagerAdapter {

    private int numberoftabs;


    public ContactUsAdapter(@NonNull FragmentManager fm, int numberoftabs) {
        super(fm,numberoftabs);
        this.numberoftabs = numberoftabs;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new tab1();
            case 1:
                return new tab2();
            case 2:
                return new tab3();
            default:
                return new tab1();
        }
    }

    @Override
    public int getCount() {
        return numberoftabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
