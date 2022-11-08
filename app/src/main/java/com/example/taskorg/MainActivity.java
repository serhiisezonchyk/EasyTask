package com.example.taskorg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.taskorg.Adapters.ToDoAdapter;
import com.example.taskorg.Auth.AuthFragment;
import com.example.taskorg.Model.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements OnDialogCloseListener{

    private RecyclerView recyclerView;
    private FloatingActionButton mFab;
    private FirebaseFirestore firestore;
    private ToDoAdapter adapter;
    private List<TaskModel> mList;
    private Query query;
    private ListenerRegistration listenerRegistration;

    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        recyclerView = findViewById(R.id.recycerlview);
        mFab = findViewById(R.id.floatingActionButton);
        firestore = FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        mFab.setOnClickListener(v -> AddNewTask.newInstance().show(getSupportFragmentManager() , AddNewTask.TAG));

        mList = new ArrayList<>();
        adapter = new ToDoAdapter(MainActivity.this , mList, mAuth.getCurrentUser().getUid());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        showData();
        recyclerView.setAdapter(adapter);
    }
    @SuppressLint("NotifyDataSetChanged")
    private void showData(){
        query = firestore.collection(uid).orderBy("time" , Query.Direction.DESCENDING);

        listenerRegistration = query.addSnapshotListener((value, error) -> {
            if(value!=null){
                for ( DocumentChange documentChange : value.getDocumentChanges()){
                if (documentChange.getType() == DocumentChange.Type.ADDED){
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            AuthFragment authFragment = new AuthFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, authFragment)
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}