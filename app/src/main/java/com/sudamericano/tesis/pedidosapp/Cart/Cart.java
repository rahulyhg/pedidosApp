package com.sudamericano.tesis.pedidosapp.Cart;

import java.util.Date;


public class Cart {

    private int mCartID;
    private int mCustomerId;

    private double mDiscount;
    private String  mDiscountType;
    private String  mCouponCode;

    private double mTaxesPercentage;


    private double mSubtotalWithTaxes0;
    private double mSubtotalWithTaxes;

    private double mTotal;

    private Date mDateCreated;


    public int getmCartID() {
        return mCartID;
    }

    public void setmCartID(int mCartID) {
        this.mCartID = mCartID;
    }

    public int getmCustomerId() {
        return mCustomerId;
    }

    public void setmCustomerId(int mCustomerId) {
        this.mCustomerId = mCustomerId;
    }

    public double getmDiscount() {
        return mDiscount;
    }

    public void setmDiscount(double mDiscount) {
        this.mDiscount = mDiscount;
    }

    public double getmTotal() {
        return mTotal;
    }

    public void setmTotal(double mTotal) {
        this.mTotal = mTotal;
    }

    public Date getmDateCreated() {
        return mDateCreated;
    }

    public void setmDateCreated(Date mDateCreated) {
        this.mDateCreated = mDateCreated;
    }

    public double getmSubtotalWithTaxes0() {
        return mSubtotalWithTaxes0;
    }

    public void setmSubtotalWithTaxes0(double mSubtotalWithTaxes0) {
        this.mSubtotalWithTaxes0 = mSubtotalWithTaxes0;
    }

    public double getmSubtotalWithTaxes() {
        return mSubtotalWithTaxes;
    }

    public void setmSubtotalWithTaxes(double mSubtotalWithTaxes) {
        this.mSubtotalWithTaxes = mSubtotalWithTaxes;
    }

    public double getmTaxesPercentage() {
        return mTaxesPercentage;
    }

    public void setmTaxesPercentage(double mTaxesPercentage) {
        this.mTaxesPercentage = mTaxesPercentage;
    }

    public String getmDiscountType() {
        return mDiscountType;
    }

    public void setmDiscountType(String mDiscountType) {
        this.mDiscountType = mDiscountType;
    }

    public String getmCouponCode() {
        return mCouponCode;
    }

    public void setmCouponCode(String mCouponCode) {
        this.mCouponCode = mCouponCode;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "mCartID=" + mCartID +
                ", mCustomerId=" + mCustomerId +
                ", mDiscount=" + mDiscount +
                ", mDiscountType='" + mDiscountType + '\'' +
                ", mCouponCode='" + mCouponCode + '\'' +
                ", mTaxesPercentage=" + mTaxesPercentage +
                ", mSubtotalWithTaxes0=" + mSubtotalWithTaxes0 +
                ", mSubtotalWithTaxes=" + mSubtotalWithTaxes +
                ", mTotal=" + mTotal +
                ", mDateCreated=" + mDateCreated +
                '}';
    }
}
