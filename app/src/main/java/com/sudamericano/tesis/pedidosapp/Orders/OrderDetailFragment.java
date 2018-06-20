package com.sudamericano.tesis.pedidosapp.Orders;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.sudamericano.tesis.pedidosapp.Cart.Order;
import com.sudamericano.tesis.pedidosapp.Cart.OrderDetails;
import com.sudamericano.tesis.pedidosapp.Cart.OrderRefunds;
import com.sudamericano.tesis.pedidosapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class OrderDetailFragment extends Fragment {


    public static final String ARG_ITEM_ORDER = "order";


    private Order orderItem;
    TextView mDate;
    TextView mCustomerName;
    TextView mCustomerAddress;
    TextView mOrderNumber;
    TextView mTotaltaxes;
    TextView mTotal;
    TextView mTotalWithRefunds;
    TextView mStatus;
    TextView mDiscount;

    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private ProgressDialog pDialog;
    String DOMAIN;
    public static final String MyPREFERENCES = "account";
    SharedPreferences sharedpreferences;

    private Button mDeleteOrder;
    private Button mCompleteOrder;

    private Button mRefund;

    Context appContext;
    private Button mCancelOrder;
    private TextView mTotal_with_refunds_text;
    private TextView mTotalPayment;


    public OrderDetailFragment() {
    }

    Bundle savedInstanceState=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        this.savedInstanceState= savedInstanceState;

        DOMAIN = getResources().getString(R.string.DOMAIN);


        appContext = getActivity();

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Espere por favor...");
        pDialog.setCancelable(false);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        super.onCreate(savedInstanceState);

        FloatingActionButton buttonAddPayment = (FloatingActionButton) getActivity().findViewById(R.id.fab);


        if (getArguments().containsKey(ARG_ITEM_ORDER)) {

            orderItem = (Order) getArguments().getSerializable(
                    ARG_ITEM_ORDER);

            if (orderItem.getPaymentsTotal() >= orderItem.getTotalWithRefunds()) {
                buttonAddPayment.setVisibility(View.GONE);
            }



            buttonAddPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {


                    Context appContext = view.getContext();


                    AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                    alert.setTitle("Agregar Pago");
                    final EditText input = new EditText(appContext);

                    input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    input.setHint("0.00");
                    input.setText(orderItem.getValueToPay()+"");


                    ArrayList<String> spinnerArray = new ArrayList<String>();
                    spinnerArray.add("EFECTIVO");
                    spinnerArray.add("CHEQUE");

                    final Spinner spinner = new Spinner(appContext);
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(appContext, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                    spinner.setAdapter(spinnerArrayAdapter);


                    //input.setRawInputType(Configuration.KEYBOARD_12KEY);
                    LinearLayout layout = new LinearLayout(appContext);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    layout.addView(input);
                    layout.addView(spinner);


                    alert.setView(layout);


                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Put actions for OK button here


                            try {
                                double total = Double.valueOf(input.getText().toString());
                                String payment_type = spinner.getSelectedItem().toString();

                                if (total <= orderItem.getValueToPay() ) {

                                    addPayment((int) orderItem.getId(), total, payment_type);

                                } else {
                                    Snackbar.make(view, "Ingrese un valor no mayor a $" + orderItem.getValueToPay()+ " usted ingreso $"+ total, Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }


                            } catch (Exception ex) {
                                Snackbar.make(view, "Ingrese un valor correcto", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
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


            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(String.valueOf(orderItem.getId()));
            }
        }
    }

    private void addPayment(int id, double total, String payment_type) {
        showpDialog();
        RequestAddMetaPaymenOrders(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                hidepDialog();

                Log.e("payment result", result.toString());

                new AlertDialog.Builder(appContext)
                        .setTitle("Pago Agregado")
                        .setMessage("El pago ha sido registrado correctamente")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().finish();
                                startActivity(new Intent(getActivity(), OrderListActivity.class));


                            }
                        }).create().show();
            }


            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(appContext)
                        .setTitle("Error de autentificación ")
                        .setMessage("Ha ocurrido un error, usted no tiene permisos para agregar pagos!")
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
                    Log.e("taxes",error.toString());

                    VolleyError errores = new VolleyError(new String(error.networkResponse.data));
                    String message = errores.getMessage();
                    JSONObject j = new JSONObject(message);
                    new AlertDialog.Builder(appContext)
                            .setTitle("Error")
                            .setMessage(j.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();


                } catch (Exception e) {

                    new AlertDialog.Builder(appContext)
                            .setTitle("Error ")
                            .setMessage("Error al agregar el pago, error desconocido: !" + error.toString())
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
                new AlertDialog.Builder(appContext)
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
        }, id, total, payment_type);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.order_detail, container, false);

        if (orderItem != null) {

            TableLayout tlayout = (TableLayout) rootView.findViewById(R.id.table_line_items);
            TableLayout tlayout2 = (TableLayout) rootView.findViewById(R.id.table_refunds_total);


            double totalTaxes = 0;

            mDate = (TextView) rootView.findViewById(R.id.date);
            mCustomerName = (TextView) rootView.findViewById(R.id.customerName);
            mCustomerAddress = (TextView) rootView.findViewById(R.id.customer_address);
            mOrderNumber = (TextView) rootView.findViewById(R.id.order);
            mTotaltaxes = (TextView) rootView.findViewById(R.id.taxes);
            mTotal = (TextView) rootView.findViewById(R.id.total);
            mTotalWithRefunds = (TextView) rootView.findViewById(R.id.total_with_refunds);

            mTotalPayment = (TextView) rootView.findViewById(R.id.total_payment);


            mTotal_with_refunds_text = (TextView) rootView.findViewById(R.id.total_with_refunds_text);
            mStatus = (TextView) rootView.findViewById(R.id.status);
            mDiscount = (TextView) rootView.findViewById(R.id.discount);

            mDeleteOrder = (Button) rootView.findViewById(R.id.btnEliminar);
            mCompleteOrder = (Button) rootView.findViewById(R.id.btnCompletar);
            mRefund = (Button) rootView.findViewById(R.id.btnReembolso);

            mCancelOrder = (Button) rootView.findViewById(R.id.btnCancelOrder);

            mDate.setText(String.valueOf(orderItem.getDate()));
            mCustomerName.setText(String.valueOf(orderItem.getCustomerName()));
            mCustomerAddress.setText(String.valueOf(orderItem.getAddressComplete()));
            mOrderNumber.setText(String.valueOf(orderItem.getId()));
            mTotal.setText("$" + String.valueOf(orderItem.getTotal()));
            mStatus.setText(String.valueOf(orderItem.getTranslationStatusOrder(orderItem.getStatus())));
            mDiscount.setText("$" + String.valueOf(orderItem.getDiscountTotal()));

            mTotaltaxes.setText("$" + String.valueOf(orderItem.getTotalTax()));


            mTotalWithRefunds.setText("$" + String.valueOf(orderItem.getTotalWithRefunds()));

            mTotalPayment.setText("Valor Pendiente: $" + String.valueOf(orderItem.getValueToPay()));


            if (orderItem.getTotalRefunds() < 0) {
                mTotalWithRefunds.setVisibility(View.VISIBLE);
                mTotal_with_refunds_text.setVisibility(View.VISIBLE);
                mTotal.setPaintFlags(mTotal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                mTotalWithRefunds.setVisibility(View.GONE);
                mTotal_with_refunds_text.setVisibility(View.GONE);
            }


            int color_text = Color.parseColor("#000000");


            tlayout.removeAllViews();

            Context applicationContext = getActivity().getApplicationContext();

            TableRow separator = new TableRow(applicationContext);
            View line = new View(applicationContext);
            TableRow.LayoutParams separatorLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1);
            separatorLayoutParams.setMargins(2, 0, 2, 0);
            separatorLayoutParams.span = 5;
            line.setBackgroundColor(Color.rgb(144, 144, 144));

            separator.addView(line, separatorLayoutParams);


            tlayout.addView(separator);


            TableRow row = new TableRow(applicationContext);

            TextView text1 = new TextView(applicationContext);
            TextView text2 = new TextView(applicationContext);
            TextView text3 = new TextView(applicationContext);
            TextView text4 = new TextView(applicationContext);

            TableRow.LayoutParams layoutParamsHeader = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);

            text1.setText("Descripción");
            text1.setLayoutParams(layoutParamsHeader);
            text1.setTypeface(null, Typeface.BOLD);
            text1.setTextColor(color_text);

            row.addView(text1);


            text2.setText("Precio");
            text2.setLayoutParams(layoutParamsHeader);

            text2.setTypeface(null, Typeface.BOLD);
            text2.setTextColor(color_text);

            row.addView(text2);

            text3.setText("Cantidad");
            text3.setLayoutParams(layoutParamsHeader);

            text3.setTypeface(null, Typeface.BOLD);
            text3.setTextColor(color_text);

            row.addView(text3);


            text4.setText("Subtotal");
            text4.setLayoutParams(layoutParamsHeader);

            text4.setTypeface(null, Typeface.BOLD);
            text4.setTextColor(color_text);
            row.addView(text4);

            tlayout.addView(row);


            separator = new TableRow(applicationContext);
            line = new View(applicationContext);
            separatorLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1);
            separatorLayoutParams.setMargins(2, 0, 5, 0);
            separatorLayoutParams.span = 5;
            line.setBackgroundColor(Color.rgb(144, 144, 144));

            separator.addView(line, separatorLayoutParams);


            tlayout.addView(separator);


            List<OrderDetails> lineItems = orderItem.getLineItems();
            List<OrderRefunds> orderRefundItems = orderItem.getOrderRefunds();

            TableRow.LayoutParams layoutParamsDetails = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);


            layoutParamsDetails.weight = 1;


            for (OrderDetails orderdetail : lineItems
                    ) {

                row = new TableRow(applicationContext);
                text1 = new TextView(applicationContext);
                text2 = new TextView(applicationContext);
                text3 = new TextView(applicationContext);
                text4 = new TextView(applicationContext);


                text1.setText(orderdetail.getmShortName());
                text1.setLayoutParams(layoutParamsDetails);

                text1.setTextColor(color_text);
                row.addView(text1);


                text2.setText("$" + orderdetail.getPrice());
                text2.setGravity(1);
                text2.setLayoutParams(layoutParamsDetails);
                text2.setTextColor(color_text);
                row.addView(text2);


                text3.setText("" + orderdetail.getQuantity());
                text3.setGravity(1);
                text3.setLayoutParams(layoutParamsDetails);
                text3.setTextColor(color_text);
                row.addView(text3);

                text4.setText("$" + orderdetail.getTotal());
                text4.setGravity(1);
                text4.setLayoutParams(layoutParamsDetails);
                text4.setTextColor(color_text);
                row.addView(text4);
                tlayout.addView(row);


                separator = new TableRow(applicationContext);
                line = new View(applicationContext);
                separatorLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1);
                separatorLayoutParams.setMargins(2, 0, 2, 0);
                separatorLayoutParams.span = 5;
                line.setBackgroundColor(Color.rgb(144, 144, 144));

                separator.addView(line, separatorLayoutParams);


                tlayout.addView(separator);


            }


            for (OrderRefunds orderRefund : orderRefundItems
                    ) {

                row = new TableRow(applicationContext);
                text1 = new TextView(applicationContext);
                text2 = new TextView(applicationContext);

                text1.setText(orderRefund.getRefund());
                text1.setLayoutParams(layoutParamsDetails);

                text1.setTextColor(color_text);
                row.addView(text1);


                text2.setText("$" + orderRefund.getTotal());
                text2.setGravity(1);
                text2.setLayoutParams(layoutParamsDetails);
                text2.setTextColor(color_text);
                row.addView(text2);

                tlayout2.addView(row);

                separator = new TableRow(applicationContext);
                line = new View(applicationContext);
                separatorLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1);
                separatorLayoutParams.setMargins(2, 0, 2, 0);
                separatorLayoutParams.span = 5;
                line.setBackgroundColor(Color.rgb(144, 144, 144));

                separator.addView(line, separatorLayoutParams);


                tlayout2.addView(separator);


            }

            if (orderItem.getValueToPay() == 0) {
                mCompleteOrder.setVisibility(View.VISIBLE);
                mDeleteOrder.setVisibility(View.GONE);
                mCancelOrder.setVisibility(View.GONE);
            } else {
                mCompleteOrder.setVisibility(View.GONE);
            }

            if(orderItem.getValueToPay()< orderItem.getTotalWithRefunds()){
              //  mDeleteOrder.setVisibility(View.GONE);
            }

            if (orderItem.getStatus().equals("completed") || orderItem.getStatus().equals("failed") || orderItem.getStatus().equals("cancelled")) {
                mDeleteOrder.setVisibility(View.GONE);
                mCompleteOrder.setVisibility(View.GONE);
                mRefund.setVisibility(View.GONE);
                mCancelOrder.setVisibility(View.GONE);
            }



            mDeleteOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Eliminacion Permanente")
                            .setMessage("Esta seguro de eliminar esta orden?")
                            .setIcon(android.R.drawable.ic_delete)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //deleteOrder((int) orderItem.getId());
                                    UpdateStatusOrder((int) orderItem.getId(), "failed");

                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();

                }
            });

            mCompleteOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Completar Pedido")
                            .setMessage("Esta seguro que desea completar este pedido?")
                            .setIcon(android.R.drawable.ic_lock_lock)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    UpdateStatusOrder((int) orderItem.getId(), "completed");
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();

                }
            });


            mCancelOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Cancelar  Pedido")
                            .setMessage("Esta seguro que desea cancelar este pedido?")
                            .setIcon(android.R.drawable.ic_lock_lock)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    UpdateStatusOrder((int) orderItem.getId(), "cancelled");
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();

                }
            });
            mRefund.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {


                    AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                    alert.setTitle("Ingrese el valor a reembolsar");
                    final EditText input = new EditText(getActivity());

                    input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    input.setHint("0.00");

                    final TextInputEditText inputRedund = new TextInputEditText(getActivity());
                    inputRedund.setHint("Mótivo del Reembolso");

                    //input.setRawInputType(Configuration.KEYBOARD_12KEY);
                    LinearLayout layout = new LinearLayout(getActivity());
                    layout.setOrientation(LinearLayout.VERTICAL);

                    layout.addView(input);
                    layout.addView(inputRedund);

                    alert.setView(layout);


                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Put actions for OK button here

                            try {
                                float total = Float.valueOf(input.getText().toString());
                                String refund = inputRedund.getText().toString();
                                if (!refund.isEmpty() || total > 0) {
                                    if (total > orderItem.getTotalWithRefunds()) {
                                        Toast.makeText(appContext, "Ingrese el monto que no supere el valor de la orden", Toast.LENGTH_LONG);
                                        return;
                                    }
                                    RefundOrder((int) orderItem.getId(), total, refund);
                                } else {
                                    Toast.makeText(appContext, "Ingrese el monto y motivo e intente nuevamente.", Toast.LENGTH_LONG);
                                }


                            } catch (Exception ex) {

                                Toast.makeText(appContext, "Ingrese el monto y motivo e intente nuevamente.", Toast.LENGTH_LONG);

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

        return rootView;
    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public interface VolleyCallback {
        void onSuccess(JSONObject result);

        void onAuthFailureError();

        void onServerError(VolleyError Error);

        void onFailureConnection();
    }


    private void deleteOrder(int id) {
        showpDialog();

        RequestdeleteOrders(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                hidepDialog();

                new AlertDialog.Builder(appContext)
                        .setTitle("Eliminacion Correcta")
                        .setMessage("La orden ha sido eliminada correctamente")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().finish();
                                startActivity(new Intent(getActivity(), OrderListActivity.class));
                            }
                        }).create().show();
            }

            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(appContext)
                        .setTitle("Error de autentificación ")
                        .setMessage("Ha ocurrido un error, usted no tiene permisos para eliminar ordenes!")
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
                    new AlertDialog.Builder(appContext)
                            .setTitle("Error")
                            .setMessage(j.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();


                } catch (Exception e) {

                    new AlertDialog.Builder(appContext)
                            .setTitle("Error ")
                            .setMessage("Error al eliminar la orden, error desconocido!" + error.toString())
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
                new AlertDialog.Builder(appContext)
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

    private void UpdateStatusOrder(int id, String status) {
        showpDialog();
        RequestChangeStatusOrder(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                hidepDialog();

                new AlertDialog.Builder(appContext)
                        .setTitle("Actualizacion de Orden Correcta")
                        .setMessage("La orden ha sido cambiada correctamente")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                getActivity().finish();
                                startActivity(new Intent(getActivity(), OrderListActivity.class));


                            }
                        }).create().show();
            }


            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(appContext)
                        .setTitle("Error de autentificación ")
                        .setMessage("Ha ocurrido un error, usted no tiene permisos para modificar una orden!")
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


                    new AlertDialog.Builder(appContext)
                            .setTitle("Actualizacion de Orden Correcta")
                            .setMessage("La orden ha sido cambiada correctamente")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    getActivity().finish();
                                    startActivity(new Intent(getActivity(), OrderListActivity.class));


                                }
                            }).create().show();


                    VolleyError errores = new VolleyError(new String(error.networkResponse.data));
                    String message = errores.getMessage();
                    JSONObject j = new JSONObject(message);
                    new AlertDialog.Builder(appContext)
                            .setTitle("Error")
                            .setMessage(j.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();


                } catch (Exception e) {

                    /*new AlertDialog.Builder(appContext)
                            .setTitle("Error ")
                            .setMessage("Error al obtener al actualizar el estado, error !"+ error.toString())
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show(); */
                }
                hidepDialog();
            }

            @Override
            public void onFailureConnection() {
                new AlertDialog.Builder(appContext)
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

    private void RefundOrder(final int id, float valor, final String refund) {
        showpDialog();
        RequestCreateRefundOrders(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                Log.e("refund", result.toString());

                hidepDialog();

                // hidepDialog();

              //  UpdateStatusOrder(id, Order.REFUNDED);

               // startActivity(new Intent(getActivity(), OrderListActivity.class));

                new AlertDialog.Builder(appContext)
                        .setTitle("Reembolso!")
                        .setMessage("La orden ha sido cambiada correctamente")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().finish();
                                startActivity(new Intent(getActivity(), OrderListActivity.class));
                            }
                        }).create().show();
            }


            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Error de autentificación ")
                        .setMessage("Ha ocurrido un error, usted no tiene permisos para realizar  reembolsos!")
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
                    new AlertDialog.Builder(appContext)
                            .setTitle("Error")
                            .setMessage(j.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();


                } catch (Exception e) {

                    new AlertDialog.Builder(appContext)
                            .setTitle("Error ")
                            .setMessage("Error en el reembolso, error desconocido!" + error.toString() )
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
                new AlertDialog.Builder(appContext)
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
        }, id, valor, refund);

    }


    public void RequestdeleteOrders(final VolleyCallback callback, int id) {


        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/wc/v1/orders/" + id + "?force=true&access_token=" + token;


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
        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        mRequestQueue.add(requestCategorias);


    }

    public void RequestChangeStatusOrder(final VolleyCallback callback, int id, String status) {
        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/wc/v1/orders/" + id + "?&access_token=" + token;
        Log.d("UPDATE ORDERS", url);
        JSONObject order = new JSONObject();
        try {
            order.put("id", id);
            order.put("status", status);

            System.out.println(order.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("error formando json", e.getMessage());
        }



      final JsonObjectRequest requestCategorias = new JsonObjectRequest(Request.Method.POST, url, order,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("estado",response.toString());
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("estado",error.toString());
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
        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        mRequestQueue.add(requestCategorias);


    }

    public void RequestCreateRefundOrders(final VolleyCallback callback, int id, float total, String refund) {
        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/wc/v1/orders/" + id + "/refunds?force=true&access_token=" + token;
        Log.d("refund ORDERS", url);
        JSONObject order_refunds = new JSONObject();

        try {
            order_refunds.put("amount", String.valueOf(total));
            order_refunds.put("reason", refund);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, order_refunds,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("refund error", error.toString());
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
        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        mRequestQueue.add(req);


    }


    public void RequestAddMetaPaymenOrders(final VolleyCallback callback, int id, double total, String payment_type) {
        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/orders/v1/add/payment/" + id + "?access_token=" + token;

        Log.d("payment", url);
        JSONObject order_meta = new JSONObject();

        try {
            order_meta.put("note", "Ha realizado un pago tipo " + payment_type + " de " + String.valueOf(total));
            order_meta.put("value", String.valueOf(total));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        /*StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("refund error", response);

                        // Display the first 500 characters of the response string.

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("refund error", error.toString());


            }
        });*/

       JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, order_meta,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("refund error", error.toString());
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
        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        mRequestQueue.add(req);


    }


    public void RequestNoteFromPaymenOrder(final VolleyCallback callback, int id, float total, String payment_type) {
        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/wc/v1/orders/" + id + "/notes?access_token=" + token;
        Log.d("refund ORDERS", url);
        JSONObject order_meta = new JSONObject();

        try {
            // order_meta.put("key", "payment");
            order_meta.put("note", "Ha realizado un pago tipo " + payment_type + " de " + String.valueOf(total));
            order_meta.put("customer_note", true);
            //  order_meta.put("value", String.valueOf(total));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, order_meta,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("refund error", error.toString());
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
        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        mRequestQueue.add(req);


    }


}
