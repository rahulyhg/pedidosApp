package com.sudamericano.tesis.pedidosapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ManualActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manual);

        WebView webView = (WebView) findViewById(R.id.webview_manual);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        String pdf="http://arboreng.com/infog/pedidosapp/download/manual/?wpdmdl=7839";
        webView.loadUrl("https://docs.google.com/viewer?url="+pdf);




    }


    @Override
    public void onBackPressed() {

        finish();
    }

}
