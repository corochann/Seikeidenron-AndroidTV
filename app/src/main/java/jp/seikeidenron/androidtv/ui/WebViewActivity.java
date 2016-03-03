package jp.seikeidenron.androidtv.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import jp.seikeidenron.androidtv.R;

import jp.seikeidenron.androidtv.common.MLog;

/**
 * Showing any Web page on WebView.
 * Currently it is used to show http://seikeidenron.jp/about/
 */
public class WebViewActivity extends Activity {


    /* Constant */
    private static final String TAG = ZasshiActivity.class.getSimpleName();
    public static final String KEY_URL = "url";                                    /* URL to access */
    public static final String DEFAULT_URL = "http://seikeidenron.jp";             /* URL to access */
    public static final String SEIKEIDENRON_ABOUT_URL = "http://seikeidenron.jp/about/";  /* URL to access */
    public static final String KEY_IS_JAVASCRIPT_ENABLED = "isjavascriptenabled";  /* whether javascript enabled or not */

    /* true: URL in the first page of webview will be shown in webview. false: browser app will be used to show URL link */
    public static final boolean SHOW_LINK_URL_IN_WEBVIEW = false;

    /* Attribute */
    private int mVolNum = 1; // This Activity shows Denshizasshi of this volume.

    private WebView mWebView;
    private ProgressDialog mProgressBar;

    private String mUrl;
    private boolean mIsJavascriptEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        mWebView = (WebView) findViewById(R.id.webView);

        mUrl     = getIntent().getStringExtra(KEY_URL);
        if(mUrl == null) {
            MLog.e(TAG, "URL is not specified in Intent! set default URL.");
            mUrl = DEFAULT_URL;
        }
        mIsJavascriptEnabled = getIntent().getBooleanExtra(KEY_IS_JAVASCRIPT_ENABLED, false);

        /* These codes are for the width adjustment, see http://stackoverflow.com/questions/3808532/how-to-set-the-initial-zoom-width-for-a-webview */
        mWebView.setInitialScale(30);
        WebSettings settings = mWebView.getSettings();
        // To fit width with display. http://stren-blog.seesaa.net/article/412992283.html
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        settings.setJavaScriptEnabled(mIsJavascriptEnabled);
        //webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);



        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        mProgressBar = ProgressDialog.show(this, "", "Loading...");


            mWebView.setWebViewClient(new WebViewClient() {
                // This is to load URL in webview, otherwise browser app will be used to load URL.
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    MLog.i(TAG, "Processing webview url click, url = " + url);
                    if(SHOW_LINK_URL_IN_WEBVIEW) { // http://www.techdoctranslator.com/android/webapps/webview
                        // load URL in this webview
                        view.loadUrl(url);
                    } else {
                        // load URL in browser app
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                    MLog.i(TAG, "Finished loading URL: " + url);
                    if (mProgressBar.isShowing()) {
                        mProgressBar.dismiss();
                    }
                }

                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    MLog.e(TAG, "Error: " + description);
                    Toast.makeText(WebViewActivity.this, "error: " + description, Toast.LENGTH_SHORT).show();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage(description);
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                    alertDialog.show();
                }
            });

        mWebView.loadUrl(mUrl);
    }

}
