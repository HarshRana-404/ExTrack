package com.harsh.extrack;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.harsh.extrack.adapters.HistoryAdapter;
import com.harsh.extrack.models.ExpenseModel;
import com.harsh.extrack.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    public static String lookUpContactName="";
    TextView tvBalance, tvContactName;
    RecyclerView rvHistory;
    Button btnReceive, btnExpend;
    HistoryAdapter adapterHistory;
    ArrayList<ExpenseModel> alExpenses = new ArrayList<>();
    BottomSheetDialog bsReceive, bsExpend;
    EditText etExpName, etExpAmount, etExpendName, etExpendAmount;
    Button btnBSReceive, btnBSExpend;
    String expenseName="", expenseAmount="", expenseDateTime;
    ImageButton ibBack, ibReceiveClose, ibExpendClose;
    Double userBalance=0.;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        try{
            getWindow().setNavigationBarColor(getResources().getColor(R.color.bg));

            tvContactName = findViewById(R.id.tv_history_contact_name);
            tvBalance = findViewById(R.id.tv_history_balance);
            ibBack = findViewById(R.id.ib_history_back);
            rvHistory = findViewById(R.id.rv_history);
            btnReceive = findViewById(R.id.btn_history_receive);
            btnExpend = findViewById(R.id.btn_history_expend);
            getHistory();

            tvContactName.setText(lookUpContactName);
            adapterHistory = new HistoryAdapter(HistoryActivity.this, alExpenses);
            rvHistory.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
            rvHistory.setAdapter(adapterHistory);
            bsReceive = new BottomSheetDialog(HistoryActivity.this);
            bsExpend = new BottomSheetDialog(HistoryActivity.this);

            btnReceive.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingInflatedId")
                @Override
                public void onClick(View v) {
                    try{
                        View viewBSReceive = LayoutInflater.from(HistoryActivity.this).inflate(R.layout.bottom_sheet_receive, null, false);

                        etExpName = viewBSReceive.findViewById(R.id.et_receive_expense_name);
                        etExpAmount = viewBSReceive.findViewById(R.id.et_receive_expense_amount);
                        btnBSReceive = viewBSReceive.findViewById(R.id.btn_receive);
                        ibReceiveClose = viewBSReceive.findViewById(R.id.ib_receive_bs_close);

                        btnBSReceive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                expenseName = etExpName.getText().toString().trim();
                                expenseAmount = etExpAmount.getText().toString().trim();
                                if(!expenseName.isEmpty() && !expenseAmount.isEmpty()){
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss", Locale.getDefault());
                                    expenseDateTime = sdf.format(Calendar.getInstance().getTime());
                                    HashMap<String, String> hmExpense = new HashMap<>();
                                    hmExpense.put("name", expenseName);
                                    hmExpense.put("amount", String.valueOf(Double.parseDouble(expenseAmount)));
                                    hmExpense.put("contact", lookUpContactName);
                                    hmExpense.put("type", "receive");
                                    hmExpense.put("datetime", expenseDateTime);
                                    Constants.fbStore.collection("users").document(Constants.uID).collection("expenses").document().set(hmExpense).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Task<QuerySnapshot> qs = Constants.fbStore.collection("users").document(Constants.uID).collection("contacts").whereEqualTo("name", lookUpContactName).get();
                                            qs.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    List<DocumentSnapshot> contact = queryDocumentSnapshots.getDocuments();
                                                    if(contact.size()==1){
                                                        userBalance = Double.parseDouble(contact.get(0).getString("balance"));
                                                        String docId = contact.get(0).getId();
                                                        userBalance-=Double.parseDouble(expenseAmount);
                                                        Constants.fbStore.collection("users").document(Constants.uID).collection("contacts").document(docId).update("balance", String.valueOf(userBalance)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(HistoryActivity.this, "Expense added successfully!", Toast.LENGTH_SHORT).show();
                                                                getHistory();
                                                                bsReceive.dismiss();
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }else{
                                    Toast.makeText(HistoryActivity.this, "Enter all details!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        ibReceiveClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bsReceive.dismiss();
                            }
                        });

                        bsReceive.setContentView(viewBSReceive);
                        bsReceive.getWindow().setNavigationBarColor(getResources().getColor(R.color.bg));
                        bsReceive.show();
                    } catch (Exception e) {
                        Log.d("dalle", e.toString());
                    }
                }
            });
            btnExpend.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingInflatedId")
                @Override
                public void onClick(View v) {
                    try{
                        View viewBSExpend = LayoutInflater.from(HistoryActivity.this).inflate(R.layout.bottom_sheet_expend, null, false);

                        etExpendName = viewBSExpend.findViewById(R.id.et_expend_expense_name);
                        etExpendAmount = viewBSExpend.findViewById(R.id.et_expend_expense_amount);
                        btnBSExpend = viewBSExpend.findViewById(R.id.btn_expend);
                        ibExpendClose = viewBSExpend.findViewById(R.id.ib_expend_bs_close);

                        btnBSExpend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                expenseName = etExpendName.getText().toString().trim();
                                expenseAmount = etExpendAmount.getText().toString().trim();
                                if(!expenseName.isEmpty() && !expenseAmount.isEmpty()){
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss", Locale.getDefault());
                                    expenseDateTime = sdf.format(Calendar.getInstance().getTime());
                                    HashMap<String, String> hmExpense = new HashMap<>();
                                    hmExpense.put("name", expenseName);
                                    hmExpense.put("amount", String.valueOf(Double.parseDouble(expenseAmount)));
                                    hmExpense.put("contact", lookUpContactName);
                                    hmExpense.put("type", "expend");
                                    hmExpense.put("datetime", expenseDateTime);
                                    Constants.fbStore.collection("users").document(Constants.uID).collection("expenses").document().set(hmExpense).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Task<QuerySnapshot> qs = Constants.fbStore.collection("users").document(Constants.uID).collection("contacts").whereEqualTo("name", lookUpContactName).get();
                                            qs.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    List<DocumentSnapshot> contact = queryDocumentSnapshots.getDocuments();
                                                    if(contact.size()==1){
                                                        userBalance = Double.parseDouble(contact.get(0).getString("balance"));
                                                        String docId = contact.get(0).getId();
                                                        userBalance+=Double.parseDouble(expenseAmount);
                                                        Constants.fbStore.collection("users").document(Constants.uID).collection("contacts").document(docId).update("balance", String.valueOf(userBalance)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(HistoryActivity.this, "Expense added successfully!", Toast.LENGTH_SHORT).show();
                                                                getHistory();
                                                                bsExpend.dismiss();
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }else{
                                    Toast.makeText(HistoryActivity.this, "Enter all details!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        ibExpendClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bsExpend.dismiss();
                            }
                        });

                        bsExpend.setContentView(viewBSExpend);
                        bsExpend.getWindow().setNavigationBarColor(getResources().getColor(R.color.bg));
                        bsExpend.show();
                    } catch (Exception e) {
                        Log.d("dalle", e.toString());
                    }
                }
            });

            ibBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

        } catch (Exception e) {

        }

    }

    void getHistory(){
        alExpenses.clear();
        Task<QuerySnapshot> qsContact = Constants.fbStore.collection("users").document(Constants.uID).collection("contacts").whereEqualTo("name", lookUpContactName).get();
        qsContact.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> contacts = queryDocumentSnapshots.getDocuments();
                Double balance = Double.parseDouble(contacts.get(0).getString("balance"));
                if(balance<0){
                    balance = Math.abs(balance);
                    tvBalance.setText("Pay: ₹ "+balance.toString());
                    tvBalance.setTextColor(getColor(R.color.red));
                }else{
                    tvBalance.setText("Receive ₹ "+balance.toString());
                    tvBalance.setTextColor(getColor(R.color.green));
                }
                if(balance==0.){
                    tvBalance.setText("₹ "+balance.toString());
                    tvBalance.setTextColor(getColor(R.color.fg));
                }
            }
        });
        Task<QuerySnapshot> qs = Constants.fbStore.collection("users").document(Constants.uID).collection("expenses").whereEqualTo("contact", lookUpContactName).get();
        qs.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> expenses = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot expense : expenses){
                    alExpenses.add(new ExpenseModel(expense.getString("name"), expense.getString("contact"), Double.parseDouble(expense.getString("amount")), expense.getString("type"), expense.getString("datetime")));
                }
                alExpenses.sort(Comparator.comparing(ExpenseModel::getDate).reversed());
                adapterHistory.notifyDataSetChanged();
            }
        });
    }
}