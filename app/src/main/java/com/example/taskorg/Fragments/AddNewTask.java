package com.example.taskorg.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.taskorg.Adapters.ToDoAdapter;
import com.example.taskorg.Listeners.OnDialogCloseListener;
import com.example.taskorg.R;
import com.example.taskorg.Utils.DateUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";

    private TextView setDeadlineDate;
    private TextView setDeadlineTime;
    private CheckBox checkBox;
    private EditText mTaskEdit;
    private String deadline_dateDate = "";
    private String deadline_timeTime = "";
    private String addressStr = "";

    private FirebaseFirestore firestore;
    private Context context;
    private String id = "";
    private String uid;
    private final ToDoAdapter adapter;

    public AddNewTask(ToDoAdapter adapter) {
        this.adapter = adapter;
    }

    public static AddNewTask newInstance(ToDoAdapter adapter) {
        return new AddNewTask(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_task, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        setDeadlineDate = view.findViewById(R.id.set_deadline_date_tv);
        setDeadlineTime = view.findViewById(R.id.set_deadline_time_tv);
        checkBox = view.findViewById(R.id.checkboxImportance);
        mTaskEdit = view.findViewById(R.id.task_edittext);
        Button mSaveBtn = view.findViewById(R.id.save_btn);

        firestore = FirebaseFirestore.getInstance();

        setDeadlineDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int MONTH = calendar.get(Calendar.MONTH);
            int YEAR = calendar.get(Calendar.YEAR);
            int DAY = calendar.get(Calendar.DATE);
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view1, year, month, dayOfMonth) -> {
                month = month + 1;
                setDeadlineDate.setText(dayOfMonth + "/" + month + "/" + year);
                deadline_dateDate = dayOfMonth + "/" + month + "/" + year;
                if (deadline_dateDate.equals(DAY + "/" + month + "/" + YEAR)) {
                    setDeadlineTime.setText("23:59");
                    deadline_timeTime = "23:59";
                } else {
                    setDeadlineTime.setText("12:0");
                    deadline_timeTime = "12:0";
                }
            }, YEAR, MONTH, DAY);
            datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
            datePickerDialog.show();
        });

        setDeadlineTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            int MONTH = calendar.get(Calendar.MONTH);
            int YEAR = calendar.get(Calendar.YEAR);
            int DAY = calendar.get(Calendar.DATE);

            int HOUR;
            int MIN;

            if (deadline_dateDate.equals("") || deadline_dateDate.equals(DAY + "/" + MONTH + "/" + YEAR)) {
                HOUR = calendar.get(Calendar.HOUR_OF_DAY);
                MIN = calendar.get(Calendar.MINUTE);
            } else {
                HOUR = 0;
                MIN = 0;
            }
            TimePickerDialog timePickerDialog;
            timePickerDialog = new TimePickerDialog(context, (timePicker, selectedHour, selectedMinute) -> {
                setDeadlineTime.setText(selectedHour + ":" + selectedMinute);
                deadline_timeTime = selectedHour + ":" + selectedMinute;

                if (deadline_dateDate.equals("")) {
                    calendar.add(Calendar.DATE, 1);
                    setDeadlineDate.setText(calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.MONTH) + 1 + "/" + calendar.get(Calendar.YEAR));
                    deadline_dateDate = calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.MONTH) + 1 + "/" + calendar.get(Calendar.YEAR);
                }
            }, HOUR + 2, MIN, true);
            timePickerDialog.setTitle("Select Time");
            timePickerDialog.show();
        });


        mSaveBtn.setOnClickListener(v -> {

            String task = mTaskEdit.getText().toString();
            if (task.isEmpty()) {
                Toast.makeText(context, "Empty task not Allowed !!", Toast.LENGTH_SHORT).show();
            } else {

                Map<String, Object> taskMap = new HashMap<>();

                taskMap.put("task", task);
                taskMap.put("deadline_date", deadline_dateDate);
                taskMap.put("deadline_time", deadline_timeTime);
                taskMap.put("status", 0);
                taskMap.put("time", FieldValue.serverTimestamp());
                taskMap.put("important", checkBox.isChecked());
                taskMap.put("address", "");
                taskMap.put("category", "Any");
                taskMap.put("keywords", "");
                taskMap.put("description", "");
                taskMap.put("tasksBefore", new ArrayList<String>());

                Date date = new Date();
                date = DateUtil.addDays(date, 1);

                SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
                taskMap.put("create_date", formatterDate.format(date));

                SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm");

                taskMap.put("create_time", formatterTime.format(date));

                firestore.collection(uid).add(taskMap).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(context, "Task Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            adapter.notifyDataSetChanged();
            dismiss();
            ViewListFragment fragment = new ViewListFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        ViewListFragment fragment = new ViewListFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListener) {
            ((OnDialogCloseListener) activity).onDialogClose(dialog);
        }
    }
}