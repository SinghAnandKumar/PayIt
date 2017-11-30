package com.athome.practice.PayIt.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.athome.practice.PayIt.Constants;
import com.athome.practice.PayIt.R;
import com.athome.practice.PayIt.beans.Transaction;

public class TransactionListItemDetailActivity extends AppCompatActivity {

    //Need to implement
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list_item_details);

        Transaction transaction = (Transaction) getIntent().getSerializableExtra(Constants.TRANSACTION_ITEM);
    }
}
