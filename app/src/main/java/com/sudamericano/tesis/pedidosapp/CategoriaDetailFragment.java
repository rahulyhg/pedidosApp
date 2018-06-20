package com.sudamericano.tesis.pedidosapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONObject;

import com.sudamericano.tesis.pedidosapp.Model.Categoria;

/**
 * A fragment representing a single Categoria detail screen.
 * This fragment is either contained in a {@link CategoriaListActivity}
 * in two-pane mode (on tablets) or a {@link CategoriaDetailActivity}
 * on handsets.
 */
public class CategoriaDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Categoria mItem;
    private ProgressDialog pDialog;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CategoriaDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            Log.d("debug2",getArguments().getString(ARG_ITEM_ID));

            int nid = Integer.parseInt(getArguments().getString(ARG_ITEM_ID));

            showpDialog();
            getCategorias(new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject jsonObject) {


                    System.err.print(jsonObject.toString());


                    hidepDialog();
                }

                @Override
                public void onAuthFailureError() {
                    hidepDialog();
                }

                @Override
                public void onServerError() {
                    hidepDialog();
                    Toast.makeText(getActivity(), "Ha ocurrido un error en el servidor, intente más tarde", Toast.LENGTH_LONG);
                }

                @Override
                public void onFailureConnection() {
                    hidepDialog();
                    Toast.makeText(getActivity(), "No se ha podido conectar con servidor, verifique su conexión e intente nuevamente!", Toast.LENGTH_LONG);
                }
            },nid);


            // mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            mItem = new Categoria(1, "a", "b", null, "c", null);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getmNombre());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.categoria_detail, container, false);
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.categoria_detail)).setText(mItem.getmDescripcion());
        }
        return rootView;
    }

    private int MY_SOCKET_TIMEOUT_MS = 30000;

    final static String CONSUMER_KEY = "ck_9d387486e469ebc84066c9214e05764bec5c422e";
    final static String CONSUMER_SECRET_KEY = "cs_3bafde5bc41b6626eff8dc6b77d636c5e4058f6e";
    final static String DOMAIN = "https://lamotora.com";

    public void getCategorias(final VolleyCallback callback,int id) {

        String url = DOMAIN + "/wc-api/v3/products/categories/"+ id +"?consumer_key=" + CONSUMER_KEY + "&consumer_secret=" + CONSUMER_SECRET_KEY;

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
                            callback.onServerError();
                        } else if (error instanceof NetworkError) {
                            callback.onFailureConnection();
                        } else if (error instanceof ParseError) {
                            callback.onServerError();
                        }
                    }

                }
        );
        requestCategorias.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        mRequestQueue.add(requestCategorias);


    }

    public interface VolleyCallback {
        void onSuccess(JSONObject result);

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
