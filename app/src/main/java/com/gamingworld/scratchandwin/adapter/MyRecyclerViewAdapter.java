package com.gamingworld.scratchandwin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamingworld.scratchandwin.R;
import com.gamingworld.scratchandwin.util.TransactionDetail;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<TransactionDetail> transactions;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public MyRecyclerViewAdapter(Context context, List<TransactionDetail> transactions) {
        this.mInflater = LayoutInflater.from(context);
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapter.ViewHolder holder, int position) {
        int coins = transactions.get(position).getTranCoins();
        String status = transactions.get(position).getTranStatus();

        holder.redeemSeq.setText("" + (position + 1) + ".");
        holder.redeemAmount.setText("" + coins);
        holder.redeemStatus.setText(status);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView redeemSeq, redeemAmount, redeemStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            redeemSeq = itemView.findViewById(R.id.redeem_seq);
            redeemAmount = itemView.findViewById(R.id.redeem_amount);
            redeemStatus = itemView.findViewById(R.id.redeem_status);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public TransactionDetail getItem(int id) {
        return transactions.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
