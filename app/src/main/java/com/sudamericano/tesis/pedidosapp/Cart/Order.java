package com.sudamericano.tesis.pedidosapp.Cart;

import android.util.Log;

import java.io.Serializable;
import java.util.List;

/**
 * Created by williamtapia on 11/4/16.
 */
public class Order implements Serializable {


    private static final long serialVersionUID = -6778335607745568452L;
    private int id;
    private String CustomerId;
    private String CustomerName;
    private String CustomerAddress1;
    private String CustomerAddress2;
    private String CustomerCity;
    private String CustomerState;

    private String Date;
    private String Status;
    private double Total;
    private double TotalTax;
    private double DiscountTotal;
    private List<OrderDetails> lineItems;
    private List<OrderRefunds> orderRefunds;
    private List<Double> Payments;


    public static final String PROCCESSING = "processing";
    public static final String REFUNDED = "refunded";
    public static final String PENDING = "pending";
    public static final String CANCELLED = "cancelled";
    public static final String COMPLETED = "completed";
    public static final String FAILED = "failed";
    public static final String ONHOLD = "on-hold";


    public Order(int id, String customer, String date, String state, double total, double discountTotal, double totalTax) {
        this.id = id;
        CustomerName = customer;
        Date = date;
        Status = state;
        Total = total;
        this.TotalTax = totalTax;
        this.DiscountTotal = discountTotal;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(String customerId) {
        CustomerId = customerId;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public double getTotal() {
        return Total;
    }

    public void setTotal(double total) {
        Total = total;
    }

    public List<OrderDetails> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<OrderDetails> lineItems) {
        this.lineItems = lineItems;
    }

    public String getCustomerAddress1() {
        return CustomerAddress1;
    }

    public void setCustomerAddress1(String customerAddress1) {
        CustomerAddress1 = customerAddress1;
    }

    public String getCustomerAddress2() {
        return CustomerAddress2;
    }

    public void setCustomerAddress2(String customerAddress2) {
        CustomerAddress2 = customerAddress2;
    }

    public String getCustomerCity() {
        return CustomerCity;
    }

    public void setCustomerCity(String customerCity) {
        CustomerCity = customerCity;
    }

    public String getCustomerState() {
        return CustomerState;
    }

    public void setCustomerState(String customerState) {
        CustomerState = customerState;
    }

    public String getAddressComplete() {
        return this.getCustomerAddress1() + " y " + this.getCustomerAddress2() + " /" + this.getCustomerCity() + " - " + this.getCustomerState();
    }

    public double getTotalTax() {
        return TotalTax;
    }

    public void setTotalTax(double totalTax) {
        TotalTax = totalTax;
    }

    public double getDiscountTotal() {
        return DiscountTotal;
    }

    public void setDiscountTotal(double discountTotal) {
        DiscountTotal = discountTotal;
    }

    public List<OrderRefunds> getOrderRefunds() {
        return orderRefunds;
    }

    public void setOrderRefunds(List<OrderRefunds> orderRefunds) {
        this.orderRefunds = orderRefunds;
    }

    public double getTotalRefunds() {
        List<OrderRefunds> orderRefunds = this.getOrderRefunds();
        double totalOrderRefunds = 0;
        for (OrderRefunds orderRefunds1 : orderRefunds) {
            totalOrderRefunds += orderRefunds1.getTotal();
        }
        return totalOrderRefunds;
    }


    public double getTotalWithRefunds() {
        return roundValue(this.getTotal() + getTotalRefunds());
    }

    private double roundValue(double value) {
        return Math.round(value * 100.0) / 100.0;

    }

    public List<Double> getPayments() {
        return Payments;
    }

    public void setPayments(List<Double> payments) {
        Payments = payments;
    }

    public double getPaymentsTotal() {
        List<Double> payments = this.getPayments();

        Log.e("paymets",payments.toString());
        double total = 0;
        for (double payment : payments
                ) {

            total += payment;
        }

        return total;
    }

    public double getValueToPay(){
       double value= this.getTotalWithRefunds() - this.getPaymentsTotal();
        if(value<=0){
            return 0;

        }else{
            return roundValue(value);
        }

    }

    public String getTranslationStatusOrder(String Status) {
        String result;
        switch (Status) {
            case PROCCESSING:
                result = "Procesando";
                break;
            case REFUNDED:
                result = "Reembolsado";
                break;
            case PENDING:
                result = "Pendiente";
                break;
            case CANCELLED:
                result = "Cancelado";
                break;
            case COMPLETED:
                result = "Completado";
                break;
            case FAILED:
                result = "Fallido";
                break;
            case ONHOLD:
                result = "En Espera";
                break;
            default:
                result = Status;

        }

        return result;

    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", CustomerName='" + CustomerName + '\'' +
                ", Date='" + Date + '\'' +
                ", Status='" + Status + '\'' +
                ", Total=" + Total +
                '}';
    }
}
