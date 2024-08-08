package com.harsh.extrack.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.harsh.extrack.R;
import com.harsh.extrack.models.ExpenseModel;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    ArrayList<ExpenseModel> alExpenses;
    Context context;

    public ExpenseAdapter(Context context, ArrayList<ExpenseModel> alExpenses){
        this.context = context;
        this.alExpenses = alExpenses;
    }

    @NonNull
    @Override
    public ExpenseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewExpense = LayoutInflater.from(context).inflate(R.layout.expense_item, parent, false);
        ViewHolder vh = new ViewHolder(viewExpense);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ViewHolder holder, int position) {
        try{
            ExpenseModel expense = alExpenses.get(position);

            holder.tvExpName.setText(expense.getName());
            holder.tvExpPerson.setText(expense.getPerson());
            holder.tvExpAmount.setText("₹ "+expense.getAmount().toString());

            String temp[] = expense.getDate().split("@");
            String dbDate[] = temp[0].split("-");
            String dbTime[] = temp[1].split(":");
            String time = dbTime[0]+":"+dbTime[1];
            String date = dbDate[2]+"-"+dbDate[1]+"-"+dbDate[0]+" · "+time;

            holder.tvExpDate.setText(date);

        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        return alExpenses.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvExpName, tvExpAmount, tvExpPerson, tvExpDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExpName = itemView.findViewById(R.id.tv_exp_name);
            tvExpPerson = itemView.findViewById(R.id.tv_exp_person);
            tvExpAmount = itemView.findViewById(R.id.tv_exp_amount);
            tvExpDate = itemView.findViewById(R.id.tv_exp_date);
        }
    }
}
