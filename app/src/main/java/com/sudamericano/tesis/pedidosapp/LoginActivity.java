package com.sudamericano.tesis.pedidosapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    public static final String MyPREFERENCES = "account";
    SharedPreferences sharedpreferences;
    private int MY_SOCKET_TIMEOUT_MS = 3000;

    String DOMAIN;
    String OAUTHUSERNAME;
    String OAUTHSECRET;
    private String username = "";
    private String password = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Espere por favor...");
        pDialog.setCancelable(false);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button mLoginInButton = (Button) findViewById(R.id.btn_login);
        final EditText mUsername = (EditText) findViewById(R.id.username_or_email);
        final EditText mPassword = (EditText) findViewById(R.id.password);
        final TextView mRecovery = (TextView) findViewById(R.id.recovery_password);


        final String url = "http://arboreng.com/infog/pedidosapp/mi-cuenta/lost-password/";

        mRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });


        if (isLogged(sharedpreferences)) {
            LoginActivity.this.finish();
            startActivity(new Intent(LoginActivity.this, MenuActivity.class));
        }
        mLoginInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isNetworkAvailable()) {

                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Error")
                            .setMessage("Usted no cuenta con una conexión a internet, verifique e intente nuevamente")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                    return;

                }
                showpDialog();
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();
                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(username)) {
                    mUsername.setError(getString(R.string.error_invalid_email));
                    hidepDialog();
                    return;

                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError(getString(R.string.error_invalid_password));
                    hidepDialog();

                    return;

                }

                login(username, password);
            }
        });


    }

    private void login(String username, String password) {
        loginWoocommerce(new VolleyCallback() {
            @Override
            public void onSuccess(String token) {
                Log.d("token", token);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                try {
                    JSONObject reader = new JSONObject(token);
                    String access_token = reader.getString("access_token");
                    String expires_in = reader.getString("expires_in");
                    String refresh_token = reader.getString("refresh_token");
                    String token_type = reader.getString("token_type");
                    String scope = reader.getString("scope");


                    editor.putString("access_token", access_token);
                    editor.putString("expires_in", expires_in);
                    editor.putString("token_type", token_type);
                    editor.putString("scope", scope);
                    editor.putString("refresh_token", refresh_token);

                    editor.commit();


                    getUserInfo();


                } catch (JSONException e) {
                    Log.e("error", e.getMessage());
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Error en el servidor  ")
                            .setMessage("La respuesta del servidor no fue la esperada!")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }


            }

            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Error al Iniciar Sesión ")
                        .setMessage("Sus datos no son correctos, por favor intente nuevamente")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).create().show();

                hidepDialog();
            }

            @Override
            public void onServerError() {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Error en el servidor ")
                        .setMessage("Ha ocurrido un error, por favor intente nuevamente")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).create().show();
                hidepDialog();
            }

            @Override
            public void onFailureConnection() {
                new AlertDialog.Builder(LoginActivity.this)
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
        }, username, password);

    }

    private void getUserInfo() {
        String username = this.username;
        requestUserInfo(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {


                Log.d("response info", response);

                try {
                    JSONObject reader = new JSONObject(response);
                    String id = reader.getString("id");
                    String display_name = reader.getString("display_name");
                    String roles = reader.getString("roles");
                    String email = reader.getString("email");
                    String permissions = reader.getString("permissions");

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("id", id);
                    editor.putString("display_name", display_name);
                    editor.putString("roles", roles);
                    editor.putString("email", email);
                    editor.putString("permissions", permissions);

                    editor.commit();

                    LoginActivity.this.finish();
                    startActivity(new Intent(LoginActivity.this, MenuActivity.class));


                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    hidepDialog();
                }
            }

            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Error de autentificación ")
                        .setMessage("Ha ocurrido un error, usted no tiene permisos para acceder a esta aplicación")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).create().show();

                hidepDialog();
            }

            @Override
            public void onServerError() {
                new AlertDialog.Builder(LoginActivity.this)
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
                new AlertDialog.Builder(LoginActivity.this)
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
        }, username);

    }

    private void loginWoocommerce(final VolleyCallback callback, final String username, final String password) {
        DOMAIN = getResources().getString(R.string.DOMAIN);
        OAUTHUSERNAME = getResources().getString(R.string.OAUTHUSERNAME);
        OAUTHSECRET = getResources().getString(R.string.OAUTHSECRET);
        String url = (DOMAIN + "?oauth=token");

        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Log.d("login app", volleyError.toString());

                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError errores = new VolleyError(new String(volleyError.networkResponse.data));
                    Log.v("errores2", errores.toString());

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
                params.put("client_id", OAUTHUSERNAME);
                params.put("client_secret", OAUTHSECRET);
                params.put("username", username);
                params.put("password", password);
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

    private void requestUserInfo(final VolleyCallback callback, final String username) {

        String token = (sharedpreferences.getString("access_token", ""));

        String url = (DOMAIN + "/wp-json/getinfo/v1/username/" + username + "?access_token=" + token);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("login app", response);
                callback.onSuccess(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Log.d("login app", volleyError.toString());

                        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                            VolleyError errores = new VolleyError(new String(volleyError.networkResponse.data));
                            Log.v("errores2", errores.toString());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private boolean isLogged(SharedPreferences sharedpreferences) {
        if (sharedpreferences.contains("access_token")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
