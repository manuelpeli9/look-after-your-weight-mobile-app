package com.example.manuel.layw_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class Chat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl("https://293330cf.ngrok.io/chat.html"); //indirizzo della homestation
    }
}
