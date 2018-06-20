package com.sudamericano.tesis.pedidosapp.Model;

/**
 * Created by williamtapia on 11/2/16.
 */
public class States {

    private String mId;
    private String mName;


    public States(String mId, String mName) {
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
        return mId;
    }
}
