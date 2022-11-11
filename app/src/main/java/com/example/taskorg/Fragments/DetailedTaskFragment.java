package com.example.taskorg.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.taskorg.Model.TaskModel;
import com.example.taskorg.R;
import com.example.taskorg.Utils.DateUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class DetailedTaskFragment extends Fragment {
    private TaskModel model;
    private List<TaskModel> mList;
    private FloatingActionButton mFab;
    private FirebaseFirestore firestore;
    private String uid;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        firestore = FirebaseFirestore.getInstance();
        mFab = view.findViewById(R.id.floatingActionButton);

        //Get args
        final Bundle bundle = getArguments();
        model = (TaskModel) bundle.getSerializable("model");

        //Create&set percentage to progress bar
        ProgressBar pb = view.findViewById(R.id.progress);
        pb.setProgress(DateUtil.getPercentTimeLeft(model.getCreate_date(), model.getCreate_time(), model.getDeadline_date(), model.getDeadline_time()));

        //Initialize buttons
        CheckBox mCheckBoxDet = view.findViewById(R.id.task_checkbox);
        TextView mStartDateTextView = view.findViewById(R.id.creation_date_textview);
        TextView mEndDateTextView = view.findViewById(R.id.deadline_date_textview);
        TextView mCategoryTextView = view.findViewById(R.id.textViewCategory);
        TextView mRemindTextView = view.findViewById(R.id.remind_tv);
        TextView mAddressTextView = view.findViewById(R.id.address_tv);
        TextView mBeforeTextView = view.findViewById(R.id.after_tv);
        TextView mKeywordsTextView = view.findViewById(R.id.keywords_tv);
        TextView mDescriptionTextView = view.findViewById(R.id.description_tv);

        //Task checkbox settings detailed info fragment
        mCheckBoxDet.setChecked(toBoolean(model.getStatus()));
        mCheckBoxDet.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (model.getTasksBefore().isEmpty()) {
                if (isChecked) {
                    model.setStatus(1);
                    firestore.collection(uid).document(model.TaskId).update("status", 1);
                } else {
                    model.setStatus(0);
                    firestore.collection(uid).document(model.TaskId).update("status", 0);
                }
            } else {
                StringBuilder sb = new StringBuilder();
                int counter = 0;
                sb.append("COMPLETE NEXT TASKS TO COMPLETE THIS ONE:");
                for (String key : model.getTasksBefore()) {
                    for (TaskModel object : mList) {
                        if (key.equals(object.getId()) && object.getStatus() == 0) {
                            sb.append("\n" + object.getTask());
                            counter++;
                        }
                    }
                }
                if (counter == 0) {
                    if (isChecked) {
                        model.setStatus(1);
                        firestore.collection(uid).document(model.TaskId).update("status", 1);
                    } else {
                        model.setStatus(0);
                        firestore.collection(uid).document(model.TaskId).update("status", 0);
                    }
                } else {
                    Toast.makeText(getContext(), sb.toString(), Toast.LENGTH_SHORT).show();
                    mCheckBoxDet.setChecked(false);
                }
            }
        });
        mCheckBoxDet.setText(model.getTask());

        //Set icon that task is important
        if (model.getImportant())
            mCheckBoxDet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_alert_24, 0, 0, 0);

        //Task category settings detailed info fragment
        mCategoryTextView.setText(model.getCategory());

        //Task creating date settings detailed info fragment
        mStartDateTextView.setText(model.getCreate_date() + " (" + model.getCreate_time() + ")");

        //Task deadline date settings detailed info fragment
        if (model.getDeadline_date().isEmpty() && model.getDeadline_time().isEmpty()) {
            mEndDateTextView.setText("No deadline");
        } else {
            mEndDateTextView.setText(model.getDeadline_date() + " (" + model.getDeadline_time() + ")");
        }

        //Task reminder settings detailed info fragment
        mRemindTextView.setText("Remind");

        //Task address settings detailed info fragment
        if (model.getAddress().isEmpty()) {
            mAddressTextView.setText("No address");
        } else {
            mAddressTextView.setText(model.getAddress());
            mAddressTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String st = mAddressTextView.getText().toString().replaceAll("\\s+", "+");
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/" + st));
                    startActivity(intent);
                }
            });
        }

        //Task conditions settings detailed info fragment
        StringBuilder sb = new StringBuilder();
        if (model.getTasksBefore().isEmpty())
            sb.append("Tasks that must be complete before this one not found");
        else {
            sb.append("Tasks that must be complete before this one:");
            for (String key : model.getTasksBefore()) {
                for (TaskModel model : mList) {
                    if (model.getId().equals(key)) {
                        sb.append("\n\t\t\t-" + model.getTask());
                    }
                }
            }
        }
        mBeforeTextView.setText(sb.toString());

        //Task keywords settings detailed info fragment
        if (model.getKeywords().isEmpty()) {
            mKeywordsTextView.setText("No keywords");
        } else {
            mKeywordsTextView.setText(model.getKeywords());
        }

        //Task description settings detailed info fragment
        if (model.getKeywords().isEmpty()) {
            mDescriptionTextView.setText("No description to this task");
        } else {
            mDescriptionTextView.setText(model.getDescription());
        }

        //Floating button listener
        mFab.setOnClickListener(v -> {
            ViewListFragment fragment = new ViewListFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detailed_task, container, false);
    }

    public DetailedTaskFragment(List<TaskModel> mList) {
        this.mList = mList;
    }

    public static DetailedTaskFragment newInstance(List<TaskModel> mList) {
        return new DetailedTaskFragment(mList);
    }

    private boolean toBoolean(int status) {
        return status != 0;
    }


}
