package com.harsh.extrack.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.harsh.extrack.R;
import com.harsh.extrack.fragments.HomeFragment;
import com.harsh.extrack.models.ContactModel;

import java.util.ArrayList;

public class ExpenseContactsAdapter extends RecyclerView.Adapter<ExpenseContactsAdapter.ViewHolder> {

    ArrayList<ContactModel> alContact;
    Context context;

    public ExpenseContactsAdapter(Context context, ArrayList<ContactModel> alContact){
        this.context = context;
        this.alContact = alContact;
    }

    @NonNull
    @Override
    public ExpenseContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewExpense = LayoutInflater.from(context).inflate(R.layout.expense_contact_item, parent, false);
        ViewHolder vh = new ViewHolder(viewExpense);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ExpenseContactsAdapter.ViewHolder holder, int position) {
        try{
            ContactModel expense = alContact.get(position);
            holder.tvContactName.setText(expense.getPerson());
            holder.ibDelete.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
                    HomeFragment.alExpenseContacts.remove(expense);
                    notifyDataSetChanged();
                    HomeFragment.checkExpenseContactsList();
                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        return alContact.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvContactName;
        ImageButton ibDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tv_expense_contact_name);
            ibDelete = itemView.findViewById(R.id.ib_expense_contact_delete);
        }
    }
}
