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

public class tab3 extends Fragment {

    public tab3() {
        // Required empty public constructor
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_tab3, container, false);
        TextView tv1 = root.findViewById(R.id.t1);
        tv1.setText("Ph: 022-22025053 / 022-66247222 /\n 022-22826814");
        Linkify.addLinks(tv1, Patterns.PHONE, "tel:", Linkify.sPhoneNumberMatchFilter, Linkify.sPhoneNumberTransformFilter);
        TextView tv2 = root.findViewById(R.id.t2);
        tv2.setText("Ph: 022-26702831 / 022-26702872");
        Linkify.addLinks(tv2, Patterns.PHONE, "tel:", Linkify.sPhoneNumberMatchFilter, Linkify.sPhoneNumberTransformFilter);

        return root;
    }
}
