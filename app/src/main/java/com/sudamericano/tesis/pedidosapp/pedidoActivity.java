package com.sudamericano.tesis.pedidosapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sudamericano.tesis.pedidosapp.Cart.Cart;
import com.sudamericano.tesis.pedidosapp.Cart.CartDetails;
import com.sudamericano.tesis.pedidosapp.Cart.CartHelper;
import com.sudamericano.tesis.pedidosapp.Cart.Order;
import com.sudamericano.tesis.pedidosapp.Model.Cliente;
import com.sudamericano.tesis.pedidosapp.Model.Country;
import com.sudamericano.tesis.pedidosapp.Orders.OrderListActivity;
import com.sudamericano.tesis.pedidosapp.Utils.DelayAutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class pedidoActivity extends AppCompatActivity {


    private static final int THRESHOLD = 1;
    private DelayAutoCompleteTextView mClienteAutoCompleteView;
    private AutoCompleteAdapter adapter;
    private int MY_SOCKET_TIMEOUT_MS = 5000;

    private ProgressDialog pDialog;
    private ArrayList<Cliente> mData;
    private ArrayList<Cliente> mDataFiltered;


    private EditText mState;
    private EditText mCiudadEditText;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mAddress1EditText;
    private EditText mAddress2EditText;
    private EditText mPhoneEditText;
    private EditText mEmailEditText;
    private CartHelper cartHelper;
    private EditText mCuponEditText;
    ToggleButton NuevoClientetoggleButton;
    private TableLayout tlayout;
    private TextView mtotalPedidoTextView;
    private TextView mDescountTypeTextView;
    private TextView mDescountValueTextView;
    private Button mApplyCupon;
    private Button mCrearPedidoButton;


    private double descuento = 0.0;

    String codigo_descuento = "";


    private TextView mIdentificacion;

    public static final String MyPREFERENCES = "account";
    SharedPreferences sharedpreferences;

    ImageButton mRemove_coupon;
    private Spinner mCountrySpinner;
    private Cliente client;
    private TextView mTaxesValue;
    private String DOMAIN = "";
    private TextView mPostCode;
    private TextView mId;
    private ArrayAdapter spinnerArrayCountriesAdapter;

    private TextView mSubtotal_with_taxes0TextView;
    private TextView mSubtotal_with_taxesTextView;
    private TextView mCliente_nuevoEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        DOMAIN = getResources().getString(R.string.DOMAIN);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Espere un momento por favor...");
        pDialog.setCancelable(false);

        tlayout = (TableLayout) findViewById(R.id.tablePedidoItems);


        mClienteAutoCompleteView = (DelayAutoCompleteTextView) findViewById(R.id.clientes_list);

        mClienteAutoCompleteView.setThreshold(THRESHOLD);

        adapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line);

        mClienteAutoCompleteView.setAdapter(adapter);

        mClienteAutoCompleteView.setLoadingIndicator(
                (android.widget.ProgressBar) findViewById(R.id.pb_loading_indicator));

        mId = (TextView) findViewById(R.id.Id);
        mIdentificacion = (TextView) findViewById(R.id.identificacion);
        mFirstNameEditText = (EditText) findViewById(R.id.first_name);
        mLastNameEditText = (EditText) findViewById(R.id.last_name);
        mAddress1EditText = (EditText) findViewById(R.id.Address1);
        mAddress2EditText = (EditText) findViewById(R.id.Address2);
        mCiudadEditText = (EditText) findViewById(R.id.city);
        mPhoneEditText = (EditText) findViewById(R.id.Phone);
        mEmailEditText = (EditText) findViewById(R.id.Email);
        mPostCode = (TextView) findViewById(R.id.PostCode);
        mCountrySpinner = (Spinner) findViewById(R.id.country);
        mState = (EditText) findViewById(R.id.status);


        mCliente_nuevoEditText = (TextView) findViewById(R.id.cliente_nuevo_text);

        mCuponEditText = (EditText) findViewById(R.id.CuponCode);
        mtotalPedidoTextView = (TextView) findViewById(R.id.totalPedido);
        mDescountTypeTextView = (TextView) findViewById(R.id.descountType);
        mDescountValueTextView = (TextView) findViewById(R.id.descountValue);
        mSubtotal_with_taxes0TextView = (TextView) findViewById(R.id.subtotal_with_taxes0);
        mSubtotal_with_taxesTextView = (TextView) findViewById(R.id.subtotal_with_taxes);

        mTaxesValue = (TextView) findViewById(R.id.taxes_value);


        NuevoClientetoggleButton = (ToggleButton) findViewById(R.id.toggle_cliente_nuevo);
        mApplyCupon = (Button) findViewById(R.id.btn_aplicar_cupon);
        mCrearPedidoButton = (Button) findViewById(R.id.btn_crear_pedito);
        mRemove_coupon = (ImageButton) findViewById(R.id.remove_coupon);


        spinnerArrayCountriesAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, new Country[]{
                new Country("EC", "EC"),
        });

        mCountrySpinner.setAdapter(spinnerArrayCountriesAdapter);


        mIdentificacion.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                Log.e("cedula", s.toString());

                if (validarIdentificacion(s.toString()) == true) {
                    mIdentificacion.setError(null);
                    mCrearPedidoButton.setEnabled(true);

                } else {

                    mIdentificacion.setError("Cédula o Ruc incorrectos!");
                    mCrearPedidoButton.setEnabled(false);


                }

            }


        });


        client = new Cliente();
        cartHelper = new CartHelper(getBaseContext());


        int cartCount = cartHelper.getCartCount();
        if (cartCount == 0) {

            hidepDialog();
            pedidoActivity.this.finish();

            startActivity(new Intent(pedidoActivity.this, CarritoItemListActivity.class));


        }

        cargarClientes("");

        NuevoClientetoggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mClienteAutoCompleteView.setEnabled(false);
                    mClienteAutoCompleteView.setVisibility(View.GONE);
                    mId.setVisibility(View.GONE);
                    mCliente_nuevoEditText.setText("AGREGAR CLIENTE");
                    clearFields();

                } else {
                    mClienteAutoCompleteView.setEnabled(true);
                    mClienteAutoCompleteView.setText("");
                    mClienteAutoCompleteView.setVisibility(View.VISIBLE);
                    mId.setVisibility(View.VISIBLE);
                    mCliente_nuevoEditText.setText("BUSCAR CLIENTE");


                }
            }
        });


        mClienteAutoCompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                client = adapter.getItem(position);
                setvaluesToTextField();
            }
        });


        mApplyCupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = mCuponEditText.getText().toString();
                applyCoupon(code);
            }

        });


        mRemove_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mCuponEditText.setText("");

                cartHelper.RemoveDiscountCart();
                loadPedidoItemsinTable();
            }

        });


        mCrearPedidoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                checkOut();


            }
        });

        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showpDialog();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showpDialog();
            }
        });


        Country country = (Country) mCountrySpinner.getSelectedItem();

        client.setmCounty(country);


        loadTaxes(client);

        loadPedidoItemsinTable();

    }

    private boolean validarIdentificacion(String cedula) {


        if(cedula.length() >= 10){


            char[] caracteres = new char[cedula.length()];
            for (int i = 0; i < cedula.length() ; i++) {
                caracteres[i] = (char)cedula.charAt(i);
                System.out.println(caracteres[i]);

                if (!Character.isDigit(caracteres[i])) {
                    return false;
                }
            }



            //verifica que el último dígito de la cédula sea válido
            int[] d = new int[10];
            //Asignamos el string a un array
            for (int i = 0; i < d.length; i++) {
                d[i] = Integer.parseInt(cedula.charAt(i) + "");
            }

            int imp = 0;
            int par = 0;

            //sumamos los duplos de posición impar
            for (int i = 0; i < d.length; i += 2) {
                d[i] = ((d[i] * 2) > 9) ? ((d[i] * 2) - 9) : (d[i] * 2);
                imp += d[i];
            }

            //sumamos los digitos de posición par
            for (int i = 1; i < (d.length - 1); i += 2) {
                par += d[i];
            }

            //Sumamos los dos resultados
            int suma = imp + par;

            //Restamos de la decena superior
            int d10 = Integer.parseInt(String.valueOf(suma + 10).substring(0, 1) +
                    "0") - suma;

            //Si es diez el décimo dígito es cero
            d10 = (d10 == 10) ? 0 : d10;

            //si el décimo dígito calculado es igual al digitado la cédula es correcta
            if (d10 == d[9]) {
                if(cedula.length() == 13 ){
                    Log.e("cedula", "11 " + caracteres[10]);
                    Log.e("cedula", "12 " + caracteres[11]);
                    Log.e("cedula", "13 " + caracteres[12]);

                    if( caracteres[10] == '0' && caracteres[11] == '0' && caracteres[12] == '1' ){

                        return true;
                    }else{
                        Log.e("cedula", "false de 13 completa");
                        return false;
                    }



                }else if(cedula.length()==10){
                    return true;
                }else{
                    Log.e("cedula", "false");
                    return false;
                }


            }else {
                //addError("La cédula ingresada no es válida");
                return false;
            }


        }
        else {

            return false;
        }


    }


    private void checkOut() {
        showpDialog();
        if (!NuevoClientetoggleButton.isChecked()) {

            if (TextUtils.isEmpty(mClienteAutoCompleteView.getText())) {
                mClienteAutoCompleteView.setError(getString(R.string.error_invalid_email));
                hidepDialog();
                return;

            }
        }

        if (TextUtils.isEmpty(mFirstNameEditText.getText())) {
            mFirstNameEditText.setError(getString(R.string.error_invalid_email));
            hidepDialog();
            return;

        }

        if (TextUtils.isEmpty(mLastNameEditText.getText())) {
            mLastNameEditText.setError(getString(R.string.error_invalid_email));
            hidepDialog();
            return;

        }

        if (TextUtils.isEmpty(mCiudadEditText.getText())) {
            mCiudadEditText.setError(getString(R.string.error_invalid_email));
            hidepDialog();
            return;

        }

        if (TextUtils.isEmpty(mAddress1EditText.getText())) {
            mAddress1EditText.setError(getString(R.string.error_invalid_email));
            hidepDialog();
            return;

        }
        if (TextUtils.isEmpty(mAddress2EditText.getText())) {
            mAddress2EditText.setError(getString(R.string.error_invalid_email));
            hidepDialog();
            return;

        }

        if (TextUtils.isEmpty(mPhoneEditText.getText())) {
            mPhoneEditText.setError(getString(R.string.error_invalid_email));
            hidepDialog();
            return;

        }

        if (TextUtils.isEmpty(mEmailEditText.getText())) {
            mEmailEditText.setError(getString(R.string.error_invalid_email));
            hidepDialog();
            return;

        }


        new AlertDialog.Builder(this)
                .setTitle("Crear Pedido")
                .setMessage("Esta seguro que desea realizar el pedido?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {


                        if (NuevoClientetoggleButton.isChecked()) {
                            crearCliente();
                            return;
                        } else {
                            updateCliente();
                            return;

                        }

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hidepDialog();
                    }
                }).show();


    }


    /**
     * Crear Pedido despues de haber creado al Cliente
     */
    private void crearPedido() {
        RequestCreatePedido(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("error", result.toString());

                try {
                    int id = (int) result.get("id");
                    reduceStock(id);
                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Felicidades")
                            .setMessage("Su pedido fue registrado correctamente!")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //pedidoActivity.this.finish();

                                    cartHelper.deleteCart();

                                    sendEmail( String.valueOf( id));

                                    startActivity(new Intent(pedidoActivity.this, MenuActivity.class));

                                }
                            }).create().show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hidepDialog();


            }

            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(pedidoActivity.this)
                        .setTitle("Error de Permisos! ")
                        .setMessage("Ha ocurrido un error, usted no tiene permisos para crear pedidos")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                pedidoActivity.this.finish();
                                startActivity(new Intent(pedidoActivity.this, OrderListActivity.class));
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
                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error")
                            .setMessage(j.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();


                } catch (Exception e) {

                    new AlertDialog.Builder(pedidoActivity.this)
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
                new AlertDialog.Builder(pedidoActivity.this)
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


    private void reduceStock(int id) {
        RequestReduceStock(new VolleyCallback() {

            @Override
            public void onSuccess(JSONObject result) {
                hidepDialog();
                /*new AlertDialog.Builder(pedidoActivity.this)
                        .setTitle("Felicidades")
                        .setMessage("El stock se ha reducido correctamente!")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //pedidoActivity.this.finish();
                                //cartHelper.deleteCart();
                                // startActivity(new Intent(pedidoActivity.this, MenuActivity.class));

                            }
                        }).create().show(); */
            }

            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(pedidoActivity.this)
                        .setTitle("Error de Permisos ! ")
                        .setMessage("Ha ocurrido un error, usted no tiene permisos para al reducir el stock")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                pedidoActivity.this.finish();
                                startActivity(new Intent(pedidoActivity.this, MenuActivity.class));
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
                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error")
                            .setMessage(j.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();


                } catch (Exception e) {

                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error ")
                            .setMessage("Error al reducir el stock, error desconocido!")
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
                new AlertDialog.Builder(pedidoActivity.this)
                        .setTitle("Error en la conexión al reducir el stock ")
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

    public void RequestReduceStock(final VolleyCallback callback, int id) {


        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "wp-json/orders/v1/reduce/stock/" + id + "/?access_token=" + token;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
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


    public void RequestCreatePedido(final VolleyCallback callback) {


        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/wc/v1/orders?access_token=" + token;


        JSONObject order = new JSONObject();
        JSONObject billing_address = new JSONObject();
        JSONObject payment_details = new JSONObject();
        JSONObject coupon_lines = new JSONObject();
        try {


            List<CartDetails> carrito = cartHelper.getCart();
            if (carrito.size() == 0) {
                Toast.makeText(getBaseContext(), "No hay productos su carrito", Toast.LENGTH_LONG);
                return;
            }

            Cart cartHeader = cartHelper.getCartHeader();


            if (cartHeader.getmCouponCode() != null && !cartHeader.getmCouponCode().isEmpty() && cartHeader.getmDiscount() > 0) {
                JSONArray c = new JSONArray();
                coupon_lines.put("code", cartHeader.getmCouponCode());
                coupon_lines.put("amount", cartHeader.getmDiscount());
                c.put(coupon_lines);
                order.put("coupon_lines", c);
            }


            JSONArray item = new JSONArray();


            for (CartDetails cartDetails : carrito
                    ) {
                JSONObject line_items = new JSONObject();

                line_items.put("product_id", cartDetails.getmProductoId());
                line_items.put("quantity", cartDetails.getmQuantity());
                line_items.put("total", cartDetails.getmSubtotalWithDiscount());
                item.put(line_items);

            }


            order.put("line_items", item);
            payment_details.put("method_id", "bacs");
            payment_details.put("method_title", "Contra Reembolso");
            payment_details.put("paid", false);
            billing_address.put("customer_id", client.getmId());
            billing_address.put("first_name", client.getmFirstName());
            billing_address.put("last_name", client.getmLastName());
            billing_address.put("company", "");
            billing_address.put("address_1", client.getmAddress1());
            billing_address.put("address_2", client.getmAddress2());
            billing_address.put("city", client.getmCity());
            billing_address.put("state", client.getmState());
            billing_address.put("postcode", "");
            billing_address.put("country", client.getmCounty().toString());
            billing_address.put("email", client.getmEmail());
            billing_address.put("phone", client.getmPhone());
            billing_address.put("postcode", client.getmPostCode());
            order.put("billing", billing_address);
            order.put("shipping", billing_address);
            order.put("payment", payment_details);
            order.put("customer_id", client.getmId());
            order.put("payment_method", "cod");
            order.put("payment_method_title", "Contra reembolso");
            order.put("status", Order.PROCCESSING);
            order.put("paid", true);


        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest req = new JsonObjectRequest(url, order,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
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


    public void RequestgetTaxes(final VolleyCallbackArray callback, String country_code) {

        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/getinfo/v1/taxes/" + country_code + "?access_token=" + token;
        Log.e("response taxes", url);


        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response taxes", response.toString());


                        hidepDialog();
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();

                Log.e("error taxes", error.toString());

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


    private void loadTaxes(final Cliente cliente) {


        showpDialog();
        RequestgetTaxes(new VolleyCallbackArray() {
            @Override
            public void onSuccess(JSONArray result) {

                hidepDialog();

                try {


                    for (int i = 0; i < result.length(); i++) {


                        JSONObject taxes = result.getJSONObject(i);
//                        int id = Integer.valueOf(taxes.get("id").toString());
                        //  String country = (String) taxes.get("country");
//                        String post_code = (String) taxes.get("postcode");
//                        String city = (String) taxes.get("city");
                        double rate = Double.valueOf(taxes.get("rate").toString());
                        String label = (String) taxes.get("label");
//                        String created_at = (String) taxes.get("priority");
//                        String priority = (String) taxes.get("compound");
//                        String shipping = (String) taxes.get("shipping");
//                        String class_ = (String) taxes.get("class");

                        cartHelper.addTaxesPercentajetoCart(rate);


//                        Country country1 = cliente.getmCounty();
//
//                        if (country.equals(country1.getmId())) {
//                            break;
//                        }


                    }

                } catch (JSONException e) {

                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error Datos")
                            .setMessage(e.getMessage())
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            }).create().show();
                } catch (Exception e) {
                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error  ")
                            .setMessage(e.getMessage())
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            }).create().show();
                }


            }

            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(pedidoActivity.this)
                        .setTitle("Error de Permisos! ")
                        .setMessage("Ha ocurrido un error, usted no tiene permisos para obtener los impuestos!")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(pedidoActivity.this, MenuActivity.class));
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
                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error")
                            .setMessage(j.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();


                } catch (Exception e) {

                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error ")
                            .setMessage("Error al obtener a los impuestos, error desconocido!")
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
                new AlertDialog.Builder(pedidoActivity.this)
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
        }, cliente.getmCounty().getmId());
    }

    /**
     * Aplicar Pedido
     *
     * @param code
     */
    private void applyCoupon(String code) {


        if ((!code.isEmpty())) {
            showpDialog();
            RequestgetCupon(new VolleyCallbackArray() {
                @Override
                public void onSuccess(JSONArray result) {

                    JSONObject cupon = null;
                    double minimum_amount = 0;
                    double maximum_amount = 0;
                    int usage_limit = 0;
                    int usage_count = 0;

                    double subtotal = cartHelper.getCartTotal();

                    try {
                        cupon = result.getJSONObject(0);
                        codigo_descuento = (String) cupon.get("code");
                        descuento = Double.valueOf(cupon.get("amount").toString());

                        try {
                            minimum_amount = Double.valueOf(cupon.get("minimum_amount").toString());
                        } catch (Exception ex) {
                            System.err.println(ex.getMessage());
                        }
                        try {
                            maximum_amount = Double.valueOf(cupon.get("maximum_amount").toString());
                        } catch (Exception ex) {
                            System.err.println(ex.getMessage());
                        }
                        try {
                            usage_limit = Integer.valueOf(cupon.get("usage_limit").toString());
                        } catch (Exception ex) {
                            System.err.println(ex.getMessage());
                        }
                        try {
                            usage_count = Integer.valueOf(cupon.get("usage_count").toString());
                        } catch (Exception ex) {
                            System.err.println(ex.getMessage());

                        }


                        //                  Date fecha_expiracion = null; int usage_limit_per_user = 0;

                        //  try {
                        //                        usage_limit_per_user = Integer.valueOf(cupon.get("usage_limit_per_user").toString());
                        //
                        //                    } catch (Exception ex) {
                        //                        System.err.print(ex.getMessage());
                        //
                        //                    }


                        //                    try {
                        //                        String fecha_exp = (String) cupon.get("expiry_date");
                        //                        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                        //                        fecha_expiracion = fmt.parse(fecha_exp);
                        //                        Date now = new Date();
                        //                        if (fecha_expiracion.before(now)) {
                        //                            mCuponEditText.setError("Este codigo ya se encuentra expirado!");
                        //                            hidepDialog();
                        //                            return;
                        //                        }
                        //
                        //                    } catch (Exception ex) {
                        //
                        //
                        //
                        //
                        //                    }

                        if (usage_count > usage_limit) {
                            mCuponEditText.setError("Este codigo ya no se puede usar, ha superado el límite de cupos admitidos ");
                            hidepDialog();
                            return;
                        }
                        if (minimum_amount > 0 && minimum_amount > subtotal) {
                            mCuponEditText.setError("Este código se puede aplicar para compras con valor mayor a $" + minimum_amount);
                            hidepDialog();
                            return;
                        }

                        if (maximum_amount > 0 && subtotal > maximum_amount) {
                            mCuponEditText.setError("Este código se puede aplicar para compras con valor menores  a $" + maximum_amount);
                            hidepDialog();
                            return;
                        }

                        String tipo = (String) cupon.get("discount_type");


                        if (tipo.equals("percent")) {
                            boolean b = cartHelper.addDiscountCart(cartHelper.DISCOUNT_PERCENT, descuento, codigo_descuento);
                            if (!b) {
                                mCuponEditText.setError("No se pudo aplicar el cupón");
                            }
                        } else if (tipo.equals("fixed_cart")) {
                            boolean b2 = cartHelper.addDiscountCart(cartHelper.DISCOUNT_FIXED_CART, descuento, codigo_descuento);
                            if (!b2) {
                                mCuponEditText.setError("No se pudo aplicar el cupón");
                            }
                        }

                        loadPedidoItemsinTable();

                    } catch (JSONException e) {

                        new AlertDialog.Builder(pedidoActivity.this)
                                .setTitle("Error")
                                .setMessage(e.getMessage())
                                .setCancelable(false)
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                }).create().show();
                    } finally {
                        hidepDialog();
                    }
                }

                @Override
                public void onAuthFailureError() {
                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error de Permisos! ")
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
                        new AlertDialog.Builder(pedidoActivity.this)
                                .setTitle("Error")
                                .setMessage(j.getString("message"))
                                .setCancelable(false)
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                }).create().show();


                    } catch (Exception e) {

                        new AlertDialog.Builder(pedidoActivity.this)
                                .setTitle("Error ")
                                .setMessage("Error al crear al cliente, error desconocido!")
                                .setCancelable(false)
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                }).create().show();
                    }
                    hidepDialog();
                    mCuponEditText.setError("Este codigo es invalido");
                }

                @Override
                public void onFailureConnection() {
                    new AlertDialog.Builder(pedidoActivity.this)
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
            }, code);
        } else {
            Toast.makeText(getBaseContext(), "Ingrese un código de cupón", Toast.LENGTH_LONG);
            return;

        }

    }

    public void RequestgetCupon(final VolleyCallbackArray callback, String code) {


        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/wc/v1/coupons/?code=" + code + "&access_token=" + token;


        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        hidepDialog();
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
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


    /**
     * Get Coustomer to AutoCompleteField
     *
     * @param query
     */

    private void cargarClientes(String query) {
        showpDialog();
        RequestgetCustomers(new VolleyCallbackArray() {
            @Override
            public void onSuccess(JSONArray result) {


                Log.d("clientes", result.toString());
                try {


                    for (int i = 0; i < result.length(); i++) {

                        JSONObject cliente = result.getJSONObject(i);
                        int id = (Integer) cliente.get("id");
                        String first_name = (String) cliente.get("first_name");
                        String last_name = (String) cliente.get("last_name");
                        String email = (String) cliente.get("email");
                        // String created_at = (String) cliente.get("date_created");


                        Cliente c = new Cliente();
                        c.setmId(id);
                        c.setmFirstName(first_name);
                        c.setmLastName(last_name);
                        c.setmEmail(email);
                        c.setmIndetificacion((String) cliente.get("username"));


                        JSONObject billing_address = cliente.getJSONObject("billing");


                        String address_1 = (String) billing_address.get("address_1");
                        String address_2 = (String) billing_address.get("address_2");
                        String city = (String) billing_address.get("city");

                        String coutry = (String) billing_address.get("country");
                        String state = (String) billing_address.get("state");

                        String phone = (String) billing_address.get("phone");
                        String postcode = (String) billing_address.get("postcode");

                        c.setmCounty(new Country(coutry, coutry));

                        c.setmState(state);

                        c.setmPostCode(postcode);
                        c.setmCity(city);

                        c.setmAddress1(address_1);
                        c.setmAddress2(address_2);
                        c.setmPhone(phone);

                        mData.add(c);


                    }

                    mDataFiltered= mData;


                    //  mClienteAutoCompleteView.setAdapter(adapter);


                } catch (JSONException e) {

                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error ")
                            .setMessage(e.getMessage())
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            }).create().show();
                    hidepDialog();
                } catch (Exception e) {
                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error  ")
                            .setMessage(e.getMessage())
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            }).create().show();
                    hidepDialog();
                }

                hidepDialog();
            }

            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(pedidoActivity.this)
                        .setTitle("Error de Permisos! ")
                        .setMessage("Ha ocurrido un error, usted no tiene permisos para ver a los clientes!")
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
                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error")
                            .setMessage(j.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();


                } catch (Exception e) {

                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error ")
                            .setMessage("Error al obtener a los cliente, error desconocido!")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }
                hidepDialog();
                mCuponEditText.setError("Este codigo es invalido");
            }

            @Override
            public void onFailureConnection() {
                new AlertDialog.Builder(pedidoActivity.this)
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
        }, query);

    }

    public void RequestgetCustomers(final VolleyCallbackArray callback, String query) {

        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/wc/v1/customers?per_page=100&search=" + query + "&access_token=" + token;

        Log.e("getClientes",url);

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        hidepDialog();
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
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

    /**
     * Crear al cliente
     */
    private void crearCliente() {
        RequestCrearCliente(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject customer) {
                try {
                    int id = (int) customer.get("id");
                    String username = (String) customer.get("username");
                    JSONObject billing = customer.getJSONObject("billing");
                    String first_name = (String) billing.get("first_name");
                    String last_name = (String) billing.get("last_name");
                    String address_1 = (String) billing.get("address_1");
                    String address_2 = (String) billing.get("address_2");
                    String city = (String) billing.get("city");
                    String state = (String) billing.get("state");
                    String country = (String) billing.get("country");
                    String postcode = (String) billing.get("postcode");
                    String email = (String) billing.get("email");
                    String phone = (String) billing.get("phone");

                    client.setmId(id);
                    client.setmIndetificacion(username);
                    client.setmAddress1(address_1);
                    client.setmAddress2(address_2);
                    client.setmCity(city);
                    client.setmFirstName(first_name);
                    client.setmLastName(last_name);
                    client.setmState(state);
                    client.setmCounty(new Country(country, country));
                    client.setmEmail(email);
                    client.setmPhone(phone);
                    client.setmPostCode(postcode);

                    crearPedido();
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }

            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(pedidoActivity.this)
                        .setTitle("Error de Permisos! ")
                        .setMessage("Ha ocurrido un error, usted no tiene permisos para crear Clientes")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                pedidoActivity.this.finish();
                                startActivity(new Intent(pedidoActivity.this, MenuActivity.class));
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
                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error")
                            .setMessage(j.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();


                } catch (Exception e) {

                    new AlertDialog.Builder(pedidoActivity.this)
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
                new AlertDialog.Builder(pedidoActivity.this)
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

    public void RequestCrearCliente(final VolleyCallback callback) {

        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/wc/v1/customers?access_token=" + token;

        JSONObject data = new JSONObject();
        try {
            setValuestoObject();
            JSONObject billing_address = new JSONObject();
            billing_address.put("first_name", client.getmFirstName());
            billing_address.put("last_name", client.getmLastName());
            billing_address.put("company", "");
            billing_address.put("address_1", client.getmAddress1());
            billing_address.put("address_2", client.getmAddress2());
            billing_address.put("city", client.getmCity());
            billing_address.put("state", client.getmState());
            billing_address.put("postcode", client.getmPostCode());
            billing_address.put("country", client.getmCounty());
            billing_address.put("email", client.getmEmail());
            billing_address.put("phone", client.getmPhone());
            data.put("billing", billing_address);

            data.put("first_name", client.getmFirstName());
            data.put("last_name", client.getmLastName());
            data.put("email", client.getmEmail());
            data.put("username", client.getmIndetificacion());
            data.put("password", UUID.randomUUID().toString());


        } catch (JSONException e) {
            hidepDialog();
            return;

        }
        JsonObjectRequest req = new JsonObjectRequest(url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
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
        });

        req.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        mRequestQueue.add(req);


    }

    private void updateCliente() {

        setValuestoObject();

        RequestUpdateCustomer(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                crearPedido();
            }

            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(pedidoActivity.this)
                        .setTitle("Error de Permisos! ")
                        .setMessage("Ha ocurrido un error, usted no tiene permisos cambiar la información de Clientes!")
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
                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error")
                            .setMessage(j.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();


                } catch (Exception e) {

                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error ")
                            .setMessage("Error al actualizar a este cliente, error desconocido!")
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
                new AlertDialog.Builder(pedidoActivity.this)
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

    public void RequestUpdateCustomer(final VolleyCallback callback) {
        setValuestoObject();

        int id = client.getmId();
        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/wc/v1/customers/" + id + "?access_token=" + token;


        JSONObject data = new JSONObject();
        try {
            JSONObject billing_address = new JSONObject();
            billing_address.put("first_name", client.getmFirstName());
            billing_address.put("last_name", client.getmLastName());
            billing_address.put("company", "");
            billing_address.put("address_1", client.getmAddress1());
            billing_address.put("address_2", client.getmAddress2());
            billing_address.put("city", client.getmCity());
            billing_address.put("state", client.getmState());
            billing_address.put("postcode", client.getmPostCode());
            billing_address.put("country", client.getmCounty());
            billing_address.put("email", client.getmEmail());
            billing_address.put("phone", client.getmPhone());
            data.put("billing", billing_address);
            data.put("first_name", client.getmFirstName());
            data.put("last_name", client.getmLastName());
            data.put("email", client.getmEmail());
            data.put("id", client.getmId());
            data.put("username", client.getmIndetificacion());
            //data.put("password", UUID.randomUUID().toString());

            Log.d("client", data.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }


        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
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


    public void  sendEmail(String order_id){
        RequestsendEmailCustomers(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                new AlertDialog.Builder(pedidoActivity.this)
                        .setTitle("OK! ")
                        .setMessage("Email enviado al administrador!")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                            }
                        }).create().show();
                hidepDialog();
            }

            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(pedidoActivity.this)
                        .setTitle("Error de Permisos! ")
                        .setMessage("Ha ocurrido un error, usted no tiene permisos cambiar la información de Clientes!")
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
                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error")
                            .setMessage(j.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();


                } catch (Exception e) {

                    new AlertDialog.Builder(pedidoActivity.this)
                            .setTitle("Error ")
                            .setMessage("Error al actualizar a este cliente, error desconocido!")
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
                new AlertDialog.Builder(pedidoActivity.this)
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
        }, order_id);
    }


    public void RequestsendEmailCustomers(final VolleyCallback callback, String order) {

        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/orders/v1/sendmail?id="+ order +"&access_token=" + token;


        JsonObjectRequest req = new JsonObjectRequest( Request.Method.GET,  url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hidepDialog();
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
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


    public class AutoCompleteAdapter extends ArrayAdapter<Cliente> implements Filterable {

        public AutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            mDataFiltered = new ArrayList<Cliente>();
            mData = new ArrayList<Cliente>();


        }

        @Override
        public int getCount() {

            if(mDataFiltered!=null){
                return mDataFiltered.size();
            }else{
                return 0;
            }


        }

        @Override
        public Cliente getItem(int index) {
            return mDataFiltered.get(index);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) pedidoActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.simple_dropdown_item_2line, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position).getmIndetificacion());
            ((TextView) convertView.findViewById(R.id.text2)).setText(getItem(position).getmFirstName() + " " + getItem(position).getmLastName());
            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter myFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    final FilterResults filterResults = new FilterResults();
                    if (constraint != null) {

                        String query = constraint.toString().toLowerCase();

                      //  Log.d("query",query);

                        // findCustomers(query);


                        ArrayList<Cliente> list = mData;

                        int count = mData.size();
                        final ArrayList<Cliente> nlist = new ArrayList<>();

                        for (int i = 0; i < count; i++) {
                             Cliente  c = list.get(i);




                            if (
                                    c.getmLastName().toLowerCase().contains(query) ||
                                            c.getmFirstName().toLowerCase().contains(query) ||
                                            c.getmIndetificacion().toLowerCase().contains(query)


                                    ) {
                                nlist.add(c);

                            }else{
                                //Log.e("query","no contiene");
                            }
                        }



                        Log.e("query",nlist.toString());

                        if(nlist.size()>0){
                            filterResults.values = nlist;
                            filterResults.count = nlist.size();
                        }else{
                            filterResults.values = null ;
                            filterResults.count = 0;
                        }



                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence contraint, FilterResults results) {


                    if (results != null && results.count > 0) {
                        mDataFiltered = (ArrayList<Cliente>) results.values;
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();

                    }
                }
            };
            return myFilter;
        }
    }


    private void findCustomers(String query) {


    }


    private void loadPedidoItemsinTable() {

        tlayout.removeAllViews();


        Context applicationContext = pedidoActivity.this;

        TableRow separator = new TableRow(pedidoActivity.this);
        View line = new View(pedidoActivity.this);
        TableRow.LayoutParams separatorLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1);

        separatorLayoutParams.setMargins(2, 0, 2, 0);
//        separatorLayoutParams.span = 7;
        line.setBackgroundColor(Color.rgb(144, 144, 144));

        separator.addView(line, separatorLayoutParams);

        tlayout.addView(separator);


        TableRow row = new TableRow(applicationContext);
        TextView text1 = new TextView(applicationContext);
        TextView text2 = new TextView(applicationContext);
        TextView text3 = new TextView(applicationContext);
        TextView text4 = new TextView(applicationContext);
        TextView text5 = new TextView(applicationContext);
        TextView text6 = new TextView(applicationContext);
        TextView text7 = new TextView(applicationContext);
        TextView text8 = new TextView(applicationContext);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

        text1.setText("DESCR.");
        text1.setLayoutParams(layoutParams);
        text1.setGravity(1);
        row.addView(text1);


        text2.setText("P. UNIT.");
        text2.setLayoutParams(layoutParams);
        text2.setGravity(1);
        row.addView(text2);

        text3.setText("CANT");
        text3.setLayoutParams(layoutParams);
        text3.setGravity(1);
        row.addView(text3);

        text4.setText("SUBT.");
        text4.setLayoutParams(layoutParams);
        text4.setGravity(1);
        row.addView(text4);

        text5.setText("DESC.");
        text5.setLayoutParams(layoutParams);
        text5.setGravity(1);
        row.addView(text5);

        text6.setText("SUB - DESC.");
        text6.setLayoutParams(layoutParams);
        text6.setGravity(1);
        row.addView(text6);

        text7.setText("IVA");
        text7.setLayoutParams(layoutParams);
        text7.setGravity(1);
        row.addView(text7);

        text8.setText("TOTAL");
        text8.setLayoutParams(layoutParams);
        text8.setGravity(1);
        row.addView(text8);

        tlayout.addView(row);


        separator = new TableRow(pedidoActivity.this);
        line = new View(pedidoActivity.this);
        separatorLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1);
        separatorLayoutParams.setMargins(2, 0, 5, 0);
        separatorLayoutParams.span = 5;
        line.setBackgroundColor(Color.rgb(144, 144, 144));
        separator.addView(line, separatorLayoutParams);
        tlayout.addView(separator);


        try {


            List<CartDetails> cartDetailsItems = cartHelper.getCart();


            for (CartDetails cartDetails : cartDetailsItems
                    ) {
                row = new TableRow(applicationContext);
                text1 = new TextView(applicationContext);
                text2 = new TextView(applicationContext);
                text3 = new TextView(applicationContext);
                text4 = new TextView(applicationContext);
                text5 = new TextView(applicationContext);
                text6 = new TextView(applicationContext);
                text7 = new TextView(applicationContext);
                text8 = new TextView(applicationContext);


                text1.setText(String.valueOf(cartDetails.getmShortName()));
                text1.setGravity(1);
                row.addView(text1);


                text2.setText(String.valueOf(cartDetails.getmUnitPrice()));
                text2.setGravity(1);
                row.addView(text2);


                text3.setText(String.valueOf(cartDetails.getmQuantity()));
                text3.setGravity(1);
                row.addView(text3);

                text4.setText(String.valueOf(cartDetails.getmSubtotal()));
                text4.setGravity(1);
                row.addView(text4);

                text5.setText(String.valueOf(cartDetails.getmDiscount()));
                text5.setGravity(1);
                row.addView(text5);

                text6.setText(String.valueOf(cartDetails.getmSubtotalWithDiscount()));
                text6.setGravity(1);
                row.addView(text6);

                text7.setText(String.valueOf(cartDetails.getmTaxesTotal()));
                text7.setGravity(1);
                row.addView(text7);

                text8.setText(String.valueOf(cartDetails.getmTotal()));
                text8.setGravity(1);
                row.addView(text8);

                tlayout.addView(row);

                separator = new TableRow(pedidoActivity.this);
                line = new View(pedidoActivity.this);
                separatorLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1);
                separatorLayoutParams.setMargins(2, 0, 2, 0);
                separatorLayoutParams.span = 5;
                line.setBackgroundColor(Color.rgb(144, 144, 144));

                separator.addView(line, separatorLayoutParams);
                tlayout.addView(separator);

            }

            Cart cartHeader = cartHelper.getCartHeader();


            String desc_type = cartHeader.getmDiscountType();


            if (desc_type.equals(cartHelper.DISCOUNT_PERCENT)) {
                desc_type = "%";
                mRemove_coupon.setVisibility(View.VISIBLE);

            } else if (desc_type.equals(cartHelper.DISCOUNT_FIXED_CART)) {
                desc_type = "$";
                mRemove_coupon.setVisibility(View.VISIBLE);

            } else {
                desc_type = "";
                mRemove_coupon.setVisibility(View.GONE);

            }

            mDescountValueTextView.setText(String.valueOf(cartHeader.getmDiscount()));
            mDescountTypeTextView.setText(desc_type);
            mSubtotal_with_taxes0TextView.setText(String.valueOf(cartHeader.getmSubtotalWithTaxes0()));
            mSubtotal_with_taxesTextView.setText(String.valueOf(cartHeader.getmSubtotalWithTaxes()));
            //mTaxesValue.setText("%" + String.valueOf(cartHeader.getmTaxesPercentage()));
            mtotalPedidoTextView.setText(String.valueOf(cartHeader.getmTotal()));

        } catch (
                Exception e
                )

        {
            e.printStackTrace();
            System.err.print(e.getMessage());

        }


    }


    private void setValuestoObject() {
        client = new Cliente();

        try {
            client.setmId(Integer.valueOf(mId.getText().toString()));
        } catch (Exception ex) {

        }
        client.setmIndetificacion(mIdentificacion.getText().toString());
        client.setmFirstName(mFirstNameEditText.getText().toString());
        client.setmLastName(mLastNameEditText.getText().toString());
        client.setmAddress1(mAddress1EditText.getText().toString());
        client.setmAddress2(mAddress2EditText.getText().toString());
        client.setmCity(mCiudadEditText.getText().toString());
        client.setmState(mState.getText().toString());
        client.setmCounty((Country) mCountrySpinner.getSelectedItem());
        client.setmEmail(mEmailEditText.getText().toString());
        client.setmPhone(mPhoneEditText.getText().toString());
        client.setmPostCode(mPostCode.getText().toString());
    }

    private void clearFields() {
        mClienteAutoCompleteView.setText("");
        mId.setText("");
        mIdentificacion.setText("");
        mFirstNameEditText.setText("");
        mLastNameEditText.setText("");
        mAddress1EditText.setText("");
        mAddress2EditText.setText("");
        mCiudadEditText.setText("");
        mState.setText("");
        mEmailEditText.setText("");
        mPhoneEditText.setText("");
        mPostCode.setText("");
    }

    private void setvaluesToTextField() {

        Log.e("cliente","postCode: "+ client.getmPostCode());

        mId.setText(String.valueOf(client.getmId()));
        mIdentificacion.setText(client.getmIndetificacion());
        mFirstNameEditText.setText(client.getmFirstName());
        mLastNameEditText.setText(client.getmLastName());
        mAddress1EditText.setText(client.getmAddress1());
        mAddress2EditText.setText(client.getmAddress2());
        mCiudadEditText.setText(client.getmCity());


        int spinnercountriesPosition = spinnerArrayCountriesAdapter.getPosition(client.getmCounty());


        mState.setText(client.getmState());
        mCountrySpinner.setSelection(spinnercountriesPosition);
        mEmailEditText.setText(client.getmEmail());
        mPhoneEditText.setText(client.getmPhone());
        mPostCode.setText(client.getmPostCode());

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

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {

        try {
            if (pDialog.isShowing())
                pDialog.dismiss();
        } catch (Exception ex) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cart, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, OrderListActivity.class));
            return true;
        }

        if (id == R.id.show_menu) {
            this.finish();
            startActivity(new Intent(this, MenuActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        finish();
    }

}