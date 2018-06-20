package com.sudamericano.tesis.pedidosapp.Model;

import java.io.Serializable;

/**
 * Created by MATUTE-TAPIA-FML on 26/05/2016.
 */
public class Categoria implements Serializable {

    private static final long serialVersionUID = 7176606085107567163L;
    private int mId;
    private String mNombre;
    private String mSlug;
    private Categoria mSuperior;
    private String mDescripcion;
    private String mImage;

    public Categoria(int mId, String mNombre, String mSlug, Categoria mSuperior, String mDescripcion, String mImage) {
        this.mId = mId;
        this.mNombre = mNombre;
        this.mSlug = mSlug;
        this.mSuperior = mSuperior;
        this.mDescripcion = mDescripcion;
        this.mImage = mImage;
    }

    public Categoria(String mNombre) {
        this.mNombre = mNombre;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmNombre() {
        return mNombre;
    }

    public void setmNombre(String mNombre) {
        this.mNombre = mNombre;
    }

    public String getmSlug() {
        return mSlug;
    }

    public void setmSlug(String mSlug) {
        this.mSlug = mSlug;
    }

    public Categoria getmSuperior() {
        return mSuperior;
    }

    public void setmSuperior(Categoria mSuperior) {
        this.mSuperior = mSuperior;
    }

    public String getmDescripcion() {
        return mDescripcion;
    }

    public void setmDescripcion(String mDescripcion) {
        this.mDescripcion = mDescripcion;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "mNombre='" + mNombre + '\'' +
                '}';
    }
}
