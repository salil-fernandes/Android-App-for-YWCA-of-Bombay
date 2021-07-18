package com.sevatech.ywca.navigation.aboutUs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.sevatech.ywca.R;

public class AboutUsFragment extends Fragment {

    public AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        NavigationView navigationView = getActivity().findViewById(R.id.navigation_view);
        Menu drawer = navigationView.getMenu();
        MenuItem menuItem;
        menuItem = drawer.findItem(R.id.nav_about_us);
        if(!menuItem.isChecked())
            menuItem.setChecked(true);

        Button button = view.findViewById(R.id.memberButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MembershipFragment fragment = new MembershipFragment();
                if (getFragmentManager() != null) {
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                }
            }
        });
        SharedPreferences sp=getActivity().getSharedPreferences("f1", Context.MODE_PRIVATE);
        String status=sp.getString("status","");
        if(status.equals("NonMember"))
            button.setVisibility(View.VISIBLE);
        else
            button.setVisibility(View.GONE);
        return view;
    }
}
