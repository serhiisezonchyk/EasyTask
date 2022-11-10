package com.example.taskorg.Auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taskorg.R;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText etEmail;
    private EditText etPass;
    private EditText etPass2;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        etEmail = view.findViewById(R.id.et_email);
        etPass = view.findViewById(R.id.et_pass);
        etPass2 = view.findViewById(R.id.et_repeatpass);

        Button btnBack = view.findViewById(R.id.btn_back);
        Button btnRegistration = view.findViewById(R.id.btn_registration);
        btnBack.setOnClickListener(view1 -> {
            AuthFragment authFragment = AuthFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragmentContainer1, authFragment)
                    .commit();
        });

        btnRegistration.setOnClickListener(view12 -> {
            if (etPass.getText().toString().equals(etPass2.getText().toString())
                    && !etEmail.getText().toString().isEmpty()
                    && !etPass.getText().toString().isEmpty())
                reg(etEmail.getText().toString(), etPass.getText().toString());
            else {
                Toast.makeText(getActivity(), "Pass not same", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    public static RegistrationFragment newInstance() {
        RegistrationFragment registrationFragment = new RegistrationFragment();
        return registrationFragment;
    }

    public void reg(String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful())
                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                });
    }
}