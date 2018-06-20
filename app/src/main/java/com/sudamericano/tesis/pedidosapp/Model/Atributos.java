package com.sudamericano.tesis.pedidosapp.Model;

import java.util.List;

/**
 * Created by williamtapia on 6/8/16.
 */
public class Atributos {
    private String mNombre;
    private List<String>  mOpciones;

    public Atributos(String mNombre, List<String> mOpciones) {
        this.mNombre = mNombre;
        this.mOpciones = mOpciones;
    }

    public String getmNombre() {
        return mNombre;
    }

    public void setmNombre(String mNombre) {
        this.mNombre = mNombre;
    }

    public List<String> getmOpciones() {
        return mOpciones;
    }

    public void setmOpciones(List<String> mOpciones) {
        this.mOpciones = mOpciones;
    }
}
