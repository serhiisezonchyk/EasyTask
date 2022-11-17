package com.example.taskorg.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskorg.R;
import com.example.taskorg.Utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ToDoAdapterReminderDialog extends RecyclerView.Adapter<ToDoAdapterReminderDialog.ToDoViewHolder> {
    private ArrayList<Date> list;
    private Date createDate;
    private Date deadlineDate;

    public ToDoAdapterReminderDialog(ArrayList<Date> list, Date createDate, Date deadlineDate) {
        this.list = list;
        this.createDate = createDate;
        this.deadlineDate = deadlineDate;
    }

    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_remind_date, parent, false);
        ToDoViewHolder vh = new ToDoViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoAdapterReminderDialog.ToDoViewHolder holder, int i) {
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy (HH:mm)");
        holder.mDate.setText(formatterDate.format(list.get(i)));
        holder.mDelete.setOnClickListener(view -> {
            list.remove(list.get(i));
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ToDoViewHolder extends RecyclerView.ViewHolder {

        public TextView mDate;
        public TextView mDelete;

        public ToDoViewHolder(View v) {
            super(v);
            mDate = v.findViewById(R.id.remind_date_tv);
            mDelete = v.findViewById(R.id.deleteTv);
        }
    }

    public ArrayList<Date> getList() {
        return list;
    }

    public boolean addNewDate(String date, String time) {
        Date createdRemindDate = DateUtil.getDateFromStrings(date, time);
        if (createDate == null) {
            createDate = new Date();
        }
        if (deadlineDate != null)
            if (!createdRemindDate.before(createDate) && !createdRemindDate.after(deadlineDate)) {
                list.add(DateUtil.getDateFromStrings(date, time));
                notifyItemInserted(list.size() - 1);
                notifyDataSetChanged();
                return true;
            } else {
                return false;
            }
        else {
            if (!createdRemindDate.before(createDate)) {
                list.add(DateUtil.getDateFromStrings(date, time));
                notifyItemInserted(list.size() - 1);
                notifyDataSetChanged();
                return true;
            } else {
                return false;
            }
        }
    }
}