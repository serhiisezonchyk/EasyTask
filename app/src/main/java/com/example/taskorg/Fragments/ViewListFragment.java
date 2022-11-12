package com.example.taskorg.Fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskorg.Adapters.ToDoAdapter;
import com.example.taskorg.Activities.MainActivity;
import com.example.taskorg.Model.TaskModel;
import com.example.taskorg.Listeners.OnDialogCloseListener;
import com.example.taskorg.R;
import com.example.taskorg.Utils.TouchHelper;
import com.example.taskorg.Vars.GlobalVar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

//Fragment with recycler view
public class ViewListFragment extends Fragment implements OnDialogCloseListener {
    private FirebaseFirestore firestore;
    private ToDoAdapter adapter;
    private List<TaskModel> mList;
    private Query query;
    private ListenerRegistration listenerRegistration;

    private String uid;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Current user uid
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        RecyclerView recyclerView = view.findViewById(R.id.recycerlview);
        FloatingActionButton mFab = view.findViewById(R.id.floatingActionButton);

        TabLayout tabLayout = view.findViewById(R.id.toolBar);
        for (String button: Arrays.asList(getResources().getStringArray(R.array.categories))) {
            TabLayout.Tab item = tabLayout.newTab().setText(button);
            tabLayout.addTab(item);
            if(GlobalVar.categoryToShow.equals(button)){
                item.select();
            }
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5: {
                        GlobalVar.categoryToShow = tab.getText().toString();
                        showData();
                        recyclerView.setAdapter(adapter);
                        break;
                    }
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        firestore = FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);
        if (GlobalVar.listState == false)
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        else
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Listener to default and long click
        mFab.setOnClickListener(v -> AddNewTask.newInstance(adapter).show(getFragmentManager(), AddNewTask.TAG));
        mFab.setOnLongClickListener((View.OnLongClickListener) view1 -> {
            DataFragment fragment = DataFragment.newInstance(adapter, mList);
            getFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            return true;
        });


        mList = new ArrayList<>();

        //Create adapter with touch helper
        adapter = new ToDoAdapter((MainActivity) getActivity(), mList, mAuth.getCurrentUser().getUid());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        showData();
        recyclerView.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_list, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showData() {
        mList.clear();
        if(GlobalVar.categoryToShow.equals("Any"))
            query = firestore.collection(uid).orderBy("time", Query.Direction.DESCENDING);
        else
            query = firestore.collection(uid).whereEqualTo("category", GlobalVar.categoryToShow).orderBy("time", Query.Direction.DESCENDING);
        listenerRegistration = query.addSnapshotListener((value, error) -> {
            if (value != null) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        String id = documentChange.getDocument().getId();
                        TaskModel taskModel = documentChange.getDocument().toObject(TaskModel.class).withId(id);
                        mList.add(taskModel);
                        adapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        mList.clear();
        showData();
        adapter.notifyDataSetChanged();
    }
}
