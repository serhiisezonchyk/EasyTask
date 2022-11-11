package com.example.taskorg.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskorg.Model.TaskModel;
import com.example.taskorg.R;

import java.util.ArrayList;
import java.util.List;

public class ToDoAdapterDialog extends RecyclerView.Adapter<ToDoAdapterDialog.ToDoViewHolder> {
    private List<TaskModel> list;
    private List<TaskModel> listReturn = new ArrayList<>();
    private List<TaskModel> tasksBefore ;
    private String currentTaskId = null;
    RecyclerViewItemClickListener recyclerViewItemClickListener;

    public ToDoAdapterDialog(List<TaskModel> list, RecyclerViewItemClickListener listener, List<TaskModel> tasksBefore, String currentTaskId) {
        this.list = list;
        this.tasksBefore = tasksBefore;
        this.currentTaskId = currentTaskId;
        this.recyclerViewItemClickListener = listener;
    }

    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_task_dialog, parent, false);

        ToDoViewHolder vh = new ToDoViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull ToDoAdapterDialog.ToDoViewHolder todoViewHolder, int i) {
        if(!currentTaskId.equals(list.get(i).getId())) {

            todoViewHolder.mCheckBox.setText(list.get(i).getTask());
            for (TaskModel key : tasksBefore) {
                if (key.getId().equals(list.get(i).getId())) {
                    todoViewHolder.mCheckBox.setChecked(true);
                    listReturn.add(list.get(i));
                }
            }
            todoViewHolder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    TaskModel model = list.get(i);
                    listReturn.add(model);
                } else {
                    TaskModel model = list.get(i);
                    listReturn.remove(model);
                }
            });

        }else{
            todoViewHolder.mCheckBox.setText(list.get(i).getTask());
            todoViewHolder.mCheckBox.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ToDoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CheckBox mCheckBox;

        public ToDoViewHolder(View v) {
            super(v);
            mCheckBox = v.findViewById(R.id.mcheckbox);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            recyclerViewItemClickListener.clickOnItem(list.get(this.getAdapterPosition()));
        }
    }

    public interface RecyclerViewItemClickListener {
        void clickOnItem(TaskModel data);
    }

    public List<TaskModel> getList() {
        return listReturn;
    }
}