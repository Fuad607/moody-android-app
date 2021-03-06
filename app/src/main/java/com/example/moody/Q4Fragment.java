package com.example.moody;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

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
        return v;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View v, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        dropdown = (Spinner) v.findViewById(R.id.special_situation);
        radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);

        String[] items = new String[]{
                "Select an answer", "Work/Study", "Family", "Partner", "Health", "Other", "None"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapter);
        RadioButton pos = (RadioButton) v.findViewById(R.id.radioPositive);
        RadioButton neg = (RadioButton) v.findViewById(R.id.radioNegative);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = (String) parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), "Selected: " + position, Toast.LENGTH_LONG).show();

                if (selectedItem.equals("Select an answer")) {
                    pos.setVisibility(View.INVISIBLE);
                    neg.setVisibility(View.INVISIBLE);
                    //Toast.makeText(v.getContext(), "Select an item", Toast.LENGTH_LONG).show();

                }else{
                    pos.setVisibility(View.VISIBLE);
                    neg.setVisibility(View.VISIBLE);

                    if (MainActivity.q4.containsKey(selectedItem)) {
                        String selectitem = MainActivity.q4.get(selectedItem);
                        if (selectitem.equals("1")) {
                            radioGroup.check(R.id.radioPositive);
                        } else {
                            radioGroup.check(R.id.radioNegative);
                        }
                    }else{
                        radioGroup.clearCheck();
                     }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                String  typ;

                if (selectedItem.equals("Select an answer")) {
                } else {

                    if (checkedRadioButton!=null)
                    {
                        String selectedRbText = checkedRadioButton.getText().toString();
                        if(selectedRbText.equals("Positive")){
                            typ="1";
                        }else {
                            typ="0";
                        }
                        MainActivity.q4.put(selectedItem, typ);
                    }

                }
            }
        });
    }
}