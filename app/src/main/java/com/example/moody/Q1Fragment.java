package com.example.moody;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Q1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Q1Fragment extends Fragment {

    public SeekBar q1range;
    int max_value=10,step=1;

    public Q1Fragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Q1Fragment newInstance(String param1, String param2) {
        Q1Fragment fragment = new Q1Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_q1, container, false);

        q1range = (SeekBar)v.findViewById(R.id.seekBar1);
        q1range.setMax(max_value/step);
        q1range.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MainActivity.q1range = progress;
                if(progress>=30){
                    System.out.println("ddd");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return v;
    }
}