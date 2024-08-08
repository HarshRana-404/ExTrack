package com.harsh.extrack.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.harsh.extrack.R;
import com.harsh.extrack.models.ExpenseModel;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    ArrayList<ExpenseModel> alExpenses;
    Context context;

    public HistoryAdapter(Context context, ArrayList<ExpenseModel> alExpenses){
        this.context = context;
        this.alExpenses = alExpenses;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewExpense = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        ViewHolder vh = new ViewHolder(viewExpense);
        return vh;
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        try{
            ExpenseModel expense = alExpenses.get(position);

            holder.tvExpName.setText(expense.getName());
            holder.tvExpAmount.setText("₹ "+expense.getAmount().toString());

            if(expense.getType().equals("receive")){
                holder.ivType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_receive));
            }else if(expense.getType().equals("expend")){
                holder.ivType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_collect));
            }
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
        TextView tvExpName, tvExpAmount, tvExpDate;
        ImageView ivType;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExpName = itemView.findViewById(R.id.tv_history_exp_name);
            tvExpAmount = itemView.findViewById(R.id.tv_history_exp_amount);
            tvExpDate = itemView.findViewById(R.id.tv_history_exp_date);
            ivType = itemView.findViewById(R.id.iv_history_type);
        }
    }
}
