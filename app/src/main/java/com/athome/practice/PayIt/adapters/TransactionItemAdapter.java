package com.athome.practice.PayIt.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.athome.practice.PayIt.R;
import com.athome.practice.PayIt.beans.Transaction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by Anand Singh on 19/11/2017.
 */
public class TransactionItemAdapter extends RecyclerView.Adapter<TransactionItemAdapter.ViewHolder> {

    private DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());
    private DateFormat timeFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());

    private List<Transaction> transactions;
    private ItemClickListener itemClickListener;

    public TransactionItemAdapter(List<Transaction> objects, @NonNull ItemClickListener itemClickListener) {
        this.transactions = objects;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        Context context = viewGroup.getContext();
        View parent = LayoutInflater.from(context).inflate(R.layout.transaction_list_item, viewGroup, false);
        return ViewHolder.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Transaction transaction = transactions.get(position);
        viewHolder.setItemsName(transaction.getItemsName());
        viewHolder.setBeneficiaryName(transaction.getBeneficiaryName());
        viewHolder.setTotalAmount(transaction.getTotalAmount());
        viewHolder.setDate(dateFormat.format(new Date(transaction.getDate())));
        viewHolder.setTime(timeFormat.format(new Date(transaction.getDate())));
        viewHolder.setTransactionId(transaction.getTransactionId());
        viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(transaction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void add(List<Transaction> newTransactions) {
        transactions.addAll(newTransactions);
        notifyDataSetChanged();
    }

    public void clear() {
        transactions.clear();
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClicked(Transaction transaction);
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder {
        private final View parent;
        private final TextView itemsName;
        private final TextView beneficiary_name;
        private final TextView totalAmount;
        private final TextView date;
        private final TextView time;
        private final TextView transactionId;

        public static ViewHolder newInstance(View parent) {
            TextView itemsName = (TextView) parent.findViewById(R.id.items_name);
            TextView beneficiary_name = (TextView) parent.findViewById(R.id.beneficiary_name);
            TextView totalAmount = (TextView) parent.findViewById(R.id.total_amount);
            TextView date = (TextView) parent.findViewById(R.id.date);
            TextView time = (TextView) parent.findViewById(R.id.time);
            TextView transactionId = (TextView) parent.findViewById(R.id.transaction_id);

            return new ViewHolder(parent, itemsName, beneficiary_name, totalAmount, date, time, transactionId);
        }

        private ViewHolder(View parent, TextView itemsName, TextView beneficiary_name, TextView totalAmount, TextView date, TextView time, TextView transactionId) {
            super(parent);
            this.parent = parent;
            this.itemsName = itemsName;
            this.beneficiary_name = beneficiary_name;
            this.totalAmount = totalAmount;
            this.date = date;
            this.time = time;
            this.transactionId = transactionId;
        }

        public void setItemsName(CharSequence text) {
            itemsName.setText(text);
        }

        public void setBeneficiaryName(CharSequence text) {
            beneficiary_name.setText(text);
        }

        public void setTotalAmount(CharSequence text) {
            totalAmount.setText(text);
        }

        public void setDate(CharSequence text) {
            date.setText(text);
        }

        public void setTime(CharSequence text) {
            time.setText(text);
        }

        public void setTransactionId(long id) {
            transactionId.setText(Long.toString(id));
        }

        public void setOnClickListener(View.OnClickListener listener) {
            parent.setOnClickListener(listener);
        }
    }
}