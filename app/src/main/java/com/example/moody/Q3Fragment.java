package com.example.moody;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Q3Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Q3Fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private  static  String URL_LOGIN="http://192.168.0.231/api/userrelationship/getallbyid";

    private String mParam1;
    private String mParam2;

    public Q3Fragment() {
        // Required empty public constructor
    }

    public static Q3Fragment newInstance(String param1, String param2) {
        Q3Fragment fragment = new Q3Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_q3);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_q3, container, false);
    }
}