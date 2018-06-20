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
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PedidoDetalleActivity extends AppCompatActivity implements View.OnClickListener {


    private int MY_SOCKET_TIMEOUT_MS = 10000;
    private ProgressDialog pDialog;
    TableLayout tlayout;
    TextView Tnro;
    TextView Tfecha;
    TextView Tcliente;
    TextView Tsubtotal;
    TextView Tiva;
    TextView Ttotal;
    TextView Testado;
    int id = 0;

    Button mEliminar ;
    Button mCompletar ;
    Button mReembolso ;

    String DOMAIN;

    public static final String MyPREFERENCES = "account";
    SharedPreferences sharedpreferences;
    int color_text = Color.parseColor("#000000");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_detalle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        DOMAIN = getResources().getString(R.string.DOMAIN);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Espere por favor...");
        pDialog.setCancelable(false);
        tlayout = (TableLayout) findViewById(R.id.tablePedidoDetalle);
        Tnro = (TextView) findViewById(R.id.TextNroOrden);
        Tfecha = (TextView) findViewById(R.id.TextFecha);
        Tcliente = (TextView) findViewById(R.id.TextCliente);
        Tsubtotal = (TextView) findViewById(R.id.TextSubtotal);
        Tiva = (TextView) findViewById(R.id.TextIVA);
        Ttotal = (TextView) findViewById(R.id.TextTotal);
        Testado = (TextView) findViewById(R.id.TextEstado);


        Bundle b = getIntent().getExtras();
        if (b != null)
            id = b.getInt("id");

        loadOrder(this, id);

        tlayout.setGravity(Gravity.CENTER);
        tlayout.setBackgroundResource(R.color.colorPrimaryDark);
        tlayout.setStretchAllColumns(true);

         mEliminar = (Button) findViewById(R.id.btnEliminar);
         mCompletar = (Button) findViewById(R.id.btnCompletar);
         mReembolso = (Button) findViewById(R.id.btnReembolso);

        mEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new AlertDialog.Builder(PedidoDetalleActivity.this)
                        .setTitle("Eliminacion Permanente")
                        .setMessage("Esta seguro de eliminar esta orden?")
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteOrder(id);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

        mCompletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new AlertDialog.Builder(PedidoDetalleActivity.this)
                        .setTitle("Completar Pedido")
                        .setMessage("Esta seguro que desea completar este pedido?")
                        .setIcon(android.R.drawable.ic_lock_lock)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                UpdateStatusOrder(id, "completed");
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

        mReembolso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder alert = new AlertDialog.Builder(PedidoDetalleActivity.this);
                alert.setTitle("Ingrese el valor a reembolsar");
                final EditText input = new EditText(PedidoDetalleActivity.this);
                input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                //input.setRawInputType(Configuration.KEYBOARD_12KEY);
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for OK button here

                        try {
                            float total = Float.valueOf(input.getText().toString());

                           // RefundOrder(id, total);
                        } catch (Exception ex) {


                        }


                    }
                });

                alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alert.show();


            }
        });

    }



    private void loadOrder(final PedidoDetalleActivity pedidosDetalleActivity, int id) {

        RequestgetOrder(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject order) {
                hidepDialog();
                tlayout.removeAllViews();

                Context applicationContext = pedidosDetalleActivity.getApplicationContext();

                TableRow separator = new TableRow(pedidosDetalleActivity);
                View line = new View(pedidosDetalleActivity);
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
                text1.setText("DESCRIPCION.");
                text1.setLayoutParams(new TableRow.LayoutParams(250, TableRow.LayoutParams.WRAP_CONTENT));
                text1.setGravity(1);
                text1.setTextColor(color_text);

                row.addView(text1);


                text5.setText("P. UNIT.");
                text5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                text5.setGravity(1);
                text5.setTextColor(color_text);

                row.addView(text5);

                text2.setText("CANT.");
                text2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                text2.setGravity(1);
                text2.setTextColor(color_text);

                row.addView(text2);


                text3.setText("SUBT.");
                text3.setLayoutParams(new TableRow.LayoutParams(10, TableRow.LayoutParams.WRAP_CONTENT));
                text3.setGravity(1);
                text3.setTextColor(color_text);


                row.addView(text3);

                tlayout.addView(row);


                separator = new TableRow(pedidosDetalleActivity);
                line = new View(pedidosDetalleActivity);
                separatorLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1);
                separatorLayoutParams.setMargins(2, 0, 5, 0);
                separatorLayoutParams.span = 5;
                line.setBackgroundColor(Color.rgb(144, 144, 144));

                separator.addView(line, separatorLayoutParams);


                tlayout.addView(separator);


                try {



                    int id = (Integer) order.get("id");

                    String created_at = (String) order.get("date_created");
                    String total = (String) order.get("total");
                 //   String subtotal = (String) order.get("subtotal");
                    String total_tax = (String) order.get("total_tax");
                    String estado = (String) order.get("status");

                    String cliente = "Invitado";
                    try {
                        JSONObject customer = order.getJSONObject("customer");
                        cliente = (String) customer.get("first_name");
                        cliente = cliente + " " + (String) customer.get("last_name");


                    } catch (Exception ex) {
                        Log.d("error", ex.toString());

                    }

                    try {

                        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = fmt.parse(created_at);

                        SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
                        created_at = fmtOut.format(date);

                    } catch (Exception ex) {

                    }


                    Tnro.setText(String.valueOf(id));
                    Tfecha.setText(created_at);
                    Tcliente.setText(cliente);
                 //   Tsubtotal.setText(subtotal);
                    Tiva.setText(total_tax);
                    Ttotal.setText(total);
                    Testado.setText(estado);
                    if(Testado.getText().equals("completed")){

                         mEliminar.setEnabled(false);
                         mCompletar.setEnabled(false);
                         mReembolso.setEnabled(false);

                    }

                    JSONArray line_items = order.getJSONArray("line_items");

                    for (int i = 0; i < line_items.length(); i++) {


                        JSONObject item = line_items.getJSONObject(i);


                        row = new TableRow(applicationContext);
                        text1 = new TextView(applicationContext);

                        text2 = new TextView(applicationContext);
                        text3 = new TextView(applicationContext);
                        text4 = new TextView(applicationContext);


                        text1.setText(String.valueOf(item.get("name")));
                        text1.setLayoutParams(new TableRow.LayoutParams(10, TableRow.LayoutParams.WRAP_CONTENT));
                        text1.setGravity(1);
                        text1.setTextColor(color_text);

                        row.addView(text1);


                        text2.setText(String.valueOf(item.get("price")));
                        text2.setGravity(1);
                        text2.setTextColor(color_text);


                        row.addView(text2);


                        text3.setText(String.valueOf(item.get("quantity")));
                        text3.setGravity(1);
                        text3.setTextColor(color_text);

                        row.addView(text3);

                        text4.setText(String.valueOf(item.get("total")));
                        text4.setGravity(1);
                        text4.setTextColor(color_text);


                        row.addView(text4);


                        tlayout.addView(row);

                        separator = new TableRow(pedidosDetalleActivity);
                        line = new View(pedidosDetalleActivity);
                        separatorLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1);
                        separatorLayoutParams.setMargins(2, 0, 2, 0);
                        separatorLayoutParams.span = 5;
                        line.setBackgroundColor(Color.rgb(144, 144, 144));

                        separator.addView(line, separatorLayoutParams);


                        tlayout.addView(separator);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    System.err.print(e.getMessage());
                }


            }


            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(PedidoDetalleActivity.this)
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
                    new AlertDialog.Builder(PedidoDetalleActivity.this)
                            .setTitle("Error")
                            .setMessage(j.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();


                } catch (Exception e) {

                    new AlertDialog.Builder(PedidoDetalleActivity.this)
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
                new AlertDialog.Builder(PedidoDetalleActivity.this)
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
    private void deleteOrder(int id) {
        RequestdeleteOrders(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                hidepDialog();

                new AlertDialog.Builder(PedidoDetalleActivity.this)
                        .setTitle("Eliminacion Correcta")
                        .setMessage("La orden ha sido eliminada correctamente")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                PedidoDetalleActivity.this.finish();

                                startActivity(new Intent(PedidoDetalleActivity.this, PedidosActivity.class));


                            }
                        }).create().show();
            }

            @Override
            public void onAuthFailureError() {

            }

            @Override
            public void onServerError(VolleyError Error) {
                hidepDialog();

                VolleyError errores = new VolleyError(new String(Error.networkResponse.data));

                new AlertDialog.Builder(PedidoDetalleActivity.this)
                        .setTitle("Error")
                        .setMessage(errores.toString())
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create().show();
            }

            @Override
            public void onFailureConnection() {

            }
        }, id);

    }
    private void UpdateStatusOrder(int id, String status) {
        showpDialog();
        RequestChangeStatusOrder(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                hidepDialog();

                new AlertDialog.Builder(PedidoDetalleActivity.this)
                        .setTitle("Actualizacion de Orden Correcta")
                        .setMessage("La orden ha sido cambiada correctamente")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                PedidoDetalleActivity.this.finish();


                                startActivity(new Intent(PedidoDetalleActivity.this, PedidosActivity.class));


                            }
                        }).create().show();
            }


            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(PedidoDetalleActivity.this)
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
                    new AlertDialog.Builder(PedidoDetalleActivity.this)
                            .setTitle("Error")
                            .setMessage(j.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();


                } catch (Exception e) {

                    new AlertDialog.Builder(PedidoDetalleActivity.this)
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
                new AlertDialog.Builder(PedidoDetalleActivity.this)
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
        }, id, status);

    }
    private void RefundOrder(int id, float valor) {


        showpDialog();

        RequestCreateRefundOrders(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                hidepDialog();

                new AlertDialog.Builder(PedidoDetalleActivity.this)
                        .setTitle("Actualizacion de Orden Correcta")
                        .setMessage("La orden ha sido cambiada correctamente")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                PedidoDetalleActivity.this.finish();

                                startActivity(new Intent(PedidoDetalleActivity.this, PedidosActivity.class));


                            }
                        }).create().show();
            }


            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(PedidoDetalleActivity.this)
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
                    new AlertDialog.Builder(PedidoDetalleActivity.this)
                            .setTitle("Error")
                            .setMessage(j.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();


                } catch (Exception e) {

                    new AlertDialog.Builder(PedidoDetalleActivity.this)
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
                new AlertDialog.Builder(PedidoDetalleActivity.this)
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
        }, id, valor);

    }


    public void RequestgetOrder(final VolleyCallback callback, int id) {
        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/wc/v1/orders/"+id+"?access_token=" + token;
        JSONObject jsonObjectLogin = new JSONObject();
        final JsonObjectRequest requestCategorias = new JsonObjectRequest(Request.Method.GET, url, jsonObjectLogin,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            callback.onFailureConnection();
                        } else if (error instanceof AuthFailureError) {
                            callback.onAuthFailureError();
                        } else if (error instanceof ServerError) {
                            callback.onServerError(error);
                        } else if (error instanceof NetworkError) {
                            callback.onFailureConnection();
                        } else if (error instanceof ParseError) {
                            callback.onServerError(error);
                        }
                    }

                }
        );
        requestCategorias.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        mRequestQueue.add(requestCategorias);


    }
    public void RequestdeleteOrders(final VolleyCallback callback, int id) {


        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/wc/v1/orders/"+id+"?force=true&access_token=" + token;



        Log.d("delete ORDERS", url);
        JSONObject jsonObjectLogin = new JSONObject();
        final JsonObjectRequest requestCategorias = new JsonObjectRequest(Request.Method.DELETE, url, jsonObjectLogin,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            callback.onFailureConnection();
                        } else if (error instanceof AuthFailureError) {
                            callback.onAuthFailureError();
                        } else if (error instanceof ServerError) {
                            callback.onServerError(error);
                        } else if (error instanceof NetworkError) {
                            callback.onFailureConnection();
                        } else if (error instanceof ParseError) {
                            callback.onServerError(error);
                        }
                    }

                }
        );
        requestCategorias.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        mRequestQueue.add(requestCategorias);


    }
    public void RequestChangeStatusOrder(final VolleyCallback callback, int id, String status) {
        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/wc/v1/orders/"+id+"?force=true&access_token=" + token;
        Log.d("UPDATE ORDERS", url);
        JSONObject order = new JSONObject();
        try {
            order.put("id", id);
            order.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("error formando json", e.getMessage());
        }
        final JsonObjectRequest requestCategorias = new JsonObjectRequest(Request.Method.POST, url, order,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            callback.onFailureConnection();
                        } else if (error instanceof AuthFailureError) {
                            callback.onAuthFailureError();
                        } else if (error instanceof ServerError) {
                            callback.onServerError(error);
                        } else if (error instanceof NetworkError) {
                            callback.onFailureConnection();
                        } else if (error instanceof ParseError) {
                            callback.onServerError(error);
                        }
                    }

                }
        );
        requestCategorias.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        mRequestQueue.add(requestCategorias);


    }
    public void RequestCreateRefundOrders(final VolleyCallback callback, int id, float total) {
        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/wc/v1/orders/"+id+"/refunds?access_token=" + token;
        Log.d("refund ORDERS", url);
        JSONObject order_refunds = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            order_refunds.put("amount", total);
            order_refunds.put("reason", "REEMBOLSO POR EMPLEADO A TRAVES DE LA PLATAFORMA ANDROID");
            data.put("order_refund", order_refunds);
            System.err.println(data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest requestCategorias = new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            callback.onFailureConnection();
                        } else if (error instanceof AuthFailureError) {
                            callback.onAuthFailureError();
                        } else if (error instanceof ServerError) {
                            callback.onServerError(error);
                        } else if (error instanceof NetworkError) {
                            callback.onFailureConnection();
                        } else if (error instanceof ParseError) {
                            callback.onServerError(error);
                        }
                    }

                }
        );
        requestCategorias.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        mRequestQueue.add(requestCategorias);


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

    }

    public interface VolleyCallback {
        void onSuccess(JSONObject result);

        void onAuthFailureError();

        void onServerError(VolleyError Error);

        void onFailureConnection();
    }


    @Override
    public void onBackPressed() {

        finish();
    }

}
