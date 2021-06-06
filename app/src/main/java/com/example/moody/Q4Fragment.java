package com.example.moody;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class Q4Fragment extends Fragment {
    Spinner dropdown;
    RadioGroup radioGroup;
    ArrayList<String> arrayListDropDown;
    String selectedItem;

    public Q4Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_q4, container, false);

        dropdown = (Spinner) v.findViewById(R.id.special_situation);
        radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);


        String[] items = new String[]{
                "Select an answer", "Work/Study", "Family", "Partner", "Health", "Other", "None"
        };


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = (String) parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), "Selected: " + position, Toast.LENGTH_LONG).show();
                RadioButton pos = (RadioButton) v.findViewById(R.id.radioPositive);
                RadioButton neg = (RadioButton) v.findViewById(R.id.radioNegative);
                pos.setChecked(false);
                neg.setChecked(false);

                if (MainActivity.q4.containsKey(selectedItem)) {
                    String selectitem=MainActivity.q4.get(selectedItem);
                    if (selectitem.equals("Positive")) {
                        System.out.println(MainActivity.q4.get(selectedItem));
                        pos.setChecked(true);
                        neg.setChecked(false);
                    } else {
                        pos.setChecked(false);
                        neg.setChecked(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                if (selectedItem.equals("Select an answer")) {
                    Toast.makeText(v.getContext(), "Select an item", Toast.LENGTH_LONG).show();
                    checkedRadioButton.setChecked(false);
                } else {
                    boolean isChecked = checkedRadioButton.isChecked();
                    String selectedRbText = checkedRadioButton.getText().toString();
                    MainActivity.q4.put(selectedItem, selectedRbText);
                }
                System.out.println(MainActivity.q4);
           /*     int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if (selectedRadioButtonId != -1) {
                    selectedRadioButton = (RadioButton) view.findViewById(selectedRadioButtonId);
                    System.out.println(selectedRadioButton);

                    selectedRbText = selectedRadioButton.getText().toString();
                    MainActivity.q4answer = selectedRbText;
                }*/
            }
        });

        return v;
    }
}