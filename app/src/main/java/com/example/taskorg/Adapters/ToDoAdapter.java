package com.example.taskorg.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskorg.AddNewTask;
import com.example.taskorg.MainActivity;
import com.example.taskorg.Model.TaskModel;
import com.example.taskorg.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private List<TaskModel> list;
    private MainActivity activity;
    private FirebaseFirestore firestore;
    private String uid;

    public ToDoAdapter(MainActivity activity, List<TaskModel> list, String uid){
        this.list = list;
        this.activity = activity;
        this.uid = uid;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.each_task , parent , false);
        firestore = FirebaseFirestore.getInstance();
        return new MyViewHolder(view);
    }

    public void deleteTask(int position){
        TaskModel model = list.get(position);
        firestore.collection(uid).document(model.TaskId).delete();
        list.remove(position);
        notifyItemRemoved(position);
    }
    public Context getContext(){
        return activity;
    }
    public void editTask(int position){
        TaskModel model = list.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("task" , model.getTask());
        bundle.putString("deadline_date" , model.getDeadline_date());
        bundle.putString("id" , model.TaskId);

        AddNewTask addNewTask = new AddNewTask();
        addNewTask.setArguments(bundle);
        addNewTask.show(activity.getSupportFragmentManager() , addNewTask.getTag());
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TaskModel model = list.get(position);
        holder.mCheckBox.setText(model.getTask());

        holder.mDeadlineDateTv.setText("Deadline date:  " + model.getDeadline_date());

        holder.mCheckBox.setChecked(toBoolean(model.getStatus()));

        holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                firestore.collection(uid).document(model.TaskId).update("status" , 1);
            }else{
                firestore.collection(uid).document(model.TaskId).update("status" , 0);
            }
        });

    }

    private boolean toBoolean(int status){
        return status != 0;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mDeadlineDateTv;
        CheckBox mCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mDeadlineDateTv = itemView.findViewById(R.id.deadline_date_date_tv);
            mCheckBox = itemView.findViewById(R.id.mcheckbox);
        }
    }
}