package com.sudamericano.tesis.pedidosapp.Model;

/**
 * Created by williamtapia on 7/5/16.
 */
public class Cliente {

    private int mId;
    private String mFirstName;
    private String mLastName;
    private String mIndetificacion;
    private String mAddress1;
    private String mAddress2;
    private Country mCounty;
    private String mCity;
    private String mState;
    private String mPhone;
    private String mEmail;
    private String mPostCode;


    public Cliente() {
    }

    public Cliente(int mId, String mFirstName, String mLastName, String mAddress1, String mAddress2, String mCity, String mState, String mPhone) {
        this.mId = mId;
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mAddress1 = mAddress1;
        this.mAddress2 = mAddress2;
        this.mCity = mCity;
        this.mState = mState;
        this.mPhone = mPhone;
    }

    public String getmIndetificacion() {
        return mIndetificacion;
    }

    public void setmIndetificacion(String mIndetificacion) {
        this.mIndetificacion = mIndetificacion;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public void setmLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getmAddress1() {
        return mAddress1;
    }

    public void setmAddress1(String mAddress1) {
        this.mAddress1 = mAddress1;
    }

    public String getmAddress2() {
        return mAddress2;
    }

    public void setmAddress2(String mAddress2) {
        this.mAddress2 = mAddress2;
    }

    public String getmCity() {
        return mCity;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public String getmState() {
        return mState;
    }

    public void setmState(String mState) {
        this.mState = mState;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public Country getmCounty() {
        return mCounty;
    }

    public void setmCounty(Country mCounty) {
        this.mCounty = mCounty;
    }

    public String getmPostCode() {
        return mPostCode;
    }

    public void setmPostCode(String mPostCode) {
        this.mPostCode = mPostCode;
    }

    @Override
    public String toString() {
        return this.getmFirstName() + " " + this.getmLastName() ;
    }
}
