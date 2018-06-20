package com.sudamericano.tesis.pedidosapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sudamericano.tesis.pedidosapp.Cart.CartDetails;
import com.sudamericano.tesis.pedidosapp.Cart.CartHelper;
import com.sudamericano.tesis.pedidosapp.Model.Categoria;
import com.sudamericano.tesis.pedidosapp.Model.Producto;

import java.util.List;

/**
 * A fragment representing a single Producto detail screen.
 * This fragment is either contained in a {@link ProductoListActivity}
 * in two-pane mode (on tablets) or a {@link ProductoDetailActivity}
 * on handsets.
 */
public class ProductoDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_PRODUCT = "product";


    private Producto mItem;
    private Button mAddToCartButton;

    CartHelper cartHelper;
    private Button mRemoveToCartButton;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProductoDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            mItem = (Producto) getArguments().getSerializable(
                    ARG_ITEM_PRODUCT);


            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getmTitle());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.producto_detail, container, false);
        cartHelper = new CartHelper(getContext());


        if (mItem != null) {

            ((TextView) rootView.findViewById(R.id.descripcion)).setText(Html.fromHtml(mItem.getmDescripcion()));
            ((TextView) rootView.findViewById(R.id.precio)).setText(String.valueOf(mItem.getmPrice()));
            ((TextView) rootView.findViewById(R.id.nombre)).setText((String) mItem.getmTitle());
            ((TextView) rootView.findViewById(R.id.stock)).setText(String.valueOf(mItem.getmStock_quantity()) + " en Stock");

            List<Categoria> categorias = mItem.getmCategorias();
            String categorias_string = "";
            for (int x = 0; x < categorias.size(); x++) {
                Categoria categoria = categorias.get(x);
                String nombre = categoria.getmNombre();
                categorias_string += nombre + ",";
            }


            ((TextView) rootView.findViewById(R.id.categoria)).setText(categorias_string);
            ((RatingBar) rootView.findViewById(R.id.ratingBar)).setRating(mItem.getAverageRating());


            ImageView viewById = (ImageView) rootView.findViewById(R.id.imagen);

            Picasso.with(getContext()).load(mItem.getmUrlImage()).into(viewById);


            mAddToCartButton = (Button) rootView.findViewById(R.id.add_to_cart);
            mRemoveToCartButton = (Button) rootView.findViewById(R.id.remove_to_cart);


            Button mBuy_nowButton = (Button) rootView.findViewById(R.id.buy_now);

            CartDetails product = cartHelper.getProduct(mItem.getmId());

            if (product != null && product.getmQuantity() >= 1) {
                mAddToCartButton.setVisibility(View.GONE);
                mRemoveToCartButton.setVisibility(View.VISIBLE);
            }

            if (mItem.getmStock_quantity() < 1) {
            }


            mAddToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (mItem.getmStock_quantity() >= 1) {
                        showQuantityDialog(mItem, mItem.getmStock_quantity());
                    } else {
                        showMessage("Error", "No hay stock disponibe");

                    }


                }
            });

            mRemoveToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean b = cartHelper.deleteProduct(mItem.getmId());

                    if (b) {
                        Toast.makeText(getContext(), "El producto fue removido correctamente al carrito de compras", Toast.LENGTH_LONG).show();
                        mAddToCartButton.setVisibility(View.VISIBLE);
                        mRemoveToCartButton.setVisibility(View.GONE);
                    }


                }
            });


            mBuy_nowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), CarritoItemListActivity.class));
                }
            });


        }

        return rootView;
    }

    protected void showQuantityDialog(final Producto producto, final int max) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle((R.string.title_select_quantity));
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        //input.setText("1");
        input.setHint("Max. " + max);

        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                try{
                    int cantidad = Integer.parseInt(input.getText().toString());

                    if (cantidad > max) {
                        showMessage("Error", "No existe la cantidad de items solicitada");
                        return;
                    }


                    boolean inserted = cartHelper.insertProduct(producto, cantidad);

                    if (inserted) {

                        Toast.makeText(getContext(), "El producto fue agregado correctamente al carrito de compras", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getContext(), "El producto no pudo ser agregado, verifique si ya existe en el carrito", Toast.LENGTH_LONG).show();


                    }
                    mAddToCartButton.setVisibility(View.GONE);
                    mRemoveToCartButton.setVisibility(View.VISIBLE);


                }catch (Exception E){
                    Toast.makeText(getContext(), "Igrese una cantidad valida!", Toast.LENGTH_LONG).show();

                }


            }
        });
        alert.setNegativeButton((R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Put actions for CANCEL button here, or leave in blank
            }
        });
        alert.show();
    }

    private void showMessage(String titulo, String mensaje) {
        new AlertDialog.Builder(getActivity())
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }

                })


                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }



}
