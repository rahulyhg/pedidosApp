package com.sudamericano.tesis.pedidosapp.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by williamtapia on 6/8/16.
 */
public class Producto implements Serializable {

    private static final long serialVersionUID = -6478896794413377840L;
    private int mId;
    private String mTitle;
    private String mDescripcion;
    private String mCreated_at;
    private List<Categoria> mCategorias;
    private String mSku;
    private double mPrice;
    private int mStock_quantity;
    private String mUrlImage;
    private boolean mIsTaxable;


    private List<Atributos> mAtributos;
    private Float averageRating;

    private boolean addtoCart;


    public Producto() {

    }

    public Producto(int mId, String mTitle, String mCreated_at, String mSku, double mPrice, int mStock_quantity, String mUrlImage, String mDescripcion,boolean Taxable) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mCreated_at = mCreated_at;
        this.mSku = mSku;
        this.mPrice = mPrice;
        this.mStock_quantity = mStock_quantity;
        this.mUrlImage = mUrlImage;
        this.mDescripcion = mDescripcion;
        this.mIsTaxable = Taxable;

    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmCreated_at() {
        return mCreated_at;
    }

    public void setmCreated_at(String mCreated_at) {
        this.mCreated_at = mCreated_at;
    }

    public List<Categoria> getmCategorias() {
        return mCategorias;
    }

    public void setmCategorias(List<Categoria> mCategorias) {
        this.mCategorias = mCategorias;
    }

    public String getmSku() {
        return mSku;
    }

    public void setmSku(String mSku) {
        this.mSku = mSku;
    }

    public double getmPrice() {
        return mPrice;
    }

    public void setmPrice(double mPrice) {
        this.mPrice = mPrice;
    }

    public int getmStock_quantity() {
        return mStock_quantity;
    }

    public void setmStock_quantity(int mStock_quantity) {
        this.mStock_quantity = mStock_quantity;
    }

    public void setmImage(String mImage) {
        this.mUrlImage = mImage;
    }

    public List<Atributos> getmAtributos() {
        return mAtributos;
    }

    public void setmAtributos(List<Atributos> mAtributos) {
        this.mAtributos = mAtributos;
    }

    public Float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Float averageRating) {
        this.averageRating = averageRating;
    }


    public String getmDescripcion() {
        return mDescripcion;
    }

    public void setmDescripcion(String mDescripcion) {
        this.mDescripcion = mDescripcion;
    }


    public void setmUrlImage(String mUrlImage) {
        this.mUrlImage = mUrlImage;
    }

    public String getmUrlImage() {
        return mUrlImage;
    }

    public boolean isAddtoCart() {
        return addtoCart;
    }

    public void setAddtoCart(boolean addtoCart) {
        this.addtoCart = addtoCart;
    }

    public boolean ismIsTaxable() {
        return mIsTaxable;
    }

    public void setmIsTaxable(boolean mIsTaxable) {
        this.mIsTaxable = mIsTaxable;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "mId=" + mId +
                ", mTitle='" + mTitle + '\'' +
                ", mDescripcion='" + mDescripcion + '\'' +
                ", mCreated_at='" + mCreated_at + '\'' +
                ", mCategorias=" + mCategorias +
                ", mSku='" + mSku + '\'' +
                ", mPrice=" + mPrice +
                ", mStock_quantity=" + mStock_quantity +
                ", mUrlImage='" + mUrlImage + '\'' +
                ", mAtributos=" + mAtributos +
                ", averageRating=" + averageRating +
                ", addtoCart=" + addtoCart +
                '}';
    }
}
