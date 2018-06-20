package com.sudamericano.tesis.pedidosapp.Cart;

import java.io.Serializable;

/**
 * Created by williamtapia on 11/5/16.
 */
public class OrderRefunds implements Serializable {


    private static final long serialVersionUID = 637909061333144831L;


    private int id;
    private String refund;
    private double total;

    public OrderRefunds(int id, String refund, double total) {
        this.id = id;
        this.refund = refund;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRefund() {
        return refund;
    }

    public void setRefund(String refund) {
        this.refund = refund;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
