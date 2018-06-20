package com.sudamericano.tesis.pedidosapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.sudamericano.tesis.pedidosapp.Model.Cliente;
import com.sudamericano.tesis.pedidosapp.Model.Producto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PedidosActivity extends AppCompatActivity implements View.OnClickListener {


    private int MY_SOCKET_TIMEOUT_MS = 30000;
    private List<Producto> productoList;
    private ProgressDialog pDialog;
    private ArrayList<Cliente> mData;
    TableLayout tlayout;
    private JSONObject JsonArrayCustomer;

    String DOMAIN;

    public static final String MyPREFERENCES = "account";
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Espere por favor...");
        pDialog.setCancelable(false);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        DOMAIN = getResources().getString(R.string.DOMAIN);

        setContentView(R.layout.activity_pedidos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        tlayout = (TableLayout) findViewById(R.id.tablePedidos);


        showpDialog();
        loadOrders(this);


        tlayout.setGravity(Gravity.CENTER);
        tlayout.setBackgroundResource(R.color.colorPrimaryDark);
        tlayout.setStretchAllColumns(true);


    }

    int color_text = Color.parseColor("#000000");


    private void loadOrders(final PedidosActivity pedidosActivity) {

        getOrders(new VolleyCallbackArray() {
            @Override
            public void onSuccess(JSONArray JsonArrayOrders) {
                hidepDialog();
                tlayout.removeAllViews();


                Context applicationContext = pedidosActivity.getApplicationContext();

                TableRow separator = new TableRow(pedidosActivity);
                View line = new View(pedidosActivity);
                TableRow.LayoutParams separatorLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1);
                separatorLayoutParams.setMargins(2, 0, 2, 0);
                separatorLayoutParams.span = 5;
                line.setBackgroundColor(Color.rgb(144, 144, 144));

                separator.addView(line, separatorLayoutParams);


                tlayout.addView(separator);




                TableRow row = new TableRow(applicationContext);
                //  row.setBackground(R.color.colorPrimaryDark);
                // row = new TableRow(applicationContext);

                TextView text1 = new TextView(applicationContext);
                TextView text2 = new TextView(applicationContext);
                TextView text5 = new TextView(applicationContext);
                TextView text3 = new TextView(applicationContext);
                TextView text4 = new TextView(applicationContext);

                // Drawable drawable = getResources().getDrawable(R.drawable.border_table);
                text1.setText("NRO.");
                text1.setLayoutParams(new TableRow.LayoutParams(10, TableRow.LayoutParams.WRAP_CONTENT));
                text1.setGravity(1);
                text1.setTextColor(color_text);
                row.addView(text1);


                text5.setText("FECHA");
                text5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                text5.setGravity(1);
                text5.setTextColor(color_text);
                row.addView(text5);

                text2.setText("CLIENTE");
                text2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                text2.setGravity(0);
                text2.setTextColor(color_text);
                row.addView(text2);


                text3.setText("TOTAL");
                text3.setLayoutParams(new TableRow.LayoutParams(10, TableRow.LayoutParams.WRAP_CONTENT));
                text3.setGravity(1);
                text3.setTextColor(color_text);

                row.addView(text3);
                text4.setText("ACCIONES");
                // text4.setBackgroundDrawable(drawable);
                text4.setGravity(1);
                text4.setTextColor(color_text);
                row.addView(text4);
                tlayout.addView(row);


                separator = new TableRow(pedidosActivity);
                line = new View(pedidosActivity);
                separatorLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1);
                separatorLayoutParams.setMargins(2, 0, 5, 0);
                separatorLayoutParams.span = 5;
                line.setBackgroundColor(Color.rgb(144, 144, 144));

                separator.addView(line, separatorLayoutParams);


                tlayout.addView(separator);


                try {
                    for (int i = 0; i < JsonArrayOrders.length(); i++) {
                        JSONObject order = JsonArrayOrders.getJSONObject(i);

                        int id = (Integer) order.get("id");
                        String created_at = (String) order.get("date_created");
                        String total = (String) order.get("total");

                        String cliente = "Invitado";
                        try {
                            JSONObject customer = order.getJSONObject("billing");
                            cliente = (String) customer.get("first_name");
                            cliente = cliente + " " + (String) customer.get("last_name");


                        } catch (Exception ex) {
                            Log.d("error", ex.toString());

                        }


                        //  JsonArrayCustomer.getString("id");

                        row = new TableRow(applicationContext);
                        text1 = new TextView(applicationContext);
                        TextView textFecha = new TextView(applicationContext);
                        text2 = new TextView(applicationContext);
                        text3 = new TextView(applicationContext);


                        text1.setText(String.valueOf(id));
                        //   text1.setBackgroundDrawable(drawable);
                        text1.setLayoutParams(new TableRow.LayoutParams(10, TableRow.LayoutParams.WRAP_CONTENT));
                        text1.setGravity(1);
                        text1.setTextColor(color_text);

                        row.addView(text1);

                        try {

                            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = fmt.parse(created_at);

                            SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
                            created_at = fmtOut.format(date);

                        } catch (Exception ex) {

                            Log.e("br20", ex.getMessage());

                        }


                        textFecha.setText(String.valueOf(created_at));
                        //   text1.setBackgroundDrawable(drawable);
                        textFecha.setLayoutParams(new TableRow.LayoutParams(10, TableRow.LayoutParams.WRAP_CONTENT));
                        textFecha.setGravity(1);
                        textFecha.setTextColor(color_text);

                        row.addView(textFecha);

                        text2.setText(cliente);
                        //  text2.setBackgroundDrawable(drawable);
                        text2.setGravity(0);

                        text2.setTextColor(color_text);

                        row.addView(text2);

                        text3.setText(total);
                        //  text3.setBackgroundDrawable(drawable);
                        text3.setGravity(1);
                        text3.setTextColor(color_text);

                        row.addView(text3);

                        Button b = new Button(applicationContext);
                        b.setText("Detalles");
                        b.setId(id);
                        b.setWidth(20);
                        b.setHeight(100);
                        b.setLayoutParams(new TableRow.LayoutParams(50, 100));

                        b.setGravity(Gravity.CENTER_HORIZONTAL);
                        b.setTextSize(8);
                        //   b.setBackgroundDrawable(drawable);
                        b.setOnClickListener(pedidosActivity);
                        b.setGravity(1);

                        row.addView(b);


                        tlayout.addView(row);

                        separator = new TableRow(pedidosActivity);
                        line = new View(pedidosActivity);
                        separatorLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1);
                        separatorLayoutParams.setMargins(2, 0, 2, 0);
                        separatorLayoutParams.span = 5;
                        line.setBackgroundColor(Color.rgb(144, 144, 144));

                        separator.addView(line, separatorLayoutParams);


                        tlayout.addView(separator);


                    }


                } catch (JSONException e) {
                    Log.e("br20", e.getMessage());
                }


            }

            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(PedidosActivity.this)
                        .setTitle("Error de autentificación ")
                        .setMessage("Ha ocurrido un error, usted no tiene permisos para aplicar cupones!")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create().show();
                hidepDialog();
            }
            @Override
            public void onServerError(VolleyError error) {
                try {
                    VolleyError errores = new VolleyError(new String(error.networkResponse.data));
                    String message = errores.getMessage();
                    JSONObject j = new JSONObject(message);
                    new AlertDialog.Builder(PedidosActivity.this)
                            .setTitle("Error")
                            .setMessage(j.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();


                } catch (Exception e) {

                    new AlertDialog.Builder(PedidosActivity.this)
                            .setTitle("Error ")
                            .setMessage("Error al crear al cliente, error desconocido!")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }
                hidepDialog();
            }
            @Override
            public void onFailureConnection() {
                new AlertDialog.Builder(PedidosActivity.this)
                        .setTitle("Error en la conexión ")
                        .setMessage("Verifique si esta conectado a internet e intente nuevamente!")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Configuracion.this.finish();
                            }
                        }).create().show();
                hidepDialog();

            }
        });
    }


    public void getOrders(final VolleyCallbackArray callback) {


        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/wc/v1/orders?access_token=" + token;


        Log.d("DOMAIN ORDERS", url);
        JSONObject jsonObjectLogin = new JSONObject();
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("response",response.toString());
                        hidepDialog();
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
                Log.d("eerror",error.toString());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    callback.onFailureConnection();
                } else if (error instanceof AuthFailureError) {
                    callback.onAuthFailureError();
                } else if (error instanceof ServerError) {
                    callback.onServerError(error);
                } else if (error instanceof NetworkError) {
                    callback.onFailureConnection();
                }

            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        mRequestQueue.add(req);


    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onClick(View v) {


        final int id = v.getId();


        Intent intent = new Intent(PedidosActivity.this, PedidoDetalleActivity.class);
        Bundle b = new Bundle();
        b.putInt("id", id);
        intent.putExtras(b);
        startActivity(intent);


    }


    public interface VolleyCallback {
        void onSuccess(JSONObject result);

        void onAuthFailureError();

        void onServerError(VolleyError Error);

        void onFailureConnection();
    }
    public interface VolleyCallbackArray {
        void onSuccess(JSONArray result);

        void onAuthFailureError();

        void onServerError(VolleyError Error);

        void onFailureConnection();
    }


    @Override
    public void onBackPressed() {

        finish();
    }


}
