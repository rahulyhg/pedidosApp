package com.sudamericano.tesis.pedidosapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;
import com.sudamericano.tesis.pedidosapp.Cart.CartHelper;
import com.sudamericano.tesis.pedidosapp.Model.AttributesProduct;
import com.sudamericano.tesis.pedidosapp.Model.Categoria;
import com.sudamericano.tesis.pedidosapp.Model.Producto;
import com.sudamericano.tesis.pedidosapp.Utils.EndlessRecyclerViewScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

/**
 * An activity representing a list of Productos. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ProductoDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ProductoListActivity extends AppCompatActivity {


    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private int MY_SOCKET_TIMEOUT_MS = 30000;
    private List<Producto> productoList;
    private ProgressDialog pDialog;


    String DOMAIN;
    private ArrayList<AttributesProduct> attributesList;
    private ArrayList<String> attributeOptionsList;
    private Attributes attribute;
    CartHelper cartHelper;
    public static final String MyPREFERENCES = "account";
    SharedPreferences sharedpreferences;
    View recyclerView;
    private SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter;

    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_list);
        cartHelper = new CartHelper(getBaseContext());


        DOMAIN = getResources().getString(R.string.DOMAIN);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        recyclerView = (RecyclerView) findViewById(R.id.producto_list);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        productoList = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        if (findViewById(R.id.producto_detail_container) != null) {
            mTwoPane = true;
        }

        simpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(productoList);

         gridLayoutManager = new GridLayoutManager(getBaseContext(), 2);

        ((RecyclerView) recyclerView).setAdapter(simpleItemRecyclerViewAdapter);
        ((RecyclerView) recyclerView).setLayoutManager(gridLayoutManager);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        ((RecyclerView) recyclerView).setLayoutManager(linearLayoutManager);

        ((RecyclerView) recyclerView).addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                LoadMoreDataFromApi(page);
            }
        });

        cargarProductos("", 0);
    }

    private void LoadMoreDataFromApi(int page) {
        cargarProductos("", page);
    }

    private void cargarProductos(final String q, int page) {

        //   showpDialog();
        requestProductos(new VolleyCallback() {
            @Override
            public void onSuccess(JSONArray product_categories) {
                try {

                    hidepDialog();
                    Producto producto;
                    List<Categoria> categoriesList;
                    Categoria c;
                    double regular_price = 0.0;
                    double sale_price = 0.0;
                    double price = 0.0;

                    for (int i = 0; i < product_categories.length(); i++) {

                        categoriesList = new ArrayList<Categoria>();
                        attributesList = new ArrayList<AttributesProduct>();
                        attributeOptionsList = new ArrayList<String>();

                        System.err.print(i);
                        JSONObject jsonObject1 = product_categories.getJSONObject(i);
                        int id = (Integer) jsonObject1.get("id");
                        String title = (String) jsonObject1.get("name");
                        String sku = (String) jsonObject1.get("sku");
                        String slug = (String) jsonObject1.get("slug");
                        String tax_status = (String) jsonObject1.get("tax_status");
                        //   String tax_class = (String) jsonObject1.get("tax_class");


                        String description = (String) jsonObject1.get("description");
                        Object created_at_date = (Object) jsonObject1.get("date_created");

                        String created_at = created_at_date.toString();

                        Float average_rating = Float.parseFloat((String) jsonObject1.get("average_rating"));

                        try {
                            regular_price = Double.valueOf(jsonObject1.get("regular_price").toString());
                        } catch (Exception ex) {
                            // Log.e("error1", ex.toString());
                        }
                        try {
                            regular_price = Double.valueOf(jsonObject1.get("sale_price").toString());
                        } catch (Exception ex) {
                            //Log.e("error2", ex.toString());
                        }
                        JSONArray jsonarrayimages = (JSONArray) jsonObject1.get("images");
                        JSONArray jsonarrayCategories = (JSONArray) jsonObject1.get("categories");

                        for (int x = 0; x < jsonarrayCategories.length(); x++) {

                            JSONObject jsonArraycategorias = (JSONObject) jsonarrayCategories.get(x);
                            c = new Categoria(jsonArraycategorias.getString("name"));
                            categoriesList.add(c);
                        }

                        JSONArray jsonarrayAttributes = (JSONArray) jsonObject1.get("attributes");


                        for (int x = 0; x < jsonarrayAttributes.length(); x++) {

                            JSONObject jsonObjectAttributes = (JSONObject) jsonarrayAttributes.get(x);

                            String name = jsonObjectAttributes.get("name").toString();
                            String visible = jsonObjectAttributes.get("visible").toString();
                            JSONArray jsonarrayOptions = (JSONArray) jsonObjectAttributes.get("options");

                            for (int y = 0; y < jsonarrayOptions.length(); y++) {
                                String value = (String) jsonarrayOptions.get(y);
                                attributeOptionsList.add(value);
                            }
                            AttributesProduct attributesProduct = new AttributesProduct(name, slug, visible, attributeOptionsList);


                            attributesList.add(attributesProduct);

                        }
                        if (sale_price > 0) {
                            price = sale_price;
                        } else {
                            price = regular_price;
                        }

                        int stock_quantity = 0;
                        try {
                            stock_quantity = (int) jsonObject1.get("stock_quantity");

                        } catch (Exception ex) {

                        }

                        JSONObject jsonObjectImagen = jsonarrayimages.getJSONObject(0);

                        String image = jsonObjectImagen.get("src").toString();


                        boolean isTaxable = false;



                        if (tax_status.equals(cartHelper.IS_TAXABLE)) {
                            isTaxable = true;
                        }

                        producto = new Producto(id, title, created_at, sku, price, stock_quantity, image, description, isTaxable);
                        producto.setAverageRating(average_rating);

                        producto.setmCategorias(categoriesList);
                        productoList.add(producto);
                        int curSize = simpleItemRecyclerViewAdapter.getItemCount();
                        simpleItemRecyclerViewAdapter.notifyItemRangeInserted(curSize, productoList.size() - 1);
                    }


                    if (!q.isEmpty()) {
                        simpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(productoList);
                        ((RecyclerView) recyclerView).setAdapter(simpleItemRecyclerViewAdapter);
                    }


                } catch (JSONException e) {
                    Log.d("error", e.toString());
                    e.printStackTrace();
                }

                simpleItemRecyclerViewAdapter.notifyDataSetChanged();




             /*   OrdersRecyclerViewAdapter simpleItemRecyclerViewAdapter = new OrdersRecyclerViewAdapter(productoList);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
                // ((RecyclerView) recyclerView).setLayoutManager(linearLayoutManager);
                ((RecyclerView) recyclerView).setAdapter(simpleItemRecyclerViewAdapter);
                ((RecyclerView) recyclerView).setLayoutManager(new GridLayoutManager(getBaseContext(), 2));

              */


                hidepDialog();
            }

            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(ProductoListActivity.this)
                        .setTitle("Error de autentificación ")
                        .setMessage("Ha ocurrido un error, usted no tiene permisos para acceder a esta información")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ProductoListActivity.this.finish();
                                startActivity(new Intent(ProductoListActivity.this, MenuActivity.class));
                            }
                        }).create().show();
                hidepDialog();

            }

            @Override
            public void onServerError() {

                new AlertDialog.Builder(ProductoListActivity.this)
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
                new AlertDialog.Builder(ProductoListActivity.this)
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
        }, q, page);

    }


    public void requestProductos(final VolleyCallback callback, String q, int page) {
        String DOMAIN = getResources().getString(R.string.DOMAIN);
        String query = "";
        try {
            query = URLEncoder.encode(q, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/wc/v1/products?search=" + query + "&page=" + (page + 1) + "&access_token=" + token;


        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
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
                    callback.onServerError();
                } else if (error instanceof NetworkError) {
                    callback.onFailureConnection();
                } else if (error instanceof ParseError) {
                    callback.onServerError();
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


    public interface VolleyCallback {
        void onSuccess(JSONArray result);

        void onAuthFailureError();

        void onServerError();

        void onFailureConnection();
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        List<Producto> productoList;

        public SimpleItemRecyclerViewAdapter(List<Producto> productoList) {
            this.productoList = productoList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.producto_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.mNombreView.setText(productoList.get(position).getmTitle()+ " / (" + productoList.get(position).getmStock_quantity() + " en stock )");


            List<Categoria> categorias = productoList.get(position).getmCategorias();

            try {
                Categoria categoria = categorias.get(0);
                holder.mCategoria.setText(categoria.getmNombre());

            } catch (Exception e) {

            }

            // holder.mCategoria.setText();
            holder.mPrecioView.setText(String.valueOf(productoList.get(position).getmPrice()) );

            Float averageRating = productoList.get(position).getAverageRating();

            holder.mRaitingVar.setRating(averageRating);

            String url = productoList.get(position).getmUrlImage();

            Picasso.with(getBaseContext()).load(url).into(holder.mImagenView);


            holder.mItem = productoList.get(position);

//            holder.mAddToCartButton.setVisibility(View.VISIBLE);

//            if (holder.mItem.getmStock_quantity() <= 0) {
//                holder.mAddToCartButton.setVisibility(View.GONE);
//            }

//            //final CartDetails product = cartHelper.getProduct(holder.mItem.getmId());
//
//            if (product != null) {
//              //  holder.mAddToCartButton.setVisibility(View.GONE);
//            }


//            holder.mAddToCartButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//
//                    Producto producto = holder.mItem;
//                    boolean inserted = cartHelper.insertProduct(producto, 1);
//
//                    if (inserted) {
//
//                        Toast.makeText(getBaseContext(), "El producto fue agregado correctamente al carrito de compras", Toast.LENGTH_LONG).show();
//
//                    } else {
//                        Toast.makeText(getBaseContext(), "El producto no pudo ser agregado, verifique si ya existe en el carrito", Toast.LENGTH_LONG).show();
//
//
//                    }
//                    holder.mAddToCartButton.setVisibility(View.GONE);
//
//
//                }
//            });


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ProductoDetailFragment.ARG_ITEM_ID, String.valueOf(holder.mItem.getmId()));
                        arguments.putSerializable(ProductoDetailFragment.ARG_ITEM_PRODUCT, holder.mItem);
                        ProductoDetailFragment fragment = new ProductoDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.producto_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ProductoDetailActivity.class);
                        intent.putExtra(ProductoDetailFragment.ARG_ITEM_ID, String.valueOf(holder.mItem.getmId()));
                        intent.putExtra(ProductoDetailFragment.ARG_ITEM_PRODUCT, holder.mItem);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return productoList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mNombreView;
            public final TextView mCategoria;
            public final TextView mPrecioView;
            public final ImageView mImagenView;
            public final RatingBar mRaitingVar;
            Button mAddToCartButton;
            public Producto mItem;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                mNombreView = (TextView) view.findViewById(R.id.nombre);
                mImagenView = (ImageView) view.findViewById(R.id.imagen);
                mPrecioView = (TextView) view.findViewById(R.id.precio);
                mCategoria = (TextView) view.findViewById(R.id.categoria);
                mRaitingVar = (RatingBar) view.findViewById(R.id.ratingBar);
                mAddToCartButton = (Button) view.findViewById(R.id.add_to_cart);


            }

            @Override
            public String toString() {
                return super.toString();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tab, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                Log.d("Intent","expand");

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                Log.d("Intent","collapse");

                productoList = new ArrayList<>();
                simpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(productoList);

                simpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(productoList);

                gridLayoutManager = new GridLayoutManager(getBaseContext(), 2);

                ((RecyclerView) recyclerView).setAdapter(simpleItemRecyclerViewAdapter);
                ((RecyclerView) recyclerView).setLayoutManager(gridLayoutManager);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
                ((RecyclerView) recyclerView).setLayoutManager(linearLayoutManager);

                ((RecyclerView) recyclerView).addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount) {
                        LoadMoreDataFromApi(page);
                    }
                });

                showpDialog();

                doMySearch(" ");

                return true;
            }
        });


        return true;
    }


    @Override
    protected void onResume() {
        Log.d("Intent","resume");

        //  handleIntent(getIntent());
        super.onResume();


    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e("intent", "is a new intent");
        // setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        Log.d("Intent",intent.toString() + "");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            productoList = new ArrayList<>();
            simpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(productoList);

            String query = intent.getStringExtra(SearchManager.QUERY);
            showpDialog();
            doMySearch(query);
        }
        // simpleItemRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void doMySearch(String query) {
        cargarProductos(query, 0);

    }

    @Override
    public boolean onSearchRequested() {
        Log.d("Intent","onSearchRequested");

        return super.onSearchRequested();
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
            NavUtils.navigateUpTo(this, new Intent(this, ProductoListActivity.class));
            return true;
        }

        if (id == R.id.carrito) {
            startActivity(new Intent(ProductoListActivity.this, CarritoItemListActivity.class));

        }


        if (id == R.id.showPedidos) {
            // this.finish();
            startActivity(new Intent(this, MenuActivity.class));

        }
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


    @Override
    public void onBackPressed() {

        Log.d("alert","back");

        finish();
    }


}