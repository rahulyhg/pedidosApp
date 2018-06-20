package com.sudamericano.tesis.pedidosapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sudamericano.tesis.pedidosapp.Cart.Cart;
import com.sudamericano.tesis.pedidosapp.Cart.CartDetails;
import com.sudamericano.tesis.pedidosapp.Cart.CartHelper;
import com.sudamericano.tesis.pedidosapp.Model.AttributesProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;


public class CarritoItemListActivity extends AppCompatActivity {


    private static final int VIEW_TYPE_FOOTER = 0;
    private static final int VIEW_TYPE_CELL = 1;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private int MY_SOCKET_TIMEOUT_MS = 30000;

    private ProgressDialog pDialog;

    SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter;
    String DOMAIN;
    public static final String MyPREFERENCES = "account";
    SharedPreferences sharedpreferences;
    private ArrayList<AttributesProduct> attributesList;
    private ArrayList<String> attributeOptionsList;
    private Attributes attribute;

    List<CartDetails> cartItems;
    private Button mRealizarPedido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito_item_list);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);


        DOMAIN = getResources().getString(R.string.DOMAIN);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        CartHelper cartHelper = new CartHelper(getBaseContext());

        cartItems = cartHelper.getCart();


        simpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(cartItems);
        View recyclerView = findViewById(R.id.item_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        ((RecyclerView) recyclerView).setLayoutManager(linearLayoutManager);
        ((RecyclerView) recyclerView).setAdapter(simpleItemRecyclerViewAdapter);


        int cartCount = cartHelper.getCartCount();





        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
         mRealizarPedido = (Button) findViewById(R.id.realizar_pedido);


        mRealizarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getBaseContext(), pedidoActivity.class));


            }
        });
        updateTotals();

        if (cartCount == 0) {

            Toast.makeText(getBaseContext(), "No hay productos en su carrito", Toast.LENGTH_LONG);

            mRealizarPedido.setEnabled(false);

        }else{
            mRealizarPedido.setEnabled(true);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.show_menu) {
            // this.finish();
            startActivity(new Intent(this, MenuActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }


    private void updateTotals() {
        TextView mNumeroItems = (TextView) findViewById(R.id.nro_items);
        TextView mTotal = (TextView) findViewById(R.id.total);

        CartHelper cartHelper = new CartHelper(getBaseContext());
        int nroItems = cartHelper.getCartCount();
        Cart cart = cartHelper.getCartHeader();

        double totalCart = 0;

        if (cart != null) {
            totalCart = cart.getmTotal();
        }
        mTotal.setText("Total: $" + (totalCart));
        mNumeroItems.setText("Cantidad: " + nroItems + " productos. ");
    }


    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>  {

        List<CartDetails> cartDetails;

        public SimpleItemRecyclerViewAdapter(List<CartDetails> cartDetails) {
            this.cartDetails = cartDetails;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }


        int id = 0;
        String name;
        double unitPrice;
        int quantity;
        String url;
        int stock;
        double subtotal;

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {



            final CartDetails cartDetails = cartItems.get(position);


            id = cartDetails.getmProductoId();

            Log.e("onholder","id="+id);

            name = cartDetails.getmName();
            unitPrice = cartDetails.getmUnitPrice();
            quantity = cartDetails.getmQuantity();
            url = cartDetails.getmImageUrl();
            stock = cartDetails.getmStockQuantity();
            subtotal = cartDetails.getmSubtotal();


            holder.mNombreView.setText(name);
            holder.mPrecioView.setText(String.valueOf(unitPrice) + " X (" + quantity + ") = " + (subtotal));
            holder.mNumberPicker.setValue(quantity);
            try {
                holder.mNumberPicker.setMaxValue(stock);

            } catch (Exception ex) {
                holder.mNumberPicker.setMaxValue(0);
            }
            holder.mNumberPicker.setMinValue(1);

            holder.mItem = cartDetails;

            Picasso.with(getBaseContext()).load(url).into(holder.mImagenView);
            final CartHelper cartHelper = new CartHelper(getBaseContext());

            holder.mPrecioView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mNumberPicker.setVisibility(View.VISIBLE);
                    holder.mSaveItem.setVisibility(View.VISIBLE);
                    holder.mRemoveItem.setVisibility(View.GONE);
                }
            });


            holder.mRemoveItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                    try {

                        final CartDetails cartDetails = cartItems.get(position);
                        id = cartDetails.getmProductoId();
                        name = cartDetails.getmName();


                        boolean deleted = cartHelper.deleteProduct(id);
                        if (deleted) {
                            cartItems.remove(position);
                            Toast.makeText(getBaseContext(), "El producto fue removido correctamente al carrito de compras", Toast.LENGTH_SHORT).show();
                            updateTotals();
                            simpleItemRecyclerViewAdapter.notifyItemRemoved(position);
                            simpleItemRecyclerViewAdapter.notifyItemRangeChanged(position, cartItems.size());


                        } else {
                            Toast.makeText(getBaseContext(), "El producto no pudo ser removido, del carrito", Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {

                        Log.e("carrito error", e.getMessage());

                    }


                }
            });

            holder.mSaveItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int cantidad = holder.mNumberPicker.getValue();
                    CartDetails cartDetails = holder.mItem;

                    boolean updated = cartHelper.updateQuantityProduct(cartDetails.getmProductoId(), cartDetails.getmUnitPrice(), cantidad);
                    if (updated) {
                        Toast.makeText(getBaseContext(), "El producto fue actualizado correctamente!", Toast.LENGTH_SHORT).show();
                        holder.mPrecioView.setText(String.valueOf(unitPrice) + " X (" + cantidad + ") = " + cartHelper.roundValue(unitPrice*cantidad));
//                        simpleItemRecyclerViewAdapter.notifyItemChanged(position);

                        updateTotals();
                    } else {
                        Toast.makeText(getBaseContext(), "El producto no pudo ser cambiado!", Toast.LENGTH_SHORT).show();

                    }
                    holder.mNumberPicker.setVisibility(View.GONE);
                    holder.mSaveItem.setVisibility(View.GONE);
                    holder.mRemoveItem.setVisibility(View.VISIBLE);
                }
            });
        }


        @Override
        public int getItemCount() {
            return cartItems.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mNombreView;
            public final TextView mCategoria;
            public final TextView mPrecioView;
            public final ImageView mImagenView;
            public final NumberPicker mNumberPicker;
            public final ImageButton mRemoveItem;
            //public final ImageButton mUpdateItem;
            public final ImageButton mSaveItem;
            public CartDetails mItem;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                mNombreView = (TextView) view.findViewById(R.id.nombre);
                mImagenView = (ImageView) view.findViewById(R.id.imagen);
                mPrecioView = (TextView) view.findViewById(R.id.precio);
                mCategoria = (TextView) view.findViewById(R.id.categoria);
                mNumberPicker = (NumberPicker) view.findViewById(R.id.quantity);
                mRemoveItem = (ImageButton) view.findViewById(R.id.delete_item);
                //  mUpdateItem = (ImageButton) view.findViewById(R.id.update_item);
                mSaveItem = (ImageButton) view.findViewById(R.id.save_item);
                mNumberPicker.setWrapSelectorWheel(true);


            }

            @Override
            public String toString() {
                return super.toString();
            }
        }


    }

    @Override
    public void onBackPressed() {

        finish();
    }



}