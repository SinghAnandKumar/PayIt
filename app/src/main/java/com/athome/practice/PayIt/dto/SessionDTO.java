package com.athome.practice.PayIt.dto;

import java.util.ArrayList;

/**
 * Created by Anand Singh on 19/11/2017.
 */
public class SessionDTO {
    private String sessionId;
    private String merchantId;
    private String merchantName;
    private String customerId;
    private String customerName;
    private String date;
    private ArrayList<OrderItem> orderItems;

    public SessionDTO(String sessionId, String merchantId, String merchantName, String customerId, String customerName, String date, ArrayList<OrderItem> orderItems) {
        this.sessionId = sessionId;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.customerId = customerId;
        this.customerName = customerName;
        this.date = date;
        this.orderItems = orderItems;
    }

    public SessionDTO() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "SessionDTO{" +
                "sessionId='" + sessionId + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", date='" + date + '\'' +
                ", orderItems=" + orderItems +
                '}';
    }
}
