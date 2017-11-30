package com.athome.practice.PayIt.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anand Singh on 19/11/2017.
 */
public class Transactions implements Serializable{
    public static final Transactions NONE = new Transactions();

    private List<Transaction> transactions = new ArrayList<Transaction>();

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}
