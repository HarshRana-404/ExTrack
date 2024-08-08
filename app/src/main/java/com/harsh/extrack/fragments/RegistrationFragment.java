package com.harsh.extrack.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.harsh.extrack.AuthenticationActivity;
import com.harsh.extrack.MainActivity;
import com.harsh.extrack.R;
import com.harsh.extrack.utils.Constants;

import java.util.HashMap;

public class RegistrationFragment extends Fragment {

    TextView tvAlreadyAUser;
    EditText etName, etEmail, etPassword;
    Button btnRegister;
    String name="", email="", password="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragRegistration = inflater.inflate(R.layout.fragment_registration, container, false);

        try{
            etName = fragRegistration.findViewById(R.id.et_name);
            etEmail = fragRegistration.findViewById(R.id.et_email);
            etPassword = fragRegistration.findViewById(R.id.et_password);
            btnRegister = fragRegistration.findViewById(R.id.btn_register);
            tvAlreadyAUser = fragRegistration.findViewById(R.id.tv_already_a_user);
            tvAlreadyAUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AuthenticationActivity.setFragment(0);
                }
            });

            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(validDetails()){
                        Constants.fbAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                HashMap<String, String> hmUser = new HashMap<>();
                                hmUser.put("name", name);
                                hmUser.put("email", email);
                                Constants.fbStore.collection("users").document(Constants.fbAuth.getUid()).set(hmUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Constants.init();
                                        Toast.makeText(requireContext(), "Registration Successful!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(requireContext(), MainActivity.class));
                                        getActivity().finish();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(), "Registration failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Toast.makeText(requireContext(), "Enter all required details!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {

        }

        return fragRegistration;
    }
    Boolean validDetails(){
        name = etName.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            return true;
        }
        return false;
    }
}