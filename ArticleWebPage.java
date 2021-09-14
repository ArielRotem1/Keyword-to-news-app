package com.example.linkstonewsbyword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ArticleWebPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_web_page);

        Intent intent = getIntent();
        String Url = intent.getStringExtra("URL");

        WebView articleWebView = findViewById(R.id.articleWebView);
        articleWebView.setWebViewClient(new MyBrowser());
        articleWebView.loadUrl(Url);
    }

    private class MyBrowser extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}