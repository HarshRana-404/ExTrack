package com.harsh.extrack.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.harsh.extrack.R;
import com.harsh.extrack.adapters.ExpenseAdapter;
import com.harsh.extrack.adapters.ExpenseContactsAdapter;
import com.harsh.extrack.models.ContactModel;
import com.harsh.extrack.models.ExpenseModel;
import com.harsh.extrack.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    RecyclerView rvExpenses, rvExpenseContacts;
    TextView tvNoExpenses;
    ExpenseAdapter adapterExpense;
    ExpenseContactsAdapter adapterExpenseContacts;
    ArrayList<ExpenseModel> alExpenses = new ArrayList<>();
    BottomSheetDialog bsAddExpense;
    Button btnAddExpense;
    EditText etExpenseName, etExpenseAmount;
    Spinner spContacts;
    static CheckBox cbSplitEqually;
    ImageButton ibClose;
    FloatingActionButton fabAddExpense;
    String expenseName, expenseAmount, expenseContact, expenseDateTime;
    ArrayList<String> alContacts = new ArrayList<>();
    public static ArrayList<ContactModel> alExpenseContacts = new ArrayList<>();
    Double userBalance=0.;
    int expenseAdded=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragHome = inflater.inflate(R.layout.fragment_home, container, false);

        try{
            rvExpenses = fragHome.findViewById(R.id.rv_expenses);
            tvNoExpenses = fragHome.findViewById(R.id.tv_no_expenses);
            fabAddExpense = fragHome.findViewById(R.id.fab_add_expense);
            rvExpenses.setLayoutManager(new LinearLayoutManager(requireContext()));
            adapterExpense = new ExpenseAdapter(requireContext(), alExpenses);
            adapterExpenseContacts = new ExpenseContactsAdapter(requireContext(), alExpenseContacts);
            rvExpenses.setAdapter(adapterExpense);
            bsAddExpense = new BottomSheetDialog(requireContext());
            getAllExpenses();

            rvExpenses.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(newState==RecyclerView.SCROLL_STATE_IDLE){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fabAddExpense.setVisibility(View.VISIBLE);
                            }
                        }, 1000);
                    }else{
                        fabAddExpense.setVisibility(View.GONE);
                    }
                }
            });

            fabAddExpense.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        View viewBSAddExpense = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_add_expense, null, false);

                        btnAddExpense = viewBSAddExpense.findViewById(R.id.btn_add_expense);
                        etExpenseName = viewBSAddExpense.findViewById(R.id.et_expense_name);
                        etExpenseAmount = viewBSAddExpense.findViewById(R.id.et_expense_amount);
                        spContacts = viewBSAddExpense.findViewById(R.id.sp_contacts);
                        cbSplitEqually = viewBSAddExpense.findViewById(R.id.cb_split_equally);
                        ibClose = viewBSAddExpense.findViewById(R.id.ib_bs_add_expense_close);
                        rvExpenseContacts = viewBSAddExpense.findViewById(R.id.rv_expense_contacts);
                        rvExpenseContacts.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
                        rvExpenseContacts.setAdapter(adapterExpenseContacts);
                        alExpenseContacts.clear();

                        btnAddExpense.setEnabled(false);
                        alContacts.clear();
                        alContacts.add("Select Contact!");
                        alContacts.add("Self");

                        spContacts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(!spContacts.getSelectedItem().toString().equals("Select Contact!")){
                                    Boolean contains=false;
                                    for(ContactModel contact : alExpenseContacts){
                                        if(contact.getPerson().equals(spContacts.getSelectedItem().toString())) {
                                            Toast.makeText(requireContext(), "This contact already added!", Toast.LENGTH_SHORT).show();
                                            contains=true;
                                        }
                                    }
                                    if(!contains){
                                        alExpenseContacts.add(new ContactModel(spContacts.getSelectedItem().toString(), 0.));
                                        adapterExpenseContacts.notifyDataSetChanged();
                                        checkExpenseContactsList();
                                    }
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });

                        Task<QuerySnapshot> qs = Constants.fbStore.collection("users").document(Constants.uID).collection("contacts").get();
                        qs.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                try{
                                    List<DocumentSnapshot> contacts = queryDocumentSnapshots.getDocuments();
                                    for(DocumentSnapshot contact : contacts){
                                        alContacts.add(contact.getString("name"));
                                    }
                                    spContacts.setAdapter(new ArrayAdapter<String>(requireContext(), R.layout.sp_contact_item, R.id.tv_contact_name, alContacts));
                                    btnAddExpense.setEnabled(true);
                                } catch (Exception e) {

                                }
                            }
                        });

                        btnAddExpense.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                expenseName = etExpenseName.getText().toString().trim();
                                expenseAmount = etExpenseAmount.getText().toString().trim();
                                if(cbSplitEqually.isChecked()){
                                    expenseAmount = String.valueOf(Math.round(Double.parseDouble(expenseAmount)/Double.parseDouble(String.valueOf(alExpenseContacts.size()))));
                                }
                                expenseContact = spContacts.getSelectedItem().toString().trim();

                                if(!expenseName.isEmpty() && !expenseAmount.isEmpty() && !alExpenseContacts.isEmpty()){
                                    for(ContactModel contact : alExpenseContacts){
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss", Locale.getDefault());
                                        expenseDateTime = sdf.format(Calendar.getInstance().getTime());
                                        HashMap<String, String> hmExpense = new HashMap<>();
                                        hmExpense.put("name", expenseName);
                                        hmExpense.put("amount", String.valueOf(Double.parseDouble(expenseAmount)));
                                        hmExpense.put("contact", contact.getPerson());
                                        hmExpense.put("type", "expend");
                                        hmExpense.put("datetime", expenseDateTime);
                                        Constants.fbStore.collection("users").document(Constants.uID).collection("expenses").document().set(hmExpense).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                expenseAdded++;
                                                if (expenseAdded == alExpenseContacts.size()) {
                                                    Toast.makeText(requireContext(), "Expense added successfully!", Toast.LENGTH_SHORT).show();
                                                    bsAddExpense.dismiss();
                                                    getAllExpenses();
                                                    alExpenseContacts.clear();
                                                }
                                                if(!contact.getPerson().equals("Self")) {
                                                    Task<QuerySnapshot> qs = Constants.fbStore.collection("users").document(Constants.uID).collection("contacts").whereEqualTo("name", contact.getPerson()).get();
                                                    qs.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            List<DocumentSnapshot> contact = queryDocumentSnapshots.getDocuments();
                                                            if (contact.size() == 1) {
                                                                userBalance = Double.parseDouble(contact.get(0).getString("balance"));
                                                                String docId = contact.get(0).getId();
                                                                userBalance += Double.parseDouble(expenseAmount);
                                                                Constants.fbStore.collection("users").document(Constants.uID).collection("contacts").document(docId).update("balance", String.valueOf(userBalance));
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }else{
                                    Toast.makeText(requireContext(), "Enter all details!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        ibClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bsAddExpense.dismiss();
                            }
                        });
                        bsAddExpense.setContentView(viewBSAddExpense);
                        bsAddExpense.show();
                    } catch (Exception e) {
                    }
                }
            });

        } catch (Exception e) {

        }
        return fragHome;
    }
    @SuppressLint("NotifyDataSetChanged")
    void getAllExpenses(){
        try{
            alExpenses.clear();
            Task<QuerySnapshot> qs = Constants.fbStore.collection("users").document(Constants.uID).collection("expenses").whereEqualTo("type", "expend").get();
            qs.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> expenses = queryDocumentSnapshots.getDocuments();
                    for(DocumentSnapshot expense : expenses){
                        alExpenses.add(new ExpenseModel(expense.getString("name"), expense.getString("contact"), Double.parseDouble(expense.getString("amount")), expense.getString("type"),expense.getString("datetime")));
                    }
                    if(alExpenses.isEmpty()){
                        tvNoExpenses.setVisibility(View.VISIBLE);
                    }else{
                        tvNoExpenses.setVisibility(View.GONE);
                        alExpenses.sort(Comparator.comparing(ExpenseModel::getDate).reversed());
                        adapterExpense.notifyDataSetChanged();
                    }
                }
            });
        } catch (Exception e) {
        }

    }
    public static void checkExpenseContactsList(){
        if(alExpenseContacts.size()>=2){
            cbSplitEqually.setVisibility(View.VISIBLE);
        }else{
            cbSplitEqually.setVisibility(View.GONE);
        }
    }
}