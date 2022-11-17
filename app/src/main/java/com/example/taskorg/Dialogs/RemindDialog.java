package com.example.taskorg.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskorg.Adapters.ToDoAdapterReminderDialog;
import com.example.taskorg.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.Date;

public class RemindDialog extends Dialog implements View.OnClickListener {

    private DialogListener listener;

    public Activity activity;
    public Button yes, no;
    TextView title;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    EditText addDate, addTime;
    String remindDate, remindTime;

    FloatingActionButton fb;
    ToDoAdapterReminderDialog adapter;

    public RemindDialog(Activity a, ToDoAdapterReminderDialog adapter) {
        super(a);
        this.activity = a;
        this.adapter = adapter;
        setupLayout();
    }

    private void setupLayout() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_reminder);
        yes = (Button) findViewById(R.id.yes);
        no = (Button) findViewById(R.id.no);
        addDate = findViewById(R.id.set_deadline_date_tv);
        addTime = findViewById(R.id.set_deadline_time_tv);
        fb = findViewById(R.id.floatingActionButton);
        title = findViewById(R.id.title);
        recyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(activity);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        fb.setOnClickListener(view -> {
            remindDate = addDate.getText().toString();
            remindTime = addTime.getText().toString();
            if (remindDate.isEmpty() || remindTime.isEmpty()) {
                Toast.makeText(getContext(), "Remind date/time can`t be null", Toast.LENGTH_SHORT).show();
            } else {
                if (!adapter.addNewDate(remindDate, remindTime)) {
                    Toast.makeText(getContext(), "Remind date is earlier/later then created time/deadline", Toast.LENGTH_SHORT).show();
                }
            }
            recyclerView.setAdapter(adapter);
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yes: {
                ArrayList<Date> arr = adapter.getList();
                if (listener != null) {
                    listener.action(arr);
                }
                break;
            }
            case R.id.no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    public interface DialogListener {
        void action(ArrayList<Date> arr);
    }

    public void setDialogListener(DialogListener listener) {
        this.listener = listener;
    }
}
