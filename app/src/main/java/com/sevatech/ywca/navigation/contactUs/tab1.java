package com.sevatech.ywca.navigation.contactUs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sevatech.ywca.R;

public class tab1 extends Fragment {

    public tab1() {
        // Required empty public constructor
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_tab1, container,false);

        TextView tv1 = root.findViewById(R.id.text1);
        tv1.setText("Ph: 022-26702831 /022-26702872");
        Linkify.addLinks(tv1, Patterns.PHONE, "tel:", Linkify.sPhoneNumberMatchFilter, Linkify.sPhoneNumberTransformFilter);

        TextView tv2 = root.findViewById(R.id.text2);
        tv2.setText("Ph: 022-27570786");
        Linkify.addLinks(tv2, Patterns.PHONE, "tel:", Linkify.sPhoneNumberMatchFilter, Linkify.sPhoneNumberTransformFilter);

        TextView tv3 = root.findViewById(R.id.text3);
        tv3.setText("Ph: 022-23096544 / 022-23096555 /\n 022-23020469");
        Linkify.addLinks(tv3, Patterns.PHONE, "tel:", Linkify.sPhoneNumberMatchFilter, Linkify.sPhoneNumberTransformFilter);

        TextView tv4 = root.findViewById(R.id.text4);
        tv4.setText("Ph: 022-22024480 / 022-22020122");
        Linkify.addLinks(tv4, Patterns.PHONE, "tel:", Linkify.sPhoneNumberMatchFilter, Linkify.sPhoneNumberTransformFilter);

        return root;
    }
}
