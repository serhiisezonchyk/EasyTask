package com.example.taskorg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.taskorg.Auth.AuthFragment;
import com.example.taskorg.Fragments.ViewListFragment;
import com.example.taskorg.R;
import com.example.taskorg.Vars.GlobalVar;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            ViewListFragment fragment = new ViewListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
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
        } else if (item.getItemId() == R.id.action_view_state) {
            GlobalVar.listState = !GlobalVar.listState;

            if (GlobalVar.listState)
                item.setTitle("View by cards");
            else
                item.setTitle("View by list");
            ViewListFragment fragment = new ViewListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
        return super.onOptionsItemSelected(item);
    }
}