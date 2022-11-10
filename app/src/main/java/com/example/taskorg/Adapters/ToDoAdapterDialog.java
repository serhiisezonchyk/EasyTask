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
    RecyclerViewItemClickListener recyclerViewItemClickListener;

    public ToDoAdapterDialog(List<TaskModel> list, RecyclerViewItemClickListener listener) {
        this.list = list;
        this.recyclerViewItemClickListener = listener;
    }

    //    private List<TaskModel>  createNewList(List<TaskModel>  list){
//        List<TaskModel> newList = new ArrayList<>();
//        for (TaskModel task:list) {
//            if(!task.equals(currenTask))
//                newList.add(task);
//        }
//        return newList;
//    }
    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_task_dialog, parent, false);

        ToDoViewHolder vh = new ToDoViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull ToDoAdapterDialog.ToDoViewHolder todoViewHolder, int i) {
        todoViewHolder.mCheckBox.setText(list.get(i).getTask());
        todoViewHolder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                TaskModel model = list.get(i);
                listReturn.add(model);
            } else {
                TaskModel model = list.get(i);
                listReturn.remove(model);
            }
        });
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