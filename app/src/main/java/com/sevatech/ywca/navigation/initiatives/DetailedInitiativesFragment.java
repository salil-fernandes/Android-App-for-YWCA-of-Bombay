package com.sevatech.ywca.navigation.initiatives;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.sevatech.ywca.R;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DetailedInitiativesFragment extends Fragment {

    ArrayList<Integer> imageList = new ArrayList<>();
    ArrayList<String> textList = new ArrayList<>();
    String text;
    String header;
    Window window;
    private InitiativesFragment initiativesFragment;
    private FragmentManager mFragmentManager;


    public DetailedInitiativesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detailed_initiatives, container, false);
        mFragmentManager = getFragmentManager();
        initiativesFragment = new InitiativesFragment();
        window = getActivity().getWindow();
        window.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Bundle bundle = getArguments();
        if (bundle != null) {
            text = bundle.getString("textView");
            header = bundle.getString("header");
            imageList = bundle.getIntegerArrayList("images");
            textList = bundle.getStringArrayList("texts");

            TextView textView = view.findViewById(R.id.s1);
            TextView headertv = view.findViewById(R.id.header);
            textView.setText(text);
            headertv.setText(header);
            RecyclerView recyclerView = view.findViewById(R.id.rv);
            LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(llm);
            SliderAdapter adapter = new SliderAdapter(getContext(), imageList, textList);
            recyclerView.setAdapter(adapter);
        }

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                Log.i(getTag(), "keyCode: " + keyCode);

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, initiativesFragment).commit();
                    return true;
                }

                return false;
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        window.clearFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }
}
