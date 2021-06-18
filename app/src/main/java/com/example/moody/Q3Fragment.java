package com.example.moody;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Q3Fragment extends Fragment {
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    ArrayList name;
    public String USER_ID;
    DBHelper DB;
    EditText searchText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_q3, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_user_relationship);
        searchText= (EditText) view.findViewById(R.id.search_text);

        sharedPreferences = this.getActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("USER_ID", "");

        ArrayList<String> array_list = new ArrayList<String>();
        ArrayList<String> array_list_filtered = new ArrayList<String>();
        ArrayList<String> array_list_contacted_user_id = new ArrayList<String>();
        ArrayList<String> array_list_contacted_user_id_filtered = new ArrayList<String>();

        DB=new DBHelper(getContext());

        Cursor cursor_user_relationship = DB.getUserRelationshipData(USER_ID);
        cursor_user_relationship.moveToFirst();

        while(cursor_user_relationship.isAfterLast() == false){
            array_list.add(cursor_user_relationship.getString(cursor_user_relationship.getColumnIndex("nickname")));
            array_list_contacted_user_id.add(cursor_user_relationship.getString(cursor_user_relationship.getColumnIndex("contacted_user_id")));
            cursor_user_relationship.moveToNext();
        }

        final HelperAdapter[] helperAdapter = {new HelperAdapter(getContext(), array_list, array_list_contacted_user_id)};
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(helperAdapter[0]);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                array_list_filtered.clear();
                array_list_contacted_user_id_filtered.clear();

              //  if (s.toString().isEmpty())
                 //   helperAdapter[0] = new HelperAdapter(getContext(), array_list, array_list_contacted_user_id);

                for (int i = 0; i < array_list.size(); i++) {
                    if (array_list.get(i).contains(s.toString())) {
                        array_list_filtered.add(array_list.get(i));
                        array_list_contacted_user_id_filtered.add(array_list_contacted_user_id.get(i));
                    }
                }
                helperAdapter[0] = new HelperAdapter(getContext(), array_list_filtered, array_list_contacted_user_id_filtered);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(helperAdapter[0]);
            }
        });
    }
}