package com.sudamericano.tesis.pedidosapp.Orders;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.sudamericano.tesis.pedidosapp.Cart.Order;
import com.sudamericano.tesis.pedidosapp.Cart.OrderDetails;
import com.sudamericano.tesis.pedidosapp.Cart.OrderRefunds;
import com.sudamericano.tesis.pedidosapp.MenuActivity;
import com.sudamericano.tesis.pedidosapp.R;
import com.sudamericano.tesis.pedidosapp.Utils.EndlessRecyclerViewScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class OrderListActivity extends AppCompatActivity {

    private boolean mTwoPane;
    private List<Order> orderList;

    String DOMAIN;
    public static final String MyPREFERENCES = "account";
    SharedPreferences sharedpreferences;
    private ProgressDialog pDialog;
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private OrdersRecyclerViewAdapter ordersRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Espere por favor...");
        pDialog.setCancelable(false);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        DOMAIN = getResources().getString(R.string.DOMAIN);

        View recyclerView = findViewById(R.id.order_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.order_detail_container) != null) {
            mTwoPane = true;
        }


        orderList = new ArrayList<>();

        ordersRecyclerViewAdapter = new OrdersRecyclerViewAdapter(orderList);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getBaseContext(), 2);
        ((RecyclerView) recyclerView).setAdapter(ordersRecyclerViewAdapter);
        ((RecyclerView) recyclerView).setLayoutManager(gridLayoutManager);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        ((RecyclerView) recyclerView).setLayoutManager(linearLayoutManager);

        ((RecyclerView) recyclerView).addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                LoadMoreDataFromApi(page);
            }
        });
        loadOrders("", 0);
    }


    private void LoadMoreDataFromApi(int page) {
        loadOrders("", page);
    }

    private void loadOrders(String query, final int page) {

        getOrders(new VolleyCallbackArray() {
            @Override
            public void onSuccess(JSONArray JsonArrayOrders) {
                Order order = null;

                try {
                    for (int i = 0; i < JsonArrayOrders.length(); i++) {
                        JSONObject json_order = JsonArrayOrders.getJSONObject(i);

                        int id = (Integer) json_order.get("id");
                        String created_at = (String) json_order.get("date_created");
                        String status = (String) json_order.get("status");
                        double total = Double.valueOf(json_order.get("total").toString());
                        double totalTaxes = Double.valueOf(json_order.get("total_tax").toString());
                        double totalDiscount = Double.valueOf(json_order.get("discount_total").toString());

                        String customer = "Invitado";
                        try {
                            JSONObject json_customer = json_order.getJSONObject("billing");
                            customer = (String) json_customer.get("first_name");
                            customer = customer + " " + (String) json_customer.get("last_name");
                        } catch (Exception ex) {
                            Log.d("error", ex.toString());

                        }

                        try {

                            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = fmt.parse(created_at);

                            SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
                            created_at = fmtOut.format(date);

                        } catch (Exception ex) {

                            Log.e("error date", ex.getMessage());

                        }

                        JSONObject billing = json_order.getJSONObject("billing");


                        String address_1 = (String) billing.get("address_1");
                        String address_2 = (String) billing.get("address_2");
                        String city = (String) billing.get("city");
                        String state = (String) billing.get("state");


                        order = new Order(id, customer, created_at, status, total, totalDiscount, totalTaxes);

                        order.setCustomerAddress1(address_1);
                        order.setCustomerAddress2(address_2);
                        order.setCustomerCity(city);
                        order.setCustomerState(state);


                        JSONArray line_items = json_order.getJSONArray("line_items");


                        List<OrderDetails> lineItemsList = new ArrayList<>();
                        OrderDetails orderDetails;

                        for (int a = 0; a < line_items.length(); a++) {
                            JSONObject line_item = line_items.getJSONObject(a);
                            int id_item = (Integer) line_item.get("id");
                            String name = (String) line_item.get("name");
                            String sku = (String) line_item.get("sku");
                            int product_id = (Integer) line_item.get("product_id");
                            int quantity = (Integer) line_item.get("quantity");
                            double price = Double.valueOf((String) line_item.get("price"));
                            double subtotal = Double.valueOf((String) line_item.get("subtotal"));
                            double subtotal_tax = Double.valueOf((String) line_item.get("subtotal_tax"));
                            double total_ = Double.valueOf((String) line_item.get("total"));
                            double total_tax = Double.valueOf((String) line_item.get("total_tax"));
                            orderDetails = new OrderDetails(id_item, name, sku, product_id, quantity, price, subtotal, subtotal_tax, total_, total_tax);
                            lineItemsList.add(orderDetails);
                        }


                        order.setLineItems(lineItemsList);


                        JSONArray jsonOrderRefunds = json_order.getJSONArray("refunds");

                        List<OrderRefunds> orderRefundsList = new ArrayList<>();
                        OrderRefunds orderRefund;

                        for (int b = 0; b < jsonOrderRefunds.length(); b++) {
                            JSONObject json_order_refund = jsonOrderRefunds.getJSONObject(b);
                            int id_refund = (Integer) json_order_refund.get("id");
                            String refund = (String) json_order_refund.get("refund");
                            double total_refund = Double.valueOf(json_order_refund.get("total").toString());
                            orderRefund = new OrderRefunds(id_refund, refund, total_refund);
                            orderRefundsList.add(orderRefund);
                        }

                        order.setOrderRefunds(orderRefundsList);


                        JSONArray payments = json_order.getJSONArray("payments");

                        Log.e("payments",payments.toString());

                        List<Double> paymentsList = new ArrayList<>();


                        for (int c = 0; c < payments.length(); c++) {

                            Double payment = payments.getDouble(c);
                            paymentsList.add(payment);
                        }

                        order.setPayments(paymentsList);

                        orderList.add(order);
                    }


                } catch (JSONException e) {
                    Log.e("exception", e.getMessage());
                }

                ordersRecyclerViewAdapter.notifyDataSetChanged();


            }

            @Override
            public void onAuthFailureError() {
                new AlertDialog.Builder(OrderListActivity.this)
                        .setTitle("Error de autentificación ")
                        .setMessage("Ha ocurrido un error, usted no tiene permisos para ver esta información!")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create().show();
            }

            @Override
            public void onServerError(VolleyError error) {
                try {
                    VolleyError errores = new VolleyError(new String(error.networkResponse.data));
                    String message = errores.getMessage();
                    JSONObject j = new JSONObject(message);
                    new AlertDialog.Builder(OrderListActivity.this)
                            .setTitle("Error")
                            .setMessage(j.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();


                } catch (Exception e) {

                    new AlertDialog.Builder(OrderListActivity.this)
                            .setTitle("Error ")
                            .setMessage("Error al crear al cliente, error desconocido!")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }

            }

            @Override
            public void onFailureConnection() {
                new AlertDialog.Builder(OrderListActivity.this)
                        .setTitle("Error en la conexión ")
                        .setMessage("Verifique si esta conectado a internet e intente nuevamente!")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).create().show();

            }
        }, query, page);
    }

    public void getOrders(final VolleyCallbackArray callback, String query, int page) {
        String token = (sharedpreferences.getString("access_token", ""));
        String url = DOMAIN + "/wp-json/wc/v1/orders?search=" + query + "&page=" + (page + 1) + "&access_token=" + token;
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
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        mRequestQueue.add(req);
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new OrdersRecyclerViewAdapter(orderList));
    }

    public class OrdersRecyclerViewAdapter
            extends RecyclerView.Adapter<OrdersRecyclerViewAdapter.ViewHolder> {

        private final List<Order> orderList_;

        public OrdersRecyclerViewAdapter(List<Order> orders) {
            this.orderList_ = orders;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.order_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            Order order = orderList_.get(position);

            holder.orderItem = order;

            holder.mDate.setText("Fecha: " + String.valueOf(order.getDate()));
            holder.mCustomerName.setText(String.valueOf(order.getCustomerName()));
            holder.mCustomerAddress.setText(String.valueOf(order.getAddressComplete()));
            holder.mOrderNumber.setText("Orden Nro: " + String.valueOf(order.getId()));
            holder.mItems.setText("Cantidad: #" + String.valueOf(order.getLineItems().size()) + " items");
            holder.mTotal.setText("Total: $" + String.valueOf(order.getTotal()));
            holder.mStatus.setText("Estado: " + order.getTranslationStatusOrder(order.getStatus()));


            if (order.getStatus().equals(Order.PROCCESSING) || order.getStatus().equals(Order.ONHOLD) || order.getStatus().equals(Order.PROCCESSING) || order.getStatus().equals(Order.PENDING)) {
                holder.mStatus.setTextColor(Color.RED);
            } else {
                holder.mStatus.setTextColor(Color.BLACK);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putSerializable(OrderDetailFragment.ARG_ITEM_ORDER, holder.orderItem);
                        OrderDetailFragment fragment = new OrderDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.order_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, OrderDetailActivity.class);

                        intent.putExtra(OrderDetailFragment.ARG_ITEM_ORDER, holder.orderItem);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderList_.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mDate;
            public final TextView mCustomerName;
            public final TextView mCustomerAddress;
            public final TextView mOrderNumber;
            public final TextView mItems;
            public final TextView mTotal;
            public final TextView mStatus;


            public Order orderItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mDate = (TextView) view.findViewById(R.id.date);
                mCustomerName = (TextView) view.findViewById(R.id.customerName);
                mCustomerAddress = (TextView) view.findViewById(R.id.customer_address);
                mOrderNumber = (TextView) view.findViewById(R.id.order);
                mItems = (TextView) view.findViewById(R.id.nro_items);
                mTotal = (TextView) view.findViewById(R.id.total);
                mStatus = (TextView) view.findViewById(R.id.status);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mOrderNumber.getText() + "'";
            }
        }
    }


    public interface VolleyCallbackArray {
        void onSuccess(JSONArray result);

        void onAuthFailureError();

        void onServerError(VolleyError Error);

        void onFailureConnection();
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


        Log.e("menu",id+"="+ R.id.show_menu);
        if (id == R.id.show_menu) {
             this.finish();
            startActivity(new Intent(this, MenuActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {

       // finish();
    }
}
