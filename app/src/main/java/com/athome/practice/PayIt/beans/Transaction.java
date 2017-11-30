package com.athome.practice.PayIt.beans;

import com.athome.practice.PayIt.dto.OrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Anand Singh on 19/11/2017.
 */
public class Transaction implements Serializable{
    public static final Transaction NONE = new Transaction();

    private List<OrderItem> orderItems;
    private String itemsName;
    private String beneficiaryName;
    private String payeeName;
    private String totalAmount;
    private long transactionId;
    private long date;

    public Transaction() {
    }

    public Transaction(List<OrderItem> orderItems, String itemsName, String beneficiaryName, String totalAmount, long transactionId, long date) {
        this.orderItems = orderItems;
        this.itemsName = itemsName;
        this.beneficiaryName = beneficiaryName;
        this.totalAmount = totalAmount;
        this.transactionId = transactionId;
        this.date = date;
        init();
    }

    private void init() {
        double totalAmount = 0;
        String itemsName = "";
        for (OrderItem orderItem: orderItems) {
            totalAmount += (orderItem.getPrice() * orderItem.getQuantity());
            itemsName += "," + orderItem.getItemName();
        }
        setTotalAmount(Double.toString(totalAmount));
        setItemsName(itemsName);
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public String getItemsName() {
        return itemsName;
    }

    public void setItemsName(String itemsName) {
        this.itemsName = itemsName;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
