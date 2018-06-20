package com.sudamericano.tesis.pedidosapp.Cart;

public class CartDetails {


    private int mCartId;
    private int mProductoId;
    private String mName;
    private String mImageUrl;
    private int mStockQuantity;
    private int mQuantity;
    private double mUnitPrice;
    private double mSubtotal;
    private double mDiscount;
    private double mSubtotalWithDiscount;
    private String mDiscountType;
    private boolean mIsTaxable;
    private double mTaxesTotal;

    private double mTotal;


    public CartDetails() {

    }


    public CartDetails(int mProductoId, String mName, String mImageUrl, int mStockQuantity, int mQuantity, double mUnitPrice, double mDiscount, String mDiscountType, boolean mIsTaxable, double mTaxesTotal, double mSubtotal) {
        this.mProductoId = mProductoId;
        this.mName = mName;
        this.mImageUrl = mImageUrl;
        this.mStockQuantity = mStockQuantity;
        this.mQuantity = mQuantity;
        this.mUnitPrice = mUnitPrice;
        this.mSubtotal = mSubtotal;
        this.mDiscount = mDiscount;
        this.mDiscountType = mDiscountType;
        this.mIsTaxable = mIsTaxable;
        this.mTaxesTotal = mTaxesTotal;

    }

    public int getmCartId() {
        return mCartId;
    }

    public void setmCartId(int mCartId) {
        this.mCartId = mCartId;
    }

    public int getmProductoId() {
        return mProductoId;
    }

    public void setmProductoId(int mProductoId) {
        this.mProductoId = mProductoId;
    }

    public String getmName() {
        return mName;
    }

    public String getmShortName() {
        return mName.substring(0, 8);
    }




    public void setmName(String mName) {
        this.mName = mName;
    }

    public double getmUnitPrice() {
        return mUnitPrice;
    }

    public void setmUnitPrice(double mUnitPrice) {
        this.mUnitPrice = mUnitPrice;
    }

    public int getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }

    public double getmDiscount() {
        return mDiscount;
    }

    public void setmDiscount(double mDiscount) {
        this.mDiscount = mDiscount;
    }

    public double getmTaxesTotal() {
        return mTaxesTotal;
    }

    public void setmTaxesTotal(double mTaxesTotal) {
        this.mTaxesTotal = mTaxesTotal;
    }

    public double getmSubtotal() {
        return mSubtotal;
    }

    public void setmSubtotal(double mSubtotal) {
        this.mSubtotal = mSubtotal;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public int getmStockQuantity() {
        return mStockQuantity;
    }

    public void setmStockQuantity(int mStockQuantity) {
        this.mStockQuantity = mStockQuantity;
    }

    public boolean ismIsTaxable() {
        return mIsTaxable;
    }

    public void setmIsTaxable(boolean mIsTaxable) {
        this.mIsTaxable = mIsTaxable;
    }

    public String getmDiscountType() {
        return mDiscountType;
    }

    public void setmDiscountType(String mDiscountType) {
        this.mDiscountType = mDiscountType;
    }

    public double getmSubtotalWithDiscount() {

        mSubtotalWithDiscount = this.getmSubtotal() - this.getmDiscount();
        return roundValue(mSubtotalWithDiscount);
    }


    public double getmTotal() {

        mTotal = roundValue(this.getmSubtotal() - this.getmDiscount() + this.getmTaxesTotal());
        return mTotal;
    }

    private double roundValue(double value) {
        return Math.round(value * 100.0) / 100.0;

    }


    @Override
    public String toString() {
        return "CartDetails{" +
                "mProductoId=" + mProductoId +
                ", mName='" + mName + '\'' +
                ", mImageUrl='" + mImageUrl + '\'' +
                ", mStockQuantity=" + mStockQuantity +
                ", mQuantity=" + mQuantity +
                ", mUnitPrice=" + mUnitPrice +
                ", mDiscount=" + mDiscount +
                ", mDiscountType=" + mDiscountType +
                ", mIsTaxable=" + mIsTaxable +
                ", mTaxesTotal=" + mTaxesTotal +
                ", mSubtotal=" + mSubtotal +
                '}';
    }
}
