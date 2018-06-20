package com.sudamericano.tesis.pedidosapp.Cart;

import java.io.Serializable;

/**
 * Created by williamtapia on 11/4/16.
 */
public class OrderDetails implements Serializable {


    private static final long serialVersionUID = 4305756710325704460L;

    private int id;
    private String name;
    private String sku;
    private int productId;
    private int quantity;
    private double price;
    private double subtotal;
    private double subtotalTax;
    private double total;
    private double totalTax;



    public OrderDetails( int id, String name, String sku, int productId, int quantity, double price, double subtotal, double subtotalTax, double total, double totalTax) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
        this.subtotalTax = subtotalTax;
        this.total = total;
        this.totalTax = totalTax;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getmShortName() {
            if(name.length()>16){
                return name.substring(0, 16)+"...";
            }else{
                return name;
            }
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public double getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getSubtotalTax() {
        return subtotalTax;
    }

    public void setSubtotalTax(double subtotalTax) {
        this.subtotalTax = subtotalTax;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(double totalTax) {
        this.totalTax = totalTax;
    }

    @Override
    public String toString() {
        return "OrderDetails{" +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", subtotal=" + subtotal +
                ", subtotalTax=" + subtotalTax +
                ", total=" + total +
                ", totalTax=" + totalTax +
                '}';
    }
}
