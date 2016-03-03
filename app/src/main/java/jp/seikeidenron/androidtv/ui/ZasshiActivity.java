package jp.seikeidenron.androidtv.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import jp.seikeidenron.androidtv.R;

import jp.seikeidenron.androidtv.common.MLog;
import jp.seikeidenron.androidtv.model.Zasshi;

/**
 * Show Denshizasshi using webview.
 * It will show zasshi with {@link #mSelectedZasshi}.
 */
public class ZasshiActivity extends Activity {

    /* Constant */
    private static final String TAG = ZasshiActivity.class.getSimpleName();

    //public static final String KEY_VOLUME_NUMBER = "zasshivolumenumber"; // not used anymore
    public static final String KEY_ZASSHI = "zasshi";

    /* Attribute */
    //private int mVolNum = -1; // This Activity shows Denshizasshi of this volume.
    private Zasshi mSelectedZasshi = null;

    private WebView mZasshiWebView;
    private ProgressDialog mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zasshi);

        //mVolNum = getIntent().getIntExtra(KEY_VOLUME_NUMBER, 1);
        mSelectedZasshi = getIntent().getParcelableExtra(KEY_ZASSHI);
        mZasshiWebView  = (WebView) findViewById(R.id.zasshiWebView);

        /* These codes are for the width adjustment, see http://stackoverflow.com/questions/3808532/how-to-set-the-initial-zoom-width-for-a-webview */
        mZasshiWebView.setInitialScale(30);
        WebSettings settings = mZasshiWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        //webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        // To fit width with display. http://stren-blog.seesaa.net/article/412992283.html
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        mProgressBar = ProgressDialog.show(this, mSelectedZasshi.getTitle(), "Loading...");

        mZasshiWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                MLog.i(TAG, "Processing webview url click...");
                view.loadUrl(url);
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
                Toast.makeText(ZasshiActivity.this, "error: " + description, Toast.LENGTH_SHORT).show();
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

        //webview.loadUrl("http://seikeidenron.jp");
        //mZasshiWebView.loadUrl("http://seikeidenron.jp/book/vol" + mVolNum + "/");
        mZasshiWebView.loadUrl(mSelectedZasshi.getZasshiUrl());
    }

}
