package jp.seikeidenron.androidtv.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import jp.seikeidenron.androidtv.data.youtube.YTLoader;
import jp.seikeidenron.androidtv.model.Zasshi;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Loader class which prepares Seikeidenron item data
 * A. Denshi Zasshi list: {@link ZasshiProvider#createZasshiList(Context)}
 *
 * *** YouTube video list loading is done by {@link YTLoader} ***
 */
public class SeikeidenronZasshiLoader extends AsyncTaskLoader<ArrayList<Zasshi>> {

    private static final String TAG = SeikeidenronZasshiLoader.class.getSimpleName();
    private ArrayList<Zasshi> mData;

    public SeikeidenronZasshiLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Zasshi> loadInBackground() {
        Log.d(TAG, "loadInBackground");

        /*
         * Executed in background thread.
         * Prepare data here, it may take long time (Database access, URL connection, etc).
         * return value is used in onLoadFinished() method in Activity/Fragment's LoaderCallbacks.
         */

        /* A. Preparing Denshi Zasshi list */
        ArrayList<Zasshi> zasshiList = null;
        try {
            zasshiList = ZasshiProvider.createZasshiList(getContext());
        } catch (JSONException e) {
            Log.e(TAG, "createZasshiList failed", e);
            //cancelLoad();
        }
        return zasshiList;
    }

    @Override
    public void deliverResult(ArrayList<Zasshi> data) {
        Log.d(TAG, "deliverResult");

        ArrayList<Zasshi> oldData = mData;
        mData = data;

        if(isStarted()){
            Log.d(TAG, "isStarted true");
            super.deliverResult(data);
        }

        if(oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    private void releaseResources(ArrayList<Zasshi> data) {
        Log.d(TAG, "releaseResources");
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
        data = null;
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading");
        if (mData != null) {
            Log.d(TAG, "mData remaining");
            deliverResult(mData);
        } else {
            Log.d(TAG, "mData is null, forceLoad");
            forceLoad();
        }
        //super.onStartLoading();

    }

    @Override
    protected void onStopLoading() {
        Log.d(TAG, "onStopLoading");
        //super.onStopLoading();
        cancelLoad();
    }

    @Override
    protected void onReset() {
        Log.d(TAG, "onReset");
        super.onReset();
    }

    @Override
    public void onCanceled(ArrayList<Zasshi> data) {
        Log.d(TAG, "onCanceled");
        super.onCanceled(data);
    }

    @Override
    protected boolean onCancelLoad() {
        return super.onCancelLoad();
    }

}