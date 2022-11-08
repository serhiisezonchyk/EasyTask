package com.example.taskorg;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNewTask  extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";

    private TextView setDeadlineDate;
    private EditText mTaskEdit;
    private Button mSaveBtn;
    private FirebaseFirestore firestore;
    private Context context;
    private String deadline_dateDate = "";
    private String id = "";
    private String deadline_dateDateUpdate = "";
    private FirebaseAuth mAuth;
    private String uid;

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_task , container , false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        setDeadlineDate = view.findViewById(R.id.set_deadline_date_tv);
        mTaskEdit = view.findViewById(R.id.task_edittext);
        mSaveBtn = view.findViewById(R.id.save_btn);

        firestore = FirebaseFirestore.getInstance();

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            id = bundle.getString("id");
            deadline_dateDateUpdate = bundle.getString("deadline_date");

            mTaskEdit.setText(task);
            setDeadlineDate.setText(deadline_dateDateUpdate);

            if (task.length() > 0){
                mSaveBtn.setEnabled(false);
                mSaveBtn.setBackgroundColor(Color.GRAY);
            }
        }

        mTaskEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")){
                    mSaveBtn.setEnabled(false);
                    mSaveBtn.setBackgroundColor(Color.GRAY);
                }else{
                    mSaveBtn.setEnabled(true);
                    mSaveBtn.setBackgroundColor(getResources().getColor(R.color.teal_700));
                    mSaveBtn.setTextColor(getResources().getColor(R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setDeadlineDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int MONTH = calendar.get(Calendar.MONTH);
            int YEAR = calendar.get(Calendar.YEAR);
            int DAY = calendar.get(Calendar.DATE);
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view1, year, month, dayOfMonth) -> {
                month = month + 1;
                setDeadlineDate.setText(dayOfMonth + "/" + month + "/" + year);
                deadline_dateDate = dayOfMonth + "/" + month +"/"+year;

            },YEAR,MONTH,DAY);
            datePickerDialog.show();
        });

        boolean finalIsUpdate = isUpdate;
        mSaveBtn.setOnClickListener(v -> {

            String task = mTaskEdit.getText().toString();

            if (finalIsUpdate){
                firestore.collection(uid).document(id).update("task" , task , "deadline_date" , deadline_dateDate);
                Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show();

            }
            else {
                if (task.isEmpty()) {
                    Toast.makeText(context, "Empty task not Allowed !!", Toast.LENGTH_SHORT).show();
                } else {

                    Map<String, Object> taskMap = new HashMap<>();

                    taskMap.put("task", task);
                    taskMap.put("deadline_date", deadline_dateDate);
                    taskMap.put("status", 0);
                    //taskMap.put("time",  FieldValue.serverTimestamp());

                    Date date = new Date();

                    SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
                    taskMap.put("date",  formatterDate.format(date));

                    SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm a");
                    taskMap.put("time",  formatterTime.format(date));

                    firestore.collection(uid).add(taskMap).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(context, "Task Saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
            dismiss();
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
        Activity activity = getActivity();
        if (activity instanceof  OnDialogCloseListener){
            ((OnDialogCloseListener)activity).onDialogClose(dialog);
        }
    }
}