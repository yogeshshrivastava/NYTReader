package com.codepath.nytreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.codepath.nytreader.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Shows the webview with the url passed and provides capability to share with other users.
 */
public class WebViewActivity extends AppCompatActivity {

    private static final String TAG = WebViewActivity.class.getSimpleName();

    public final static String EXTRA_WEB_URL = TAG + ".EXTRA_WEB_URL";
    public final static String EXTRA_TITLE = TAG + ".EXTRA_TITLE";


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.title)
    TextView mTitle;

    @BindView(R.id.webView)
    WebView mWebView;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;

    private String title;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        if(savedInstanceState != null) {
            title = savedInstanceState.getString(EXTRA_TITLE);
            url = savedInstanceState.getString(EXTRA_WEB_URL);
        } else {
            title = getIntent().getStringExtra(EXTRA_TITLE);
            url = getIntent().getStringExtra(EXTRA_WEB_URL);
        }

        swipeRefreshLayout.setEnabled(false);

        initToolbar();
        // Configure the Webview
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        // Enabling responsive layout:
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        // Set a webview client
        mWebView.setWebViewClient(new CustomClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress == 100) {
                    hideRefreshing();
                }
            }
        });
        // Load the actual url.
        mWebView.loadUrl(url);
        setRefreshing();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.arrow_left));
        mTitle.setText(title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_WEB_URL, url);
    }


    private static class CustomClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_web_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        } else if(item.getItemId() == R.id.action_share) {
            share();
        }
        return true;
    }

    private void share() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, title);
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_check_story) + url);
        startActivity(Intent.createChooser(share, getString(R.string.share_title)));
    }

    private void setRefreshing() {
        if(!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.removeCallbacks(null);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }
    }

    private void hideRefreshing() {
        if(swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.removeCallbacks(null);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }
}
