package com.sudamericano.tesis.pedidosapp;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d("serachebalve","holaa");


        handleIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        Log.e("intent","is a new intent");
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
//        productoList = new ArrayList<>();
//        simpleItemRecyclerViewAdapter = new OrdersRecyclerViewAdapter(productoList);

        Log.d("search","serach handel");

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        } else {

            // cargarProductos("", 0);

        }
        // simpleItemRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void doMySearch(String query) {
     //   cargarProductos(query, 0);
        //simpleItemRecyclerViewAdapter.fil
        Log.d("search","serach handel");


    }

    @Override
    public boolean onSearchRequested() {

        return super.onSearchRequested();
    }


    @Override
    public void onBackPressed() {

       // finish();
    }

}
