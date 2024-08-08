package com.harsh.extrack.fragments;

import android.annotation.SuppressLint;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.QuerySnapshot;
import com.harsh.extrack.AuthenticationActivity;
import com.harsh.extrack.MainActivity;
import com.harsh.extrack.R;
import com.harsh.extrack.utils.Constants;

public class LoginFragment extends Fragment {

    TextView tvNotAUser, tvForgotPassword;
    EditText etEmail, etPassword;
    Button btnLogin;
    String email="", password="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragLogin = inflater.inflate(R.layout.fragment_login, container, false);

        try{

            tvNotAUser = fragLogin.findViewById(R.id.tv_not_a_user);
            tvForgotPassword = fragLogin.findViewById(R.id.tv_forgot_password);
            etEmail = fragLogin.findViewById(R.id.et_email);
            etPassword = fragLogin.findViewById(R.id.et_password);
            btnLogin = fragLogin.findViewById(R.id.btn_login);
            tvNotAUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AuthenticationActivity.setFragment(1);
                }
            });
            tvForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    email = etEmail.getText().toString().trim();
                    if(!email.isEmpty()){
                        try{
                            Task<QuerySnapshot> qs = Constants.fbStore.collection("users").whereEqualTo("email", email).get();
                            qs.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(queryDocumentSnapshots.isEmpty()){
                                        Toast.makeText(requireContext(), "This email is not registered!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Constants.fbAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(requireContext(), "Password reset link is sent, please check your e-mail!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        } catch (Exception e) {

                        }
                    }else{
                        Toast.makeText(requireContext(), "Enter an e-mail address!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(validDetails()){
                        Constants.fbAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Constants.init();
                                Toast.makeText(requireContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(requireContext(), MainActivity.class));
                                getActivity().finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(), "Login failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Toast.makeText(requireContext(), "Enter all required details!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {

        }

        return fragLogin;
    }
    Boolean validDetails(){
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        if(!email.isEmpty() && !password.isEmpty()){
            return true;
        }
        return false;
    }
}