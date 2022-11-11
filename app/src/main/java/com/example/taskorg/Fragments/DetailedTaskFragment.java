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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.taskorg.Model.TaskModel;
import com.example.taskorg.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class DetailedTaskFragment extends Fragment {
    private TaskModel model;
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

        final Bundle bundle = getArguments();
        model = (TaskModel) bundle.getSerializable("model");
        ProgressBar pb = view.findViewById(R.id.progress);
        CheckBox mCheckBoxDet = view.findViewById(R.id.task_checkbox);
        TextView mStartDateTextView = view.findViewById(R.id.creation_date_textview);
        TextView mEndDateTextView = view.findViewById(R.id.deadline_date_textview);
        TextView mCategoryTextView = view.findViewById(R.id.textViewCategory);
        TextView mRemindTextView = view.findViewById(R.id.remind_tv);
        TextView mAddressTextView = view.findViewById(R.id.address_tv);
        mAddressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String st = mAddressTextView.getText().toString().replaceAll("\\s+","+");
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/" + st));
                startActivity(intent);
            }
        });
        TextView mBeforeTextView = view.findViewById(R.id.after_tv);
        TextView mKeywordsTextView = view.findViewById(R.id.keywords_tv);
        TextView mDescriptionTextView = view.findViewById(R.id.description_tv);


        mCheckBoxDet.setChecked(toBoolean(model.getStatus()));
        mCheckBoxDet.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                firestore.collection(uid).document(model.TaskId).update("status", 1);
            } else {
                firestore.collection(uid).document(model.TaskId).update("status", 0);
            }
        });
        mCheckBoxDet.setText(model.getTask());
        mStartDateTextView.setText(model.getCreate_date() +" ("+model.getCreate_time()+")");
        mEndDateTextView.setText(model.getDeadline_date() +" ("+model.getDeadline_time()+")");
        mCategoryTextView.setText(model.getCategory());
        mRemindTextView.setText("Remind");
        mAddressTextView.setText(model.getAddress());
        mBeforeTextView.setText("Before");
        mKeywordsTextView.setText(model.getKeywords());
        mDescriptionTextView.setText(model.getDescription());

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

    public static DetailedTaskFragment newInstance() {
        DetailedTaskFragment fragment = new DetailedTaskFragment();
        return fragment;
    }

    private boolean toBoolean(int status) {
        return status != 0;
    }

}
