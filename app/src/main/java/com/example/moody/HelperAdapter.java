package com.example.moody;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HelperAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList arrayList,
            arrayListName;

    public HelperAdapter(Context context, ArrayList arrayListName){
        this.context=context;
      //  this.arrayList=arrayList;
        this.arrayListName=arrayListName;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_relationship,parent,false);
        ViewHolderClass viewHolderClass=new ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolderClass viewHolderClass=(ViewHolderClass)viewHolder;
        viewHolderClass.textView.setText(UserRelationship.names[position]);

        viewHolderClass.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"test",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListName.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder{
        TextView textView;
        public ViewHolderClass(@NonNull @NotNull View itemView) {
            super(itemView);
            textView=(TextView)itemView.findViewById(R.id.tv_user_name);
        }
    }
}
