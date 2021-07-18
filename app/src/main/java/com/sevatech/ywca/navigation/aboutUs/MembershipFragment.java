package com.sevatech.ywca.navigation.aboutUs;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sevatech.ywca.R;
import com.sevatech.ywca.navigation.contactUs.ContactUsFragment;

public class MembershipFragment extends Fragment {

    public MembershipFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_membership, container, false);
        Button button1 = view.findViewById(R.id.backButton);
        TextView textView = view.findViewById(R.id.textView5);
        String membershipString = getResources().getString(R.string.howtomembership);
        int i1 = membershipString.indexOf("[");
        int i2 = membershipString.indexOf("]");

        membershipString = membershipString.replace("[","").replace("]","");
        SpannableString spannableString = new SpannableString(membershipString);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ContactUsFragment contactUsFragment = new ContactUsFragment();
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, contactUsFragment).commit();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan, i1, i2 - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        System.out.println(i1+" "+i2);

        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutUsFragment fragment = new AboutUsFragment();
                if (getFragmentManager() != null) {
//                    getFragmentManager().popBackStack();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                AboutUsFragment aboutUsFragment = new AboutUsFragment();
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    assert getFragmentManager() != null;
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, aboutUsFragment).commit();
                    return true;
                }

                return false;
            }
        });

        return view;
    }
}
