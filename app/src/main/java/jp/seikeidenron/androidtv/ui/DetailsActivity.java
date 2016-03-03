package jp.seikeidenron.androidtv.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import jp.seikeidenron.androidtv.R;

import jp.seikeidenron.androidtv.common.MLog;
import jp.seikeidenron.androidtv.common.Utils;
import jp.seikeidenron.androidtv.model.YTInfo;


public class DetailsActivity extends Activity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    /**
     * Defines type of item which is obtained from {@link #KEY_ITEM}.
     * 2. YouTubeInfo -> {@link YTInfo} class
     */
    public static final String KEY_TYPE = "MovieType";
    public static final int TYPE_INVALID = -1;
    public static final int TYPE_YOUTUBE = 2;

    public static final String KEY_ITEM = "Item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
