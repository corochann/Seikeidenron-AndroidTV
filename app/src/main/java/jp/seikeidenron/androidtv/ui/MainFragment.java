package jp.seikeidenron.androidtv.ui;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.seikeidenron.androidtv.R;

import jp.seikeidenron.androidtv.common.MLog;
import jp.seikeidenron.androidtv.data.SeikeidenronZasshiLoader;
import jp.seikeidenron.androidtv.data.ZasshiProvider;
import jp.seikeidenron.androidtv.data.youtube.YTChannel;
import jp.seikeidenron.androidtv.data.youtube.YTLoader;
import jp.seikeidenron.androidtv.data.youtube.YTPlaylist;
import jp.seikeidenron.androidtv.data.youtube.YTPlaylistItem;
import jp.seikeidenron.androidtv.model.YTInfo;
import jp.seikeidenron.androidtv.model.Zasshi;
import jp.seikeidenron.androidtv.ui.background.PicassoBackgroundManager;
import jp.seikeidenron.androidtv.ui.presenter.IconHeaderItemPresenter;
import jp.seikeidenron.androidtv.ui.presenter.YTPresenter;
import jp.seikeidenron.androidtv.ui.presenter.ZasshiPresenter;

import java.util.ArrayList;

/**
 * MainFragment
 */
public class MainFragment extends BrowseFragment {
    private static final String TAG = MainFragment.class.getSimpleName();

    /*--- Constant ---*/
    /* Header Ids */

    public static final int HEADER_ID_DENSHIZASSHI = 1; // denshi zasshi
    public static final int HEADER_ID_YOUTUBE = 2; // youtube
    public static final int HEADER_ID_NICONICO= 3; // niconico
    public static final int HEADER_ID_GRID_ITEM = 4; // setting

    /* Grid row item settings */
    private static final int GRID_ITEM_WIDTH = 300;
    private static final int GRID_ITEM_HEIGHT = 200;

    private static final int ZASSHI_ITEM_LOADER_ID = 1;
    private static final int YT_LOADER_ID = 2;

    /* Attribute */
    private ArrayObjectAdapter mRowsAdapter;
    private ListRow mZasshiListRow;
    private ListRow mYTListRow;
    private ArrayList<ListRow> mVideoListRowArray = new ArrayList<>();
    private ListRow mGridItemListRow;

    /* Relation */
    private static PicassoBackgroundManager picassoBackgroundManager = null;
    YTChannel        mYTChannel = null; // Seikeidenron YT Channel data


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        setupUIElements();

        loadRows();
        setRows();
        LoaderManager.enableDebugLogging(true);
        getLoaderManager().initLoader(ZASSHI_ITEM_LOADER_ID, null, new MainFragmentZasshiLoaderCallbacks());
        getLoaderManager().initLoader(YT_LOADER_ID, null, new MainFragmentYTLoaderCallbacks());

        setupEventListeners();

        picassoBackgroundManager = new PicassoBackgroundManager(getActivity());
        picassoBackgroundManager.updateBackgroundWithDelay();
    }

    private void setupEventListeners() {
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            // each time the item is clicked, code inside here will be executed.
            Log.d(TAG, "onItemClicked: item = " + item.toString());
            if (item instanceof String) {
                if (getString(R.string.seikeidenron_about).equals(item)) {
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    intent.putExtra(WebViewActivity.KEY_URL, WebViewActivity.SEIKEIDENRON_ABOUT_URL);
                    intent.putExtra(WebViewActivity.KEY_IS_JAVASCRIPT_ENABLED, true);
                    startActivity(intent);
                }
            } else if (item instanceof YTInfo){
                YTInfo ytInfo = (YTInfo) item;
                MLog.v(TAG, "YTInfo " + ytInfo.getTitle() + "Clicked");

                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.KEY_TYPE, DetailsActivity.TYPE_YOUTUBE);
                intent.putExtra(DetailsActivity.KEY_ITEM, ytInfo);
                getActivity().startActivity(intent);

            } else if (item instanceof Zasshi){
                Zasshi zasshi = (Zasshi) item;
                Intent intent = new Intent(getActivity(), ZasshiActivity.class);
                //intent.putExtra(ZasshiActivity.KEY_VOLUME_NUMBER, zasshi.getId());
                intent.putExtra(ZasshiActivity.KEY_ZASSHI, zasshi);
                startActivity(intent);
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            // each time the item is selected, code inside here will be executed.
            if (item instanceof String) {                    // GridItemPresenter
                Log.v(TAG, "onItemSelected String");
                // Do not update background. It is better to keep previous background.
                //picassoBackgroundManager.updateBackgroundWithDelay();
            } else if (item instanceof YTInfo) {
                Log.v(TAG, "onItemSelected YTInfo");
                // Do not update background. It is better to keep previous background.
            } else if (item instanceof Zasshi) {
                Log.v(TAG, "onItemSelected Zasshi");
                String bgUrl = ((Zasshi) item).getBackgroundImageUrl();
                Log.v(TAG, "updating background image, URL = " + bgUrl);
                picassoBackgroundManager.updateBackgroundWithDelay(bgUrl);

            } else {
                Log.v(TAG, "onItemSelected Others");
            }
        }
    }

    private void setupUIElements() {
        setBadgeDrawable(getActivity().getResources().getDrawable(R.drawable.green_logo));
        setTitle(getString(R.string.app_title)); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));

        setHeaderPresenterSelector(new PresenterSelector() {
            @Override
            public Presenter getPresenter(Object o) {
                return new IconHeaderItemPresenter();
            }
        });
    }



    private void loadRows() {
        /* GridItemPresenter */
        mGridItemListRow = makeGridItemListRow(HEADER_ID_GRID_ITEM);
    }

    private synchronized void setRows() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        addRow(mZasshiListRow);
        addRow(mYTListRow);
        addRow(mGridItemListRow);

        /* Set */
        setAdapter(mRowsAdapter);
    }

    private void addRow(Row row) {
        if(row != null) {
            mRowsAdapter.add(row);
        } else {
            MLog.i(TAG, "skipped. Row is null ");
        }
        return;
    }

    private ListRow makeGridItemListRow(int headerId) {
        //HeaderItem gridItemPresenterHeader = new HeaderItem(headerId, getString(R.string.title_header_setting));
        HeaderItem gridItemPresenterHeader = new HeaderItem(headerId, getString(R.string.title_header_about));
        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add(getString(R.string.seikeidenron_about));
        return new ListRow(gridItemPresenterHeader, gridRowAdapter);
    }

    private class MainFragmentZasshiLoaderCallbacks implements LoaderManager.LoaderCallbacks<ArrayList<Zasshi>> {
        @Override
        public Loader<ArrayList<Zasshi>> onCreateLoader(int id, Bundle args) {
            /* Create new Loader */
            Log.d(TAG, "onCreateLoader");
            if( id == ZASSHI_ITEM_LOADER_ID) {
                Log.d(TAG, "create SeikeidenronZasshiLoader");
                //return new SeikeidenronZasshiLoader(getActivity());
                return new SeikeidenronZasshiLoader(getActivity().getApplicationContext());
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Zasshi>> loader, ArrayList<Zasshi> data) {
            Log.d(TAG, "onLoadFinished");
            /* Loader data has prepared. Start updating UI here */
            switch (loader.getId()) {
                case ZASSHI_ITEM_LOADER_ID:
                    Log.d(TAG, "Zasshi list UI update");

                    int index = 0;

                    /* Denshizasshi - ZasshiPresenter  */
                    ZasshiPresenter    zasshiPresenter  = new ZasshiPresenter();
                    ArrayObjectAdapter zasshiRowAdapter = new ArrayObjectAdapter(zasshiPresenter);

                    ArrayList<Zasshi> zasshiList = null;
                    try {
                            zasshiList = ZasshiProvider.createZasshiList(getActivity()); // = data;
                    } catch (Exception e) {
                        MLog.e(TAG, e.toString());
                    }
                    if (zasshiList != null) {
                        for(Zasshi zasshi : zasshiList) {
                            zasshiRowAdapter.add(zasshi);
                        }
                    }

                    HeaderItem zasshiHeader = new HeaderItem(HEADER_ID_DENSHIZASSHI, getString(R.string.title_header_zasshi));
                    mZasshiListRow = new ListRow(zasshiHeader, zasshiRowAdapter);

                    /* GridItemPresenter */
                    if (mGridItemListRow == null) {
                        mGridItemListRow = makeGridItemListRow(HEADER_ID_GRID_ITEM);
                    }

                    /* Set */
                    setRows();
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Zasshi>> loader) {
            Log.d(TAG, "onLoadReset");
            /* When it is called, Loader data is now unavailable due to some reason. */

        }


    }

    private class MainFragmentYTLoaderCallbacks implements LoaderManager.LoaderCallbacks<YTChannel> {
        @Override
        public Loader<YTChannel> onCreateLoader(int id, Bundle args) {
            /* Create new Loader */
            if( id == YT_LOADER_ID) {
                Log.d(TAG, "create YTLoader");
                return new YTLoader(getActivity().getApplicationContext());
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<YTChannel> loader, YTChannel data) {
            Log.d(TAG, "onLoadFinished");
            /* Loader data has prepared. Start updating UI here */
            switch (loader.getId()) {
                case YT_LOADER_ID:
                    Log.d(TAG, "VideoLists UI update");


                    /* YT Channel - YTPresenter  */
                    ArrayObjectAdapter ytRowAdapter = new ArrayObjectAdapter(new YTPresenter());

                    mYTChannel = data;

                    ArrayList<YTPlaylist> seikeidenronPlaylists = mYTChannel.getPlaylistArray();
                    int ytVideoId = 1;
                    for(YTPlaylist playlist : seikeidenronPlaylists) {

                        ArrayList<YTPlaylistItem> playlistItemArray = playlist.getPlaylistItemArray();
                        //TODO: currently Playlists size must be 1 only, need arraylist for ListRow to show multile playlists.
                        for(YTPlaylistItem playlistItem : playlistItemArray) {
                            YTInfo ytVideo;
                            ytVideo = new YTInfo(
                                    ytVideoId++,
                                    playlistItem.getTitle(),
                                    playlistItem.getDescription(),
                                    playlistItem.getThumbnail().getBitmap(),
                                    playlistItem.getVideoId(),
                                    playlistItem.getPlaylistItemId(),
                                    playlistItem.getThumbnail().getUrl());
                            ytRowAdapter.add(ytVideo);
                        }
                    }
                    HeaderItem ytHeader = new HeaderItem(HEADER_ID_YOUTUBE, getString(R.string.title_header_youtube));
                    mYTListRow = new ListRow(ytHeader, ytRowAdapter);
            }
            setRows();
        }

        @Override
        public void onLoaderReset(Loader<YTChannel> loader) {
            Log.d(TAG, "onLoadReset");
            /* When it is called, Loader data is now unavailable due to some reason. */

        }


    }


    /**
     * from AOSP sample source code
     * GridItemPresenter class. Show TextView with item type String.
     */
    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {

        }
    }
}
