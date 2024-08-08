package com.harsh.extrack.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.harsh.extrack.HistoryActivity;
import com.harsh.extrack.R;
import com.harsh.extrack.models.ContactModel;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    ArrayList<ContactModel> alContact;
    Context context;

    public ContactAdapter(Context context, ArrayList<ContactModel> alContact){
        this.context = context;
        this.alContact = alContact;
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewExpense = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        ViewHolder vh = new ViewHolder(viewExpense);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {
        try{
            ContactModel expense = alContact.get(position);

            holder.tvContactName.setText(expense.getPerson());
            Double balance = expense.getBalance();
            if(balance<0){
                balance = Math.abs(balance);
                holder.tvBalance.setText("₹ "+balance.toString());
                holder.tvBalance.setTextColor(context.getColor(R.color.red));
            }else{
                holder.tvBalance.setText("₹ "+balance.toString());
                holder.tvBalance.setTextColor(context.getColor(R.color.green));
            }
            if(balance==0.){
                holder.tvBalance.setTextColor(context.getColor(R.color.fg));
            }

            holder.cvContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HistoryActivity.lookUpContactName = expense.getPerson();
                    context.startActivity(new Intent(context, HistoryActivity.class));
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
        TextView tvContactName, tvBalance;
        CardView cvContact;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cvContact = itemView.findViewById(R.id.cv_contact);
            tvContactName = itemView.findViewById(R.id.tv_contact_name);
            tvBalance = itemView.findViewById(R.id.tv_contact_balance);
        }
    }
}
