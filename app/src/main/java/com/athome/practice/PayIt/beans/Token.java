package com.athome.practice.PayIt.beans;

import android.util.Log;

import com.athome.practice.PayIt.Constants;
import com.athome.practice.PayIt.dto.OrderItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anand Singh on 21/11/17.
 */
public class Token {

    String transactionId = "";
    String senderId = "";
    String receiverId = "";
    String totalAmount = "";
    String date = "";
    List<OrderItem> orderItems = new ArrayList<>();

    //Constructor for Sender
    public Token(String receiverId){
        this.receiverId = receiverId;
    }

    public Token(String transactionId, String senderId, String receiverId, String totalAmount, String date, List<OrderItem> orderItems) {
        this.transactionId = transactionId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.totalAmount = totalAmount;
        this.date = date;
        this.orderItems = orderItems;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void add(OrderItem item){
        orderItems.add(item);
    }

    public void removeOrderItem(String orderItem) {
        //TODO
    }

    public void setSenderId(String filePath){
        senderId = filePath;
    }

    public String getSenderId(){
        return senderId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void createFromJSON(JSONObject main){
        //Decode JSON and create Token
        try {
            String transactionId = main.getString(Constants.TRANSACTION_ID);
            String senderId = main.getString(Constants.SENDER_ID);
            String receiverId = main.getString(Constants.RECEIVER_ID);
            JSONArray orderItems = main.getJSONArray(Constants.ORDER_ITEMS);
            String totalAmount = main.getString(Constants.TOTAL_AMOUNT);
            String date = main.getString(Constants.DATE);

            this.orderItems.clear();

            for(int i=0; i<orderItems.length(); i++){
                OrderItem item = new OrderItem();
                try {
                    item.setItemName(((OrderItem)orderItems.get(i)).getItemName());
                    item.setPrice(((OrderItem)orderItems.get(i)).getPrice());
                    item.setQuantity(((OrderItem)orderItems.get(i)).getQuantity());
                } catch (JSONException e) {
                    Log.d(Constants.TAG,"Error while parsing OrderItems");
                }
                add(item);
            }

            setTransactionId(transactionId);
            setSenderId(senderId);
            setReceiverId(receiverId);
            setTotalAmount(totalAmount);
            setDate(date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
