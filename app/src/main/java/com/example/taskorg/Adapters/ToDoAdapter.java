package com.example.taskorg.Adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskorg.Activities.MainActivity;
import com.example.taskorg.Fragments.DataFragment;
import com.example.taskorg.Fragments.DetailedTaskFragment;
import com.example.taskorg.Model.TaskModel;
import com.example.taskorg.R;
import com.example.taskorg.Utils.DateUtil;
import com.example.taskorg.Vars.GlobalVar;
import com.google.android.gms.common.util.DataUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private List<TaskModel> list;
    private MainActivity activity;
    private FirebaseFirestore firestore;
    private String uid;

    public ToDoAdapter(MainActivity activity, List<TaskModel> list, String uid) {
        this.list = list;
        this.activity = activity;
        this.uid = uid;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        //Display settings (card, list)
        if (GlobalVar.listState == false)
            view = LayoutInflater.from(activity).inflate(R.layout.each_task_card, parent, false);
        else
            view = LayoutInflater.from(activity).inflate(R.layout.each_task, parent, false);
        firestore = FirebaseFirestore.getInstance();
        return new MyViewHolder(view);
    }

    public void deleteTask(int position) {
        TaskModel model = list.get(position);
        firestore.collection(uid).document(model.TaskId).delete();
        list.remove(position);
        notifyItemRemoved(position);
    }

    public Context getContext() {
        return activity;
    }

    public void editTask(int position) {
        //Get current task
        TaskModel model = list.get(position);

        //Set args
        Bundle bundle = new Bundle();
        bundle.putString("task", model.getTask());
        bundle.putString("deadline_date", model.getDeadline_date());
        bundle.putString("deadline_time", model.getDeadline_time());
        bundle.putBoolean("important", model.getImportant());
        bundle.putString("address", model.getAddress());
        bundle.putString("id", model.TaskId);
        bundle.putString("category", model.getCategory());
        bundle.putString("keywords", model.getKeywords());
        bundle.putString("description", model.getDescription());
        bundle.putStringArrayList("tasksBefore", model.getTasksBefore());

        //Open Data fragment with info about current task
        DataFragment fragment = DataFragment.newInstance(this, list);
        fragment.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Creating each one task on recycler view
        TaskModel model = list.get(position);

        //Set Task name
        holder.mCheckBox.setText(model.getTask());

        //Set deadline textView
        if (model.getDeadline_date().isEmpty() || model.getDeadline_time().isEmpty())
            holder.mDeadlineDateTv.setText("No deadline specified.");
        else
            holder.mDeadlineDateTv.setText("Deadline date:  " + model.getDeadline_date() + " (" + model.getDeadline_time() + ")");

        //Set icon that task is important
        if (model.getImportant())
            holder.mCheckBox.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_alert_24, 0, 0, 0);
        holder.mCheckBox.setChecked(toBoolean(model.getStatus()));

        //Checkbox listener
        holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (model.getTasksBefore().isEmpty()) {
                //If dependent tasks don`t exist simple changing of status
                if (isChecked) {
                    model.setStatus(1);
                    firestore.collection(uid).document(model.TaskId).update("status", 1);
                } else {
                    model.setStatus(0);
                    firestore.collection(uid).document(model.TaskId).update("status", 0);
                }
            } else {
                //Build toast with dependent tasks names
                StringBuilder sb = new StringBuilder();
                int counter = 0;
                sb.append("COMPLETE NEXT TASKS TO COMPLETE THIS ONE:");
                for (String key : model.getTasksBefore()) {
                    for (TaskModel object : list) {
                        if (key.equals(object.getId()) && object.getStatus() == 0) {
                            sb.append("\n" + object.getTask());
                            counter++;
                        }
                    }
                }
                //If dependent tasks is complete
                if (counter == 0) {
                    if (isChecked) {
                        model.setStatus(1);
                        firestore.collection(uid).document(model.TaskId).update("status", 1);
                    } else {
                        model.setStatus(0);
                        firestore.collection(uid).document(model.TaskId).update("status", 0);
                    }
                }
                //Notify if dependent tasks is uncompleted
                else {
                    Toast.makeText(getContext(), sb.toString(), Toast.LENGTH_SHORT).show();
                    holder.mCheckBox.setChecked(false);
                }
            }
        });

        //Long click listener on task
        holder.itemView.setOnLongClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("model", model);
            DetailedTaskFragment fragment = DetailedTaskFragment.newInstance(list);
            fragment.setArguments(bundle);
            activity.getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            return true;
        });

        if (GlobalVar.listState == false)
            holder.progressBar.setProgress(DateUtil.getPercentTimeLeft(model.getCreate_date(), model.getCreate_time(), model.getDeadline_date(), model.getDeadline_time()));

    }

    private boolean toBoolean(int status) {
        return status != 0;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mDeadlineDateTv;
        CheckBox mCheckBox;
        ProgressBar progressBar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mDeadlineDateTv = itemView.findViewById(R.id.deadline_date_date_tv);
            mCheckBox = itemView.findViewById(R.id.mcheckbox);
            if (GlobalVar.listState == false)
                progressBar = itemView.findViewById(R.id.progress);
        }
    }
}