package com.sudamericano.tesis.pedidosapp.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by MATUTE-TAPIA-FML on 26/05/2016.
 */
public class AttributesProduct implements Serializable {

    private static final long serialVersionUID = 7176606005107067163L;
    private int mId;
    private String mNombre;
    private String mSlug;
    private String mVisible;
    private List<String> mOptions;

    public AttributesProduct(String mNombre, String mSlug, String mVisible, List<String> mOptions) {

        this.mNombre = mNombre;
        this.mSlug = mSlug;
        this.mVisible = mVisible;
        this.mOptions= mOptions;
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

    public String getmVisible() {
        return mVisible;
    }

    public void setmVisible(String mVisible) {
        this.mVisible = mVisible;
    }

    public List<String> getmOptions() {
        return mOptions;
    }

    public void setmOptions(List<String> mOptions) {
        this.mOptions = mOptions;
    }

    @Override
    public String toString() {
        return "AttributesProduct{" +
                "mNombre='" + mNombre + '\'' +
                '}';
    }


}
