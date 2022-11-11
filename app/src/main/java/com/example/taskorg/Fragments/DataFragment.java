package com.example.taskorg.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.taskorg.Adapters.ToDoAdapter;
import com.example.taskorg.Adapters.ToDoAdapterDialog;
import com.example.taskorg.Dialogs.TasksDialog;
import com.example.taskorg.Model.TaskModel;
import com.example.taskorg.R;
import com.example.taskorg.Utils.DateUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFragment extends Fragment {

    public static final String TAG = "AddNewTask";
    private List<TaskModel> mList;

    private TextView setDeadlineDate;
    private TextView setDeadlineTime;
    private CheckBox checkBox;
    private EditText mTaskEdit;
    private EditText mAddressEdit;
    private Spinner mCategorySpinner;
    private EditText mKeywordsEdit;
    private EditText mDescriptionEdit;

    private String deadline_dateDate = "";
    private String deadline_timeTime = "";
    private String addressStr = "";
    private String categoryStr = "";
    private String keywordsStr = "";
    private String descriptionStr = "";
    private ArrayList<String> tasksBeforeStr = new ArrayList<>();
    private List<TaskModel> tasksBefore = new ArrayList<>();
    private TextView setTasksBefore;

    private FirebaseFirestore firestore;
    private Context context;
    private String id = "";
    private String uid;
    private ToDoAdapter adapter;

    private FloatingActionButton mFab;

    public DataFragment(ToDoAdapter adapter, List<TaskModel> mList) {
        this.adapter = adapter;
        this.mList = mList;
    }

    public static DataFragment newInstance(ToDoAdapter adapter, List<TaskModel> mList) {
        return new DataFragment(adapter, mList);
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
        mAddressEdit = view.findViewById(R.id.address_et);
        mCategorySpinner = view.findViewById(R.id.category_spinner);
        mKeywordsEdit = view.findViewById(R.id.keywords_et);
        mDescriptionEdit = view.findViewById(R.id.description_et);
        setTasksBefore = view.findViewById(R.id.after_et);

        Button mSaveBtn = view.findViewById(R.id.save_btn);
        mFab = view.findViewById(R.id.floatingActionButton);
        setTasksBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToDoAdapterDialog dataAdapter = new ToDoAdapterDialog(mList, data -> {}, tasksBefore, id);
                TasksDialog customDialog = new TasksDialog(getActivity(), dataAdapter);
                customDialog.setDialogListener(new TasksDialog.DialogListener() {
                    @Override
                    public void action(List<TaskModel> arr) {
                        tasksBefore.clear();
                        tasksBefore = arr;

                        StringBuilder output = new StringBuilder();
                        output.append("Do after: ");
                        for (TaskModel model : tasksBefore) {
                            output.append(model.getTask() + " ");
                        }
                        setTasksBefore.setText(arr.isEmpty() ? "Click to chose tasks" : output);
                    }
                });
                customDialog.show();
                customDialog.setCanceledOnTouchOutside(false);
            }
        });

        firestore = FirebaseFirestore.getInstance();

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            id = bundle.getString("id");
            deadline_dateDate = bundle.getString("deadline_date");
            deadline_timeTime = bundle.getString("deadline_time");
            boolean important = bundle.getBoolean("important");
            addressStr = bundle.getString("address");
            categoryStr = bundle.getString("category");
            keywordsStr = bundle.getString("keywords");
            descriptionStr = bundle.getString("description");
            tasksBeforeStr = bundle.getStringArrayList("tasksBefore");

            mTaskEdit.setText(task);
            mAddressEdit.setText(addressStr);
            mCategorySpinner.setSelection(Arrays.asList(getResources().getStringArray(R.array.categories)).indexOf(categoryStr));
            mKeywordsEdit.setText(keywordsStr);
            mDescriptionEdit.setText(descriptionStr);
            setDeadlineDate.setText(deadline_dateDate);
            setDeadlineTime.setText(deadline_timeTime);
            checkBox.setChecked(important);

            StringBuilder sbTasksBefore = new StringBuilder();
            sbTasksBefore.append("Do after: ");
            int counter = 0;
            if (!tasksBeforeStr.isEmpty())
                for (String key : tasksBeforeStr) {
                    for (TaskModel model : mList) {
                        if (key.equals(model.getId())) {
                            tasksBefore.add(model);
                            sbTasksBefore.append(model.getTask() + " ");
                            counter++;
                        }
                    }
                }
            setTasksBefore.setText(counter == 0 ? "Click to chose tasks" : sbTasksBefore.toString());

        }
//
//        mTaskEdit.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.toString().equals("")){
//                    mSaveBtn.setEnabled(false);
//                    mSaveBtn.setBackgroundColor(Color.GRAY);
//                }else{
//                    mSaveBtn.setEnabled(true);
//                    mSaveBtn.setBackgroundColor(getResources().getColor(R.color.teal_700));
//                    mSaveBtn.setTextColor(getResources().getColor(R.color.white));
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

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
            timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    setDeadlineTime.setText(selectedHour + ":" + selectedMinute);
                    deadline_timeTime = selectedHour + ":" + selectedMinute;

                    if (deadline_dateDate.equals("")) {
                        calendar.add(Calendar.DATE, 1);
                        setDeadlineDate.setText(calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.MONTH) + 1 + "/" + calendar.get(Calendar.YEAR));
                        deadline_dateDate = calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.MONTH) + 1 + "/" + calendar.get(Calendar.YEAR);
                    }
                }
            }, HOUR + 2, MIN, true);
            timePickerDialog.setTitle("Select Time");
            timePickerDialog.show();
        });


        boolean finalIsUpdate = isUpdate;
        mSaveBtn.setOnClickListener(v -> {

            String task = mTaskEdit.getText().toString();
            addressStr = mAddressEdit.getText().toString();
            categoryStr = mCategorySpinner.getSelectedItem().toString().isEmpty() ?
                    "Any" : mCategorySpinner.getSelectedItem().toString();
            keywordsStr = mKeywordsEdit.getText().toString();
            descriptionStr = mDescriptionEdit.getText().toString();

            tasksBeforeStr.clear();
            for (TaskModel model : tasksBefore) {
                tasksBeforeStr.add(model.getId());
            }

            if (finalIsUpdate) {
                firestore.collection(uid).document(id).update("task", task,
                        "deadline_date", deadline_dateDate,
                        "deadline_time", deadline_timeTime,
                        "important", checkBox.isChecked(),
                        "address", addressStr,
                        "category", categoryStr,
                        "keywords", keywordsStr,
                        "description", descriptionStr,
                        "tasksBefore", tasksBeforeStr);
                Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show();

            } else {
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
                    taskMap.put("address", addressStr);
                    taskMap.put("category", categoryStr);
                    taskMap.put("keywords", keywordsStr);
                    taskMap.put("description", descriptionStr);
                    taskMap.put("tasksBefore", tasksBeforeStr);

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
            }
            adapter.notifyDataSetChanged();
            ViewListFragment fragment = new ViewListFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        });
        mFab.setOnClickListener(v -> {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data, container, false);
    }
}
