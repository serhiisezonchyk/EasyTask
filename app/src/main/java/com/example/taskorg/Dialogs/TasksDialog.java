package com.example.taskorg.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskorg.Adapters.ToDoAdapterDialog;
import com.example.taskorg.Model.TaskModel;
import com.example.taskorg.R;

import java.util.List;

public class TasksDialog extends Dialog implements View.OnClickListener {

    private DialogListener listener;

    public TasksDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public TasksDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public Activity activity;
    public Dialog dialog;
    public Button yes, no;
    TextView title;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter adapter;


    public TasksDialog(Activity a, RecyclerView.Adapter adapter) {
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
        setContentView(R.layout.dialog_tasks);
        yes = (Button) findViewById(R.id.yes);
        no = (Button) findViewById(R.id.no);
        title = findViewById(R.id.title);
        recyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(adapter);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yes: {
                List<TaskModel> arr = ((ToDoAdapterDialog) adapter).getList();
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
        void action(List<TaskModel> arr);
    }

    public void setDialogListener(DialogListener listener) {
        this.listener = listener;
    }
}