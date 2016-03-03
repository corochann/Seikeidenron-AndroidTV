package jp.seikeidenron.androidtv.data.youtube;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

/**
 * Loader class which prepares {@link YTChannel} class data
 */
public class YTLoader extends AsyncTaskLoader<YTChannel> {

    private static final String TAG = YTLoader.class.getSimpleName();
    YTChannel mData;

    public YTLoader(Context context) {
        super(context);
    }

    @Override
    public YTChannel loadInBackground() {
        Log.d(TAG, "loadInBackground");

        /*
         * Executed in background thread.
         * Prepare data here, it may take long time (Database access, URL connection, etc).
         * return value is used in onLoadFinished() method in Activity/Fragment's LoaderCallbacks.
         */
        return YTSeikeidenronProvider.buildMedia(getContext());
    }

    @Override
    public void deliverResult(YTChannel data) {
        Log.d(TAG, "deliverResult");

        YTChannel oldData = mData;
        mData = data;

        if(isStarted()){
            Log.d(TAG, "isStarted true");
            super.deliverResult(data);
        }

        if(oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    private void releaseResources(YTChannel data) {
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
    public void onCanceled(YTChannel data) {
        Log.d(TAG, "onCanceled");
        super.onCanceled(data);
    }

    @Override
    protected boolean onCancelLoad() {
        return super.onCancelLoad();
    }
}