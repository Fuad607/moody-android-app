package com.example.moody;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HelperLogAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<String> arrayListTime, arrayListMood,arrayListRelax;

    public HelperLogAdapter(Context context, ArrayList<String> arrayListTime,ArrayList<String> arrayListMood, ArrayList<String> arrayListRelax) {
        this.context = context;
        this.arrayListTime = arrayListTime;
        this.arrayListMood = arrayListMood;
        this.arrayListRelax = arrayListRelax;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_view, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolderClass viewHolderClass = (ViewHolderClass) viewHolder;
        viewHolderClass.textViewDate.setText(arrayListTime.get(position));
        viewHolderClass.textViewMood.setText(arrayListMood.get(position));
        viewHolderClass.textViewRelax.setText(arrayListRelax.get(position));

        viewHolderClass.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context, "test", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListTime.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {
        TextView textViewDate,textViewMood,textViewRelax;

        public ViewHolderClass(@NonNull @NotNull View itemView) {
            super(itemView);
            textViewDate = (TextView) itemView.findViewById(R.id.log_user_survey_date);
            textViewMood = (TextView) itemView.findViewById(R.id.log_user_mood);
            textViewRelax = (TextView) itemView.findViewById(R.id.log_user_relaxation);
        }
    }
}
