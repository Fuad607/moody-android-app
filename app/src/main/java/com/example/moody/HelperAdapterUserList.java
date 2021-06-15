package com.example.moody;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HelperAdapterUserList extends RecyclerView.Adapter {

    Context context;
    ArrayList<String> arrayList, contactedUserId;

    public HelperAdapterUserList(Context context, ArrayList<String> arrayList, ArrayList<String> contacted_user_id) {
        this.context = context;
        this.arrayList = arrayList;
        this.contactedUserId = contacted_user_id;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_lists_history, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolderClass viewHolderClass = (ViewHolderClass) viewHolder;
        viewHolderClass.textView.setText(arrayList.get(position));

        viewHolderClass.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context, "test", Toast.LENGTH_LONG).show();
            }
        });

        viewHolderClass.checkBox_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    viewHolderClass.m1.setVisibility(View.VISIBLE);
                    viewHolderClass.m2.setVisibility(View.VISIBLE);
                    viewHolderClass.m3.setVisibility(View.VISIBLE);
                    MainActivity.q3.put(contactedUserId.get(viewHolderClass.getAdapterPosition()), "");
                } else {
                    MainActivity.q3.remove(contactedUserId.get(viewHolderClass.getAdapterPosition()));
                    viewHolderClass.m1.setVisibility(View.INVISIBLE);
                    viewHolderClass.m2.setVisibility(View.INVISIBLE);
                    viewHolderClass.m3.setVisibility(View.INVISIBLE);
                }
            }
        });

        viewHolderClass.m1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolderClass.m1.getTag().equals(0)) {
                    viewHolderClass.m1.setBackgroundResource(R.color.button_selected);
                    // viewHolderClass.m1.setBackgroundColor(Color.parseColor("#3700B3"));
                    viewHolderClass.m1.setTag(1);
                    if (MainActivity.q3.containsKey(contactedUserId.get(viewHolderClass.getAdapterPosition()))) {
                        MainActivity.q3.put(contactedUserId.get(viewHolderClass.getAdapterPosition()), viewHolderClass.m1.getText().toString());
                    }
                    viewHolderClass.m2.setBackgroundResource(R.color.button_unselected);
                    viewHolderClass.m3.setBackgroundResource(R.color.button_unselected);
                } else {
                    viewHolderClass.m1.setTag(0);
                    viewHolderClass.m1.setBackgroundResource(R.color.button_unselected);
                }
            }
        });

        viewHolderClass.m2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolderClass.m2.getTag().equals(0)) {
                    viewHolderClass.m2.setBackgroundResource(R.color.button_selected);
                    // viewHolderClass.m2.setBackgroundColor(Color.parseColor("#3700B3"));
                    viewHolderClass.m2.setTag(1);
                    if (MainActivity.q3.containsKey(contactedUserId.get(viewHolderClass.getAdapterPosition()))) {
                        MainActivity.q3.put(contactedUserId.get(viewHolderClass.getAdapterPosition()), viewHolderClass.m2.getText().toString());
                    }
                    viewHolderClass.m1.setBackgroundResource(R.color.button_unselected);
                    viewHolderClass.m3.setBackgroundResource(R.color.button_unselected);
                } else {
                    viewHolderClass.m2.setTag(0);
                    viewHolderClass.m2.setBackgroundResource(R.color.button_unselected);
                }
            }
        });

        viewHolderClass.m3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolderClass.m3.getTag().equals(0)) {
                    viewHolderClass.m3.setBackgroundResource(R.color.button_selected);
                    // viewHolderClass.m3.setBackgroundColor(Color.parseColor("#3700B3"));
                    viewHolderClass.m3.setTag(1);
                    if (MainActivity.q3.containsKey(contactedUserId.get(viewHolderClass.getAdapterPosition()))) {
                        MainActivity.q3.put(contactedUserId.get(viewHolderClass.getAdapterPosition()), viewHolderClass.m3.getText().toString());
                    }
                    viewHolderClass.m1.setBackgroundResource(R.color.button_unselected);
                    viewHolderClass.m2.setBackgroundResource(R.color.button_unselected);
                } else {
                    viewHolderClass.m3.setTag(0);
                    viewHolderClass.m3.setBackgroundResource(R.color.button_unselected);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {
        TextView textView;
        CheckBox checkBox_select;
        Button m1, m2, m3;

        public ViewHolderClass(@NonNull @NotNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_user_name);
            checkBox_select = (CheckBox) itemView.findViewById(R.id.checkBox_select);
            m1 = (Button) itemView.findViewById(R.id.metting_type_1);
            m2 = (Button) itemView.findViewById(R.id.metting_type_2);
            m3 = (Button) itemView.findViewById(R.id.metting_type_3);
            m1.setTag(0);
            m2.setTag(0);
            m3.setTag(0);
        }
    }
}
