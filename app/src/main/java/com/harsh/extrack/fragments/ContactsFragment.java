package com.harsh.extrack.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.harsh.extrack.R;
import com.harsh.extrack.adapters.ContactAdapter;
import com.harsh.extrack.models.ContactModel;
import com.harsh.extrack.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactsFragment extends Fragment {

    RecyclerView rvContacts;
    ContactAdapter adapterContact;
    ArrayList<ContactModel> alContacts = new ArrayList<>();
    FloatingActionButton fabAddContact;
    BottomSheetDialog bsAddContact;
    EditText etContactName;
    Button btnAddContact;
    ImageButton ibClose;
    String contactName="";
    TextView tvNoContacts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragContacts = inflater.inflate(R.layout.fragment_contacts, container, false);

        try{
            rvContacts = fragContacts.findViewById(R.id.rv_contacts);
            tvNoContacts = fragContacts.findViewById(R.id.tv_no_contacts);
            rvContacts.setLayoutManager(new LinearLayoutManager(requireContext()));
            adapterContact = new ContactAdapter(requireContext(), alContacts);
            rvContacts.setAdapter(adapterContact);
            fabAddContact = fragContacts.findViewById(R.id.fab_add_contact);
            bsAddContact = new BottomSheetDialog(requireContext());

            getContacts();

            rvContacts.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(newState==RecyclerView.SCROLL_STATE_IDLE){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fabAddContact.setVisibility(View.VISIBLE);
                            }
                        }, 1000);
                    }else{
                        fabAddContact.setVisibility(View.GONE);
                    }
                }
            });

            fabAddContact.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingInflatedId")
                @Override
                public void onClick(View v) {

                    View viewBSAddContact = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_add_contact, null ,false);

                    etContactName = viewBSAddContact.findViewById(R.id.et_contact);
                    btnAddContact = viewBSAddContact.findViewById(R.id.btn_add_contact);
                    ibClose = viewBSAddContact.findViewById(R.id.ib_bs_add_contact_close);

                    btnAddContact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            contactName = etContactName.getText().toString().trim();
                            if(!contactName.isEmpty()){
                                if(!contactName.equalsIgnoreCase("Self")){
                                    if(!contactExists()){
                                        HashMap<String, String> hmContact = new HashMap<>();
                                        hmContact.put("name", contactName);
                                        hmContact.put("balance", "0.");
                                        Constants.fbStore.collection("users").document(Constants.uID).collection("contacts").document().set(hmContact).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(requireContext(), "Person added successfully!", Toast.LENGTH_SHORT).show();
                                                getContacts();
                                                bsAddContact.dismiss();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(requireContext(), "Person with this name already exists!", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(requireContext(), "You cannot add 'Self' as a contact!", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(requireContext(), "Enter person name!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    ibClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bsAddContact.dismiss();
                        }
                    });

                    bsAddContact.setContentView(viewBSAddContact);
                    bsAddContact.show();
                }
            });

        } catch (Exception e) {

        }

        return fragContacts;
    }

    @SuppressLint("NotifyDataSetChanged")
    void getContacts(){
        alContacts.clear();
        Task<QuerySnapshot> qs = Constants.fbStore.collection("users").document(Constants.uID).collection("contacts").get();
        qs.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> contacts = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot contact : contacts){
                    alContacts.add(new ContactModel(contact.getString("name"), Double.parseDouble(contact.getString("balance"))));
                }
                if(alContacts.isEmpty()){
                    tvNoContacts.setVisibility(View.VISIBLE);
                }else{
                    tvNoContacts.setVisibility(View.GONE);
                    adapterContact.notifyDataSetChanged();
                }
            }
        });
    }
    Boolean contactExists(){
        for(ContactModel contact : alContacts){
            if(contact.getPerson().toLowerCase().equals(contactName.toLowerCase())){
                return true;
            }
        }
        return false;
    }

}