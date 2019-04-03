package com.kr.pineco;

import java.util.ArrayList;

public class Order {
    String orderUID,orderDelivery, orderDiscount, orderTotal, orderGrandTotal, orderPromotionCode,orderTime,orderPaymentType,orderStatus;
    ArrayList<Fruit> fruitArray=new ArrayList<>();
    public Order(String orderUID,String orderDelivery, String orderTotal, String orderDiscount, String orderGrandTotal, String orderPromotionCode, String orderTime, String orderPaymentType, String orderStatus, ArrayList<Fruit> fruitArray){
        this.orderUID=orderUID;
        this.orderDelivery=orderDelivery;
        this.orderDiscount=orderDiscount;
        this.orderTotal=orderTotal;
        this.orderGrandTotal=orderGrandTotal;
        this.orderPaymentType=orderPaymentType;
        this.orderPromotionCode=orderPromotionCode;
        this.orderTime=orderTime;
        this.orderStatus=orderStatus;
        this.fruitArray=fruitArray;
    }

    public String getOrderUID() {
        return orderUID;
    }

    public void setOrderUID(String orderUID) {
        this.orderUID = orderUID;
    }

    public String getOrderDelivery() {
        return orderDelivery;
    }

    public void setOrderDelivery(String orderDelivery) {
        this.orderDelivery = orderDelivery;
    }

    public String getOrderDiscount() {
        return orderDiscount;
    }

    public void setOrderDiscount(String orderDiscount) {
        this.orderDiscount = orderDiscount;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getOrderGrandTotal() {
        return orderGrandTotal;
    }

    public void setOrderGrandTotal(String orderGrandTotal) {
        this.orderGrandTotal = orderGrandTotal;
    }

    public String getOrderPromotionCode() {
        return orderPromotionCode;
    }

    public void setOrderPromotionCode(String orderPromotionCode) {
        this.orderPromotionCode = orderPromotionCode;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderPaymentType() {
        return orderPaymentType;
    }

    public void setOrderPaymentType(String orderPaymentType) {
        this.orderPaymentType = orderPaymentType;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public ArrayList<Fruit> getFruitArray() {
        return fruitArray;
    }

    public void setFruitArray(ArrayList<Fruit> fruitArray) {
        this.fruitArray = fruitArray;
    }
}
