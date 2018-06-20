package com.sudamericano.tesis.pedidosapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sudamericano.tesis.pedidosapp.Model.Categoria;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CategoriaListActivity extends AppCompatActivity {


    private boolean mTwoPane;
    private List<Categoria> categoriaList;
    private ProgressDialog pDialog;

    String CONSUMER_KEY;

    String CONSUMER_SECRET_KEY;
    String DOMAIN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        CONSUMER_KEY = getResources().getString(R.string.CONSUMER_KEY);
        CONSUMER_SECRET_KEY = getResources().getString(R.string.CONSUMER_SECRET_KEY);
        DOMAIN = getResources().getString(R.string.DOMAIN);

        setContentView(R.layout.activity_categoria_list);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        categoriaList = new ArrayList<>();

        showpDialog();
        getCategorias(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

                Categoria categoria;
                try {
                    JSONArray product_categories = jsonObject.getJSONArray("product_categories");

                    for (int i = 0; i < product_categories.length(); i++) {
                        System.err.print(i);
                        JSONObject jsonObject1 = product_categories.getJSONObject(i);
                        int id = (Integer) jsonObject1.get("id");
                        String name = (String) jsonObject1.get("name");
                        String slug = (String) jsonObject1.get("slug");
                        String description = (String) jsonObject1.get("description");
                        String image = (String) jsonObject1.get("image");
                        categoria = new Categoria(id, name, slug, null, description, image);
                        categoriaList.add(categoria);
                    }


                } catch (JSONException e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }


                SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(categoriaList);
                View recyclerView = findViewById(R.id.categoria_list);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
                ((RecyclerView) recyclerView).setLayoutManager(linearLayoutManager);
                ((RecyclerView) recyclerView).setAdapter(simpleItemRecyclerViewAdapter);

                hidepDialog();
            }

            @Override
            public void onAuthFailureError() {
                hidepDialog();
            }

            @Override
            public void onServerError() {
                hidepDialog();
                Toast.makeText(getBaseContext(), "Ha ocurrido un error en el servidor, intente más tarde", Toast.LENGTH_LONG);
            }

            @Override
            public void onFailureConnection() {
                hidepDialog();
                Toast.makeText(getBaseContext(), "No se ha podido conectar con servidor, verifique su conexión e intente nuevamente!", Toast.LENGTH_LONG);
            }
        });


        if (findViewById(R.id.categoria_detail_container) != null) {
            mTwoPane = true;
        }


    }


    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        List<Categoria> categoriaList;


        public SimpleItemRecyclerViewAdapter(List<Categoria> categoriaList) {
            this.categoriaList = categoriaList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.categoria_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.mIdView.setText(categoriaList.get(position).getmNombre());

            String url = categoriaList.get(position).getmImage();


            ImageRequest request = new ImageRequest(url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            holder.mContentView.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            holder.mContentView.setImageBitmap(null);
                        }
                    });

            RequestQueue mRequestQueue;
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();
            mRequestQueue.add(request);


            //  holder.mContentView.setImageBitmap(img);


            holder.mItem = categoriaList.get(position);


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(CategoriaDetailFragment.ARG_ITEM_ID, String.valueOf(holder.mItem.getmId()));
                        CategoriaDetailFragment fragment = new CategoriaDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.categoria_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, CategoriaDetailActivity.class);
                        Log.d("debug",holder.mItem.getmSlug());
                        Log.d("debug", String.valueOf(holder.mItem.getmId()));
                        intent.putExtra(CategoriaDetailFragment.ARG_ITEM_ID, String.valueOf(holder.mItem.getmId()));

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return categoriaList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final ImageView mContentView;
            public Categoria mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (ImageView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return this.mItem.getmNombre();
            }
        }
    }



    private int MY_SOCKET_TIMEOUT_MS = 30000;



    public void getCategorias(final VolleyCallback callback) {

        String url = DOMAIN + "/wc-api/v3/products/categories?consumer_key=" + CONSUMER_KEY + "&consumer_secret=" + CONSUMER_SECRET_KEY;

        Log.d("DOMAIN",url);
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
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
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


    @Override
    public void onBackPressed() {

        finish();
    }

}




