package com.daplace;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import static com.daplace.utils.LoadingUtil.enableDisableView;


public class MainActivity extends AppCompatActivity {
    private MyWebChromeClient mWebChromeClient = null;
    private View mCustomView;
    FrameLayout fader;
    FrameLayout mainFrame;
    boolean isPageError = false;
    LinearLayout linear;
    private ProgressBar progressbar;
    Button btn;
    TextView txt;
    AVLoadingIndicatorView avi;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    private WebView myWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        linear = findViewById(R.id.linear);
        btn =  findViewById(R.id.reload);
        txt =  findViewById(R.id.errText);

        progressbar = findViewById(R.id.progress_bar);

        fader = (FrameLayout) findViewById(R.id.fader);
        mainFrame = (FrameLayout) findViewById(R.id.mainFrame);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        setLoadingAnimation();
        isPageError = false;
        progressbar.setVisibility( View.GONE);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 recreate();
            }
        });
        if (!isNetworkAvailable()) {
            stopLoadingAnimation();
            // Create an Alert Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Set the Alert Dialog Message
            builder.setMessage("Internet Connection Required");
            builder.setCancelable(false);

            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            builder.setPositiveButton("Retry",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int id) {
                            // Restart the Activity
                            recreate();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        myWebView = findViewById(R.id.webview);
        mWebChromeClient = new MyWebChromeClient();
        myWebView.setWebChromeClient(mWebChromeClient);
        myWebView.setWebViewClient(new WebViewClient()
        {
            public void onPageFinished( WebView view, String url){

                if (isPageError){
                    myWebView.setVisibility(View.GONE);
                    linear.setVisibility(View.VISIBLE);

                }
                stopLoadingAnimation();
                progressbar.setVisibility( View.GONE);

            }


            public void onReceivedError( WebView view, int errorCode, String description, String failingUrl){

                isPageError = true;

            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                   view.loadUrl(url);
                   setLoadingAnimation();
                   progressbar.setVisibility( View.VISIBLE);
                   return true;

            }

            });


        if (isNetworkAvailable()) {
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setSupportMultipleWindows(true);
            webSettings.setGeolocationEnabled(true);
            myWebView.loadUrl("https://cancerchronic.org/daplace"); //URL input
        }
        else{
            Toast.makeText(this, "No internet connection" ,Toast .LENGTH_SHORT).show();
        }
    }

    // Private class isNetworkAvailable
    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public class MyWebChromeClient extends WebChromeClient {

        FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mContentView = findViewById(R.id.activity_main21);
            mContentView.setVisibility(View.GONE);
            mCustomViewContainer = new FrameLayout(MainActivity.this);
            mCustomViewContainer.setLayoutParams(LayoutParameters);
            mCustomViewContainer.setBackgroundResource(android.R.color.black);
            view.setLayoutParams(LayoutParameters);
            mCustomViewContainer.addView(view);
            mCustomView = view;
            mCustomViewCallback = callback;
            mCustomViewContainer.setVisibility(View.VISIBLE);
            setContentView(mCustomViewContainer);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            } else {
                // Hide the custom view.
                mCustomView.setVisibility(View.GONE);
                // Remove the custom view from its container.
                mCustomViewContainer.removeView(mCustomView);
                mCustomView = null;
                mCustomViewContainer.setVisibility(View.GONE);
                mCustomViewCallback.onCustomViewHidden();
                // Show the content view.
                mContentView.setVisibility(View.VISIBLE);
                setContentView(mContentView);
            }
        }
    }

    public void setLoadingAnimation(){
        enableDisableView(mainFrame, false);
        fader.setVisibility(View.VISIBLE);
        avi.show();
    }

   public void stopLoadingAnimation(){
        enableDisableView(mainFrame, true);
        fader.setVisibility(View.GONE);
        avi.hide();
    }



    @Override
    public void onBackPressed() {
        if (mCustomViewContainer != null)
            mWebChromeClient.onHideCustomView();
        else if (myWebView.canGoBack())
            myWebView.goBack();
    }
}


