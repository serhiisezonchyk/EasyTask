package com.example.taskorg.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.taskorg.R;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (savedInstanceState == null) {
            AuthFragment authFragment = new AuthFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, authFragment)
                    .commit();
        }
    }

}