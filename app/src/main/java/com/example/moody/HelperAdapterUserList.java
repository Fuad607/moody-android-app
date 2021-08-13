package com.example.moody;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HelperAdapterUserList extends RecyclerView.Adapter {

    Context context;
    ArrayList<String> arrayList, contactedUserId;
    private RvClickListener clickListener;
    public List<String> checkedFriends = new ArrayList<>();

    public HelperAdapterUserList(Context context, ArrayList<String> arrayList, ArrayList<String> contacted_user_id) {
        this.context = context;
        this.arrayList = arrayList;
        this.contactedUserId = contacted_user_id;
    }

    public void setClickListener(RvClickListener clickListener) {
        this.clickListener = clickListener;
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

        viewHolderClass.checkBox_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((ViewHolderClass) viewHolder).checkBox_select.setChecked(setChecked(contactedUserId.get(position)));
                String commaseparatedlist = checkedFriends.toString();
                commaseparatedlist
                        = commaseparatedlist.replace("[", "")
                        .replace("]", "")
                        .replace(" ", "");
                clickListener.onClick(commaseparatedlist);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolderClass extends RecyclerView.ViewHolder {
        TextView textView;
        CheckBox checkBox_select;

        public ViewHolderClass(@NonNull @NotNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.user_name_history);
            checkBox_select = (CheckBox) itemView.findViewById(R.id.checkbox_select_history);
        }
    }

    private boolean setChecked(String id) {
        if (checkedFriends.contains(id)) {
            checkedFriends.remove(id);
            return false;
        } else {
            checkedFriends.add(id);
            return true;
        }
    }
}
