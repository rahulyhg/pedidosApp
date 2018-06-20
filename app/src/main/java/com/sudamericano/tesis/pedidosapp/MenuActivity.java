package com.sudamericano.tesis.pedidosapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.sudamericano.tesis.pedidosapp.Orders.OrderListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "account";


    private ProgressDialog pDialog;
    private int MY_SOCKET_TIMEOUT_MS = 6000;
    String DOMAIN;

    ImageButton mComprasButton ;
    ImageButton mPedidosButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Espere por favor...");
        pDialog.setCancelable(false);
        DOMAIN = getResources().getString(R.string.DOMAIN);


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

         mComprasButton = (ImageButton) findViewById(R.id.btn_compras);
         mPedidosButton = (ImageButton) findViewById(R.id.btn_pedidos);
        ImageButton mManualButton = (ImageButton) findViewById(R.id.btn_manual);
        ImageButton mLogoutButton = (ImageButton) findViewById(R.id.btn_logout);
//
//        mComprasButton.setVisibility(View.GONE);
//        mComprasButton.setEnabled(false);
//
//        mPedidosButton.setVisibility(View.GONE);
//        mPedidosButton.setEnabled(false);


        TextView mdisplay_name = (TextView) findViewById(R.id.display_name);


        String display_name = (sharedpreferences.getString("display_name", ""));


        verifyPermission();

//        try {
//            JSONObject reader = new JSONObject(permissions);
//
//
//            try {
//                String can_publish = reader.getString("create_pedidos");
//                if (can_publish.equals("true")) {
//                    mComprasButton.setVisibility(View.VISIBLE);
//                    mComprasButton.setEnabled(true);
//
//                }
//
//            } catch (JSONException ex) {
//
//                Log.e("error", ex.toString());
//
//            }
//
//

//
//        } catch (JSONException e) {
//            e.printStackTrace();
//
//            new AlertDialog.Builder(MenuActivity.this)
//                    .setTitle("Error en el servidor  ")
//                    .setMessage("Ha ocurrido un error al obtener información del usuario, por favor intente nuevamente")
//                    .setCancelable(false)
//                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                        }
//                    }).create().show();
//        }


        mdisplay_name.setText(display_name);


        mComprasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MenuActivity.this, ProductoListActivity.class));


            }

        });

        mPedidosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, OrderListActivity.class));
            }

        });


        mManualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getBaseContext(), ManualActivity.class));

            }

        });
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor edit = sharedpreferences.edit();
                edit.clear();
                edit.commit();
                MenuActivity.this.finish();
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
            }

        });

    }

    private void verifyPermission() {

        //showpDialog();
        String id = (sharedpreferences.getString("id", ""));

        getPermissionsbyUserId(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {



                try {
                    JSONObject reader = new JSONObject(response);
                    String id = reader.getString("id");
                    String display_name = reader.getString("name");
                    String roles = reader.getString("roles");
                    String email = reader.getString("email");
                    JSONObject permissions = reader.getJSONObject("capabilities");

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("id", id);
                    editor.putString("display_name", display_name);
                    editor.putString("roles", roles);
                    editor.putString("email", email);
                    editor.putString("permissions", permissions.toString());
                    editor.commit();





                    try {
                        String can_publish = permissions.getString("create_pedidos");
                        if (can_publish.equals("true")) {
                            mPedidosButton.setVisibility(View.VISIBLE);
                            mPedidosButton.setEnabled(true);

                        }

                    } catch (JSONException ex) {

                        Log.e("error", ex.toString());

                    }

                    try {
                        String can_publish = permissions.getString("edit_pedidos");
                        if (can_publish.equals("true")) {
                            mComprasButton.setVisibility(View.VISIBLE);
                            mComprasButton.setEnabled(true);

                        }

                    } catch (JSONException ex) {

                        Log.e("error", ex.toString());

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    hidepDialog();
                }
            }

            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(MenuActivity.this)
                        .setTitle("Error de autentificación ")
                        .setMessage("Ha ocurrido un error, usted no tiene permisos para acceder a esta aplicación")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).create().show();

                SharedPreferences.Editor edit = sharedpreferences.edit();
                edit.clear();
                edit.commit();


                MenuActivity.this.finish();
                startActivity(new Intent(MenuActivity.this, LoginActivity.class));


                hidepDialog();
            }

            @Override
            public void onServerError() {
                new AlertDialog.Builder(MenuActivity.this)
                        .setTitle("Error en el servidor  ")
                        .setMessage("Ha ocurrido un error al obtener información del usuario, por favor intente nuevamente")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).create().show();
                hidepDialog();
            }

            @Override
            public void onFailureConnection() {
                new AlertDialog.Builder(MenuActivity.this)
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
        }, id);
    }

    @Override
    public void onBackPressed() {

        //finish();
    }

    private void getPermissionsbyUserId(final VolleyCallback callback, final String id) {

        String token = (sharedpreferences.getString("access_token", ""));

        String url = (DOMAIN + "/wp-json/wp/v2/users/" + id + "?context=edit&access_token=" + token);

        Log.d("consultado", url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {



                        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                            VolleyError errores = new VolleyError(new String(volleyError.networkResponse.data));


                        }

                        if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                            callback.onFailureConnection();
                        } else if (volleyError instanceof AuthFailureError) {
                            callback.onAuthFailureError();
                        } else if (volleyError instanceof ServerError) {
                            callback.onServerError();
                        } else if (volleyError instanceof NetworkError) {
                            callback.onFailureConnection();
                        } else if (volleyError instanceof ParseError) {
                            callback.onServerError();
                        } else if (volleyError instanceof TimeoutError) {
                            callback.onFailureConnection();
                        }

                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grant_type", "password");
                return params;

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

        };


        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        mRequestQueue.add(postRequest);
    }

    public interface VolleyCallback {
        void onSuccess(String token);

        void onAuthFailureError();

        void onServerError();

        void onFailureConnection();
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}
