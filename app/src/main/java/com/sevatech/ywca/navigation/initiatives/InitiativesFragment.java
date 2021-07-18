package com.sevatech.ywca.navigation.initiatives;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.material.navigation.NavigationView;
import com.sevatech.ywca.R;

import java.util.ArrayList;
import java.util.List;


public class InitiativesFragment extends Fragment {

    private ArrayList<Integer> imageList = new ArrayList<>();
    private ArrayList<String> textList = new ArrayList<>();
    private String text,header;


    private final Bundle args = new Bundle();

    private DetailedInitiativesFragment detailedInitiativesFragment;


    public InitiativesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getContext() fragment
        View view = inflater.inflate(R.layout.fragment_initiatives, container, false);
        NavigationView navigationView = getActivity().findViewById(R.id.navigation_view);
        Menu drawer = navigationView.getMenu();
        MenuItem menuItem;
        menuItem = drawer.findItem(R.id.nav_initiatives);
        if(!menuItem.isChecked())
            menuItem.setChecked(true);
        RelativeLayout rellay_piya = view.findViewById(R.id.rellay_piya);
        RelativeLayout rellay_asha = view.findViewById(R.id.rellay_asha);
        RelativeLayout rellay_pasi = view.findViewById(R.id.rellay_pasi);
        RelativeLayout rellay_wdu = view.findViewById(R.id.rellay_wdu);
        RelativeLayout rellay_ywcahostels = view.findViewById(R.id.rellay_ywcahostels);
        RelativeLayout rellay_pr = view.findViewById(R.id.rellay_pr);
        RelativeLayout rellay_sh = view.findViewById(R.id.rellay_sh);
        RelativeLayout rellay_membership = view.findViewById(R.id.rellay_membership);
        RelativeLayout rellay_others = view.findViewById(R.id.rellay_others);
        RelativeLayout rellay_se = view.findViewById(R.id.rellay_se);
        RelativeLayout rellay_ic = view.findViewById(R.id.rellay_ic);


        detailedInitiativesFragment = new DetailedInitiativesFragment();

        rellay_piya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageList.clear();
                textList.clear();

                imageList.add(R.drawable.image6);

                imageList.add(R.drawable.image8);
                imageList.add(R.drawable.image9);
                imageList.add(R.drawable.image10);
                imageList.add(R.drawable.image11);
                imageList.add(R.drawable.image12);
                args.putIntegerArrayList("images", imageList);


                textList.add("Walk for freedom");

                textList.add("Walkathon");
                textList.add("A Trek to Kaldurg Fort");
                textList.add("Interschool Competition");
                textList.add("Dance Therapy by Piya");
                textList.add("Piya");
                args.putStringArrayList("texts", textList);

                text = getContext().getResources().getString(R.string.piya_text);
                header = "PIYA-Participation and Involvement of Youth in Action";
                args.putString("header", header);
                args.putString("textView", text);

                detailedInitiativesFragment.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailedInitiativesFragment)
                        .commit();

            }
        });

        rellay_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageList.clear();
                textList.clear();

                imageList.add(R.drawable.image51);
                imageList.add(R.drawable.image52);
                imageList.add(R.drawable.image53);
                imageList.add(R.drawable.image54);
                args.putIntegerArrayList("images", imageList);

                textList.add("IC Review ");
                textList.add("Family Room");
                textList.add("Waiting Room");
                textList.add("Reception");
                args.putStringArrayList("texts", textList);

                text = getContext().getResources().getString(R.string.ic_text);
                header = "IC-International Centre";
                args.putString("header", header);
                args.putString("textView", text);

                detailedInitiativesFragment.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailedInitiativesFragment)
                        .commit();

            }
        });

        rellay_asha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageList.clear();
                textList.clear();

                imageList.add(R.drawable.image13);
                imageList.add(R.drawable.image14);
                imageList.add(R.drawable.image15);
                imageList.add(R.drawable.image16);
                imageList.add(R.drawable.image17);
                imageList.add(R.drawable.image18);
                imageList.add(R.drawable.image19);
                args.putIntegerArrayList("images", imageList);


                textList.add("Annual Day");
                textList.add("Sports Day");
                textList.add("Training of Puppeteers");
                textList.add("Workshop on Muslim Legal Rights");
                textList.add("Cartoon Making Workshop");
                textList.add("Self Defence Training");
                textList.add("Annual Day Celebration");
                args.putStringArrayList("texts", textList);

                text = getContext().getResources().getString(R.string.asha_text);
                header = "Asha Kiran";
                args.putString("textView", text);
                args.putString("header", header);

                detailedInitiativesFragment.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailedInitiativesFragment)
                        .commit();

            }
        });

        rellay_pasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageList.clear();
                textList.clear();

                imageList.add(R.drawable.image1);
                imageList.add(R.drawable.image2);
                imageList.add(R.drawable.image3);
                imageList.add(R.drawable.image4);
                imageList.add(R.drawable.image5);
                args.putIntegerArrayList("images", imageList);


                textList.add("Panel Discussion on Women and Social Media ");
                textList.add("Paralegal Training");
                textList.add("Session on RTI");
                textList.add("Life and Struggle of Transgender");
                textList.add("Children's Day Celebration");

                args.putStringArrayList("texts", textList);

                text = getContext().getResources().getString(R.string.pasi_text);
                header = "PASI-Public Affairs and Social Issues";
                args.putString("textView", text);
                args.putString("header", header);

                detailedInitiativesFragment.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailedInitiativesFragment)
                        .commit();

            }
        });

        rellay_wdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageList.clear();
                textList.clear();

                imageList.add(R.drawable.image21);
                imageList.add(R.drawable.image23);
                imageList.add(R.drawable.image24);
                imageList.add(R.drawable.image25);
                imageList.add(R.drawable.image26);
                imageList.add(R.drawable.image27);
                imageList.add(R.drawable.image28);
                imageList.add(R.drawable.image29);
                imageList.add(R.drawable.image30);
                imageList.add(R.drawable.image31);
                imageList.add(R.drawable.image32);
                imageList.add(R.drawable.image33);
                args.putIntegerArrayList("images", imageList);



                textList.add("Nutritious food Competiton");
                textList.add("Children’s Programme");
                textList.add("Bakery certificate distribution");
                textList.add("Debate Competition");
                textList.add("Bakery Practical");
                textList.add("Rally on Human Rights Awareness");
                textList.add("Annual Sports Day Celebration");
                textList.add("Bunny Tamtola");
                textList.add("Peace March");
                textList.add("Children’s Camp");
                textList.add("Disaster Management training");
                textList.add("Anand Mela");
                args.putStringArrayList("texts", textList);

                text = getContext().getResources().getString(R.string.wdu_text);
                header = "WDU-Women's Development Unit";
                args.putString("textView", text);
                args.putString("header", header);

                detailedInitiativesFragment.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailedInitiativesFragment)
                        .commit();

            }
        });
        rellay_ywcahostels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageList.clear();
                textList.clear();

                imageList.add(R.drawable.image34);
                imageList.add(R.drawable.image35);
                imageList.add(R.drawable.image36);
                imageList.add(R.drawable.image37);
                imageList.add(R.drawable.image38);
                imageList.add(R.drawable.image39);
                args.putIntegerArrayList("images", imageList);


                textList.add("ABH In-night ");
                textList.add("DDH");
                textList.add("National Festival Celebration");
                textList.add("Dining Room LWH");
                textList.add("DDH Lounge");
                textList.add("LWH Building");
                args.putStringArrayList("texts", textList);

                text = getContext().getResources().getString(R.string.ywca_hostels_text);
                header = "YWCA Hostels";
                args.putString("textView", text);
                args.putString("header", header);

                detailedInitiativesFragment.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailedInitiativesFragment)
                        .commit();

            }
        });
        rellay_pr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageList.clear();
                textList.clear();

                imageList.add(R.drawable.image40);
                imageList.add(R.drawable.image41);
                imageList.add(R.drawable.image42);
                args.putIntegerArrayList("images", imageList);


                textList.add("Staff Training");
                textList.add("Music around the world");
                textList.add("Training on GST");
                args.putStringArrayList("texts", textList);

                text = getContext().getResources().getString(R.string.pr_text);
                header = "Public Relations";
                args.putString("textView", text);
                args.putString("header", header);

                detailedInitiativesFragment.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailedInitiativesFragment)
                        .commit();


            }
        });
        rellay_sh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageList.clear();
                textList.clear();

                imageList.add(R.drawable.image43);
                imageList.add(R.drawable.image44);
                imageList.add(R.drawable.image45);
                args.putIntegerArrayList("images", imageList);


                textList.add("Inaugration of Ummeed Shelter home");
                textList.add("Inaugration of Ummeed Shelter home");
                textList.add("Ashraya");
                args.putStringArrayList("texts", textList);

                text = getContext().getResources().getString(R.string.sh_text);
                header = "Shelter Homes";
                args.putString("textView", text);
                args.putString("header", header);

                detailedInitiativesFragment.setArguments(args);
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailedInitiativesFragment)
                        .commit();

            }
        });
        rellay_membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageList.clear();
                textList.clear();

                imageList.add(R.drawable.image46);
                imageList.add(R.drawable.image47);
                imageList.add(R.drawable.image48);
                imageList.add(R.drawable.image49);
                imageList.add(R.drawable.image50);
                args.putIntegerArrayList("images", imageList);


                textList.add("EKTA");
                textList.add("Evening of Carols");
                textList.add("Sacred Music");
                textList.add("World Membership Day");
                textList.add("Christmas Programme");
                args.putStringArrayList("texts", textList);

                text = getContext().getResources().getString(R.string.membership_text);
                header = "Memberships";
                args.putString("textView", text);
                args.putString("header", header);

                detailedInitiativesFragment.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailedInitiativesFragment)
                        .commit();

            }
        });

        rellay_se.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageList.clear();
                textList.clear();

                imageList.add(R.drawable.image55);
                imageList.add(R.drawable.image56);

                imageList.add(R.drawable.image59);
                args.putIntegerArrayList("images", imageList);


                textList.add("World Day of Prayer");
                textList.add("YWCA & YMCA Week of Prayer");

                textList.add("Week of Prayer");
                args.putStringArrayList("texts", textList);

                text = getContext().getResources().getString(R.string.se_text);
                header = "Spiritual Emphasis";
                args.putString("textView", text);
                args.putString("header", header);

                detailedInitiativesFragment.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailedInitiativesFragment)
                        .commit();

            }
        });

        rellay_others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageList.clear();
                textList.clear();

                imageList.add(R.drawable.image60);
                imageList.add(R.drawable.image61);
                imageList.add(R.drawable.image62);
                imageList.add(R.drawable.image63);
                args.putIntegerArrayList("images", imageList);

                textList.add("Paralegal Training");
                textList.add("AGM");
                textList.add("Best NGO Award");
                textList.add("Best NGO Award");
                args.putStringArrayList("texts", textList);

                text = "";
                header = "General";
                args.putString("textView", text);
                args.putString("header", header);

                detailedInitiativesFragment.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailedInitiativesFragment)
                        .commit();

            }
        });
        return view;
    }
}
