package com.athome.practice.PayIt.activities;

import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.athome.practice.PayIt.Constants;
import com.athome.practice.PayIt.R;
import com.athome.practice.PayIt.adapters.TransactionItemAdapter;
import com.athome.practice.PayIt.beans.Token;
import com.athome.practice.PayIt.beans.Transaction;
import com.athome.practice.PayIt.dto.OrderItem;
import com.athome.practice.PayIt.helpers.ConnectionHelperReceiver;
import com.github.ivbaranov.mli.MaterialLetterIcon;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity implements TransactionItemAdapter.ItemClickListener,ConnectionHelperReceiver.ConnHelperReceiverCallback {

    private RecyclerView recyclerView;
    private TransactionItemAdapter transactionItemAdapter;
    private List<Transaction> allTransactions;
    private Token token;
    private ConnectionHelperReceiver connectionHelperReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        token = createToken();
        connectionHelperReceiver = new ConnectionHelperReceiver(this, this, token);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionHelperReceiver.openSocket();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        transactionItemAdapter = new TransactionItemAdapter(getAllTransactions(), this);
        recyclerView.setAdapter(transactionItemAdapter);
    }

    @Override
    public void onItemClicked(Transaction transaction) {
        Intent intent = new Intent(TransactionHistoryActivity.this, TransactionListItemDetailActivity.class);
        intent.putExtra(Constants.TRANSACTION_ITEM,transaction);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        connectionHelperReceiver.unRegisterService();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        connectionHelperReceiver.openSocket();
    }

    private Token createToken() {
        //Logic to receive receiver Id and generate Transaction ID
        //dummy
        Token token = new Token("renuka123");
        return token;
    }

    private void refreshTransactionList(ArrayList<Transaction> transactions){

        Log.e(Constants.TAG, "Refreshing List");

        if(transactionItemAdapter!=null) {
            transactionItemAdapter.notifyDataSetChanged();
        } else {
            transactionItemAdapter = new TransactionItemAdapter(getAllTransactions(), this);
        }
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.transactions);
        relativeLayout.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();

        for(Transaction trans: transactions ){
            View view = inflater.inflate(R.layout.transaction_list_item, null);
            MaterialLetterIcon letter = (MaterialLetterIcon) view.findViewById(R.id.letter);
            TextView items = (TextView) view.findViewById(R.id.items_name);
            TextView beneficiary = (TextView) view.findViewById(R.id.beneficiary_name);
            TextView totalAmount = (TextView) view.findViewById(R.id.total_amount);
            TextView date = (TextView) view.findViewById(R.id.date);
            TextView time = (TextView) view.findViewById(R.id.time);

            items.setText(trans.getItemsName());
            beneficiary.setText(trans.getBeneficiaryName());
            totalAmount.setText(trans.getTotalAmount());

//            date.setText(trans.getDate());
//            time.setText(trans.getTime());
            letter.setLetter(trans.getBeneficiaryName().charAt(0)+"");

//            view.setOnClickListener(new CardListener());
            relativeLayout.addView(view);
        }
    }

    //Hard coded
    public List<Transaction> getAllTransactions() {
        allTransactions = new ArrayList<Transaction>();

        OrderItem o1 = new OrderItem("Veg Thali",60.00,1);
        OrderItem o2 = new OrderItem("Idli Sambar",20.00,2);
        OrderItem o3 = new OrderItem("Kanda Poha",25.00,1);

        List<OrderItem> orderList = new ArrayList<>();
        orderList.add(o1);

        Transaction t1 = new Transaction(orderList,"Veg Thali","Punjabi Junction","20.00",1234,45677889);
        Transaction t2 = new Transaction(orderList,"Veg Thali","Punjabi Junction","20.00",1235,7685424);
        Transaction t3 = new Transaction(orderList,"Veg Thali","Punjabi Junction","20.00",1236,45677889);
        Transaction t4 = new Transaction(orderList,"Veg Thali","Punjabi Junction","20.00",1237,45677889);

        allTransactions.add(t1);
        allTransactions.add(t2);
        allTransactions.add(t3);
        allTransactions.add(t4);

        return allTransactions;
    }

    @Override
    public void onServiceDiscovered(NsdServiceInfo serviceInfo) {

    }

    @Override
    public void onServiceResolved(NsdServiceInfo serviceInfo) {

    }

    @Override
    public void onDiscoveryStopped(String s) {
        //
    }
}
