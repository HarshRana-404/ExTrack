package com.harsh.extrack.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Authentication;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.harsh.extrack.AuthenticationActivity;
import com.harsh.extrack.R;
import com.harsh.extrack.utils.Constants;

import java.util.List;

public class ProfileFragment extends Fragment {

    TextView tvName, tvEmail, tvPersonalExpense, tvContactsCount;
    Button btnLogout, btnADNo, btnADYes, btnChangePassword;
    Double selfExpend=0.;
    int contactCount=0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragProfile = inflater.inflate(R.layout.fragment_profile, container, false);

        try{

            tvName = fragProfile.findViewById(R.id.tv_profile_name);
            tvEmail = fragProfile.findViewById(R.id.tv_profile_email);
            tvPersonalExpense = fragProfile.findViewById(R.id.tv_profile_self_expend);
            tvContactsCount = fragProfile.findViewById(R.id.tv_profile_contacts_count);
            btnLogout = fragProfile.findViewById(R.id.btn_profile_logout);
            btnChangePassword = fragProfile.findViewById(R.id.btn_profile_change_password);
            getProfileDetails();

            btnChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(requireContext());
                    View adView = LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog_change_password, null, false);
                    adb.setView(adView);
                    AlertDialog ad = adb.show();

                    btnADYes = adView.findViewById(R.id.btn_ad_change_password_yes);
                    btnADNo = adView.findViewById(R.id.btn_ad_change_password_no);

                    btnADYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Constants.fbAuth.sendPasswordResetEmail(Constants.fbAuth.getCurrentUser().getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(requireContext(), "Password reset link is sent, please check your e-mail!", Toast.LENGTH_SHORT).show();
                                    Constants.fbAuth.signOut();
                                    startActivity(new Intent(requireContext(), AuthenticationActivity.class));
                                    getActivity().finish();
                                }
                            });
                        }
                    });
                    btnADNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ad.dismiss();
                        }
                    });
                }
            });

            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(requireContext());
                    View adView = LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog_logout, null, false);
                    adb.setView(adView);
                    AlertDialog ad = adb.show();

                    btnADYes = adView.findViewById(R.id.btn_ad_logout_yes);
                    btnADNo = adView.findViewById(R.id.btn_ad_logout_no);

                    btnADYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Constants.fbAuth.signOut();
                            Toast.makeText(requireContext(), "Logged out!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(requireContext(), AuthenticationActivity.class));
                            getActivity().finish();
                        }
                    });
                    btnADNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ad.dismiss();
                        }
                    });
                }
            });

        } catch (Exception e) {

        }

        return fragProfile;
    }
    void getProfileDetails(){
        try{
            Task<DocumentSnapshot> person = Constants.fbStore.collection("users").document(Constants.uID).get();
            person.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    tvName.setText(documentSnapshot.getString("name"));
                    tvEmail.setText(documentSnapshot.getString("email"));
                }
            });
            Task<QuerySnapshot> qs = Constants.fbStore.collection("users").document(Constants.uID).collection("expenses").whereEqualTo("contact", "Self").get();
            qs.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> expenses = queryDocumentSnapshots.getDocuments();
                    for(DocumentSnapshot expense : expenses){
                        selfExpend+=Double.parseDouble(expense.getString("amount"));
                    }
                    tvPersonalExpense.setText("Self expense: ₹ "+selfExpend);
                }
            });
            Task<QuerySnapshot> qsContact = Constants.fbStore.collection("users").document(Constants.uID).collection("contacts").get();
            qsContact.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    contactCount = queryDocumentSnapshots.size();
                    tvContactsCount.setText("Contacts added: "+contactCount);
                }
            });

        } catch (Exception e) {

        }
    }
}