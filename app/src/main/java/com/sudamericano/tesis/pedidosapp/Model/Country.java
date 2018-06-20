package com.sudamericano.tesis.pedidosapp.Model;

/**
 * Created by williamtapia on 10/20/16.
 */
public class Country {

    private String mId;
    private String mName;


    public Country(String mId, String mName) {
        this.mId = mId;
        this.mName = mName;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    @Override
    public String toString() {
        return mName;
    }
}
