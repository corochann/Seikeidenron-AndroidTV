package jp.seikeidenron.androidtv.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.util.Log;

import jp.seikeidenron.androidtv.common.MLog;
import jp.seikeidenron.androidtv.model.Movie;
import jp.seikeidenron.androidtv.model.YTInfo;
import jp.seikeidenron.androidtv.ui.background.PicassoBackgroundManager;

import com.google.android.youtube.player.YouTubeIntents;

import jp.seikeidenron.androidtv.common.Utils;
import jp.seikeidenron.androidtv.ui.presenter.CustomDescriptionPresenter;
import jp.seikeidenron.androidtv.ui.presenter.CustomDetailsOverviewRowPresenter;
import jp.seikeidenron.androidtv.ui.presenter.CustomFullWidthDetailsOverviewRowPresenter;
import jp.seikeidenron.androidtv.ui.presenter.DetailsDescriptionPresenter;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 *
 */
public class VideoDetailsFragment extends DetailsFragment {

    private static final String TAG = VideoDetailsFragment.class.getSimpleName();

    private static final int ACTION_PLAY_VIDEO = 1;

    private static final int FULL_WIDTH_DETAIL_THUMB_WIDTH = 220;
    private static final int FULL_WIDTH_DETAIL_THUMB_HEIGHT = 120;

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    public static final String CATEGORY_FULL_WIDTH_DETAILS_OVERVIEW_ROW_PRESENTER = "FullWidthDetailsOverviewRowPresenter";
    public static final String CATEGORY_DETAILS_OVERVIEW_ROW_PRESENTER = "DetailsOverviewRowPresenter";

    /* Attribute */
    private ArrayObjectAdapter mAdapter;
    private CustomFullWidthDetailsOverviewRowPresenter mFwdorPresenter;
    private CustomDetailsOverviewRowPresenter mDorPresenter;
    private ClassPresenterSelector mClassPresenterSelector;
    private ListRow mRelatedVideoRow = null;

    private DetailsRowBuilderTask mDetailsRowBuilderTask;

    private int mType = DetailsActivity.TYPE_INVALID;
    private YTInfo mYtInfo = null;

    /* Relation */
    private Movie mSelectedMovie;
    private PicassoBackgroundManager mPicassoBackgroundManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        int width = Utils.getDisplayWidthInPx(getActivity());
        int height = Utils.getDisplayHeightInPx(getActivity());
        MLog.v(TAG, "Display width = " + width + ", height = " + height);

        //mFwdorPresenter = new CustomFullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        mFwdorPresenter = new CustomFullWidthDetailsOverviewRowPresenter(new CustomDescriptionPresenter(), width);
        mDorPresenter = new CustomDetailsOverviewRowPresenter(new DetailsDescriptionPresenter(), getActivity());

        mPicassoBackgroundManager = new PicassoBackgroundManager(getActivity());

        mType = getActivity().getIntent().getIntExtra(DetailsActivity.KEY_TYPE, DetailsActivity.TYPE_INVALID);
        switch (mType) {
            case DetailsActivity.TYPE_YOUTUBE:
                mYtInfo = getActivity().getIntent().getParcelableExtra(DetailsActivity.KEY_ITEM);
                mSelectedMovie = new Movie();
                mSelectedMovie.setId(mYtInfo.getId());
                mSelectedMovie.setTitle(mYtInfo.getTitle());
                mSelectedMovie.setStudio(""); //ytInfo.getStudio()
                mSelectedMovie.setDescription(mYtInfo.getStudio());
                if(width < 1920) {
                    mSelectedMovie.setCardImageUrl(null);
                } else {
                    mSelectedMovie.setCardImageUrl(mYtInfo.getThumbnailUrl());
                }
                mSelectedMovie.setBackgroundImageUrl(mYtInfo.getThumbnailUrl());
                mSelectedMovie.setCategory("Youtube");
                break;
            case DetailsActivity.TYPE_INVALID:
            default:
                MLog.e(TAG, "Intent type invalid");
                mSelectedMovie = null;
                break;
        }


        mDetailsRowBuilderTask = (DetailsRowBuilderTask) new DetailsRowBuilderTask().execute(mSelectedMovie);

        setOnItemViewClickedListener(new ItemViewClickedListener());

        mPicassoBackgroundManager.updateBackgroundWithDelay(mSelectedMovie.getBackgroundImageUrl());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mClassPresenterSelector = new ClassPresenterSelector();
        //Log.v(TAG, "mFwdorPresenter.getInitialState: " + mFwdorPresenter.getInitialState());
        //mClassPresenterSelector.addClassPresenter(DetailsOverviewRow.class, mDorPresenter);
        mClassPresenterSelector.addClassPresenter(DetailsOverviewRow.class, mFwdorPresenter);
        mClassPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());

        mAdapter = new ArrayObjectAdapter(mClassPresenterSelector);
        setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        mDetailsRowBuilderTask.cancel(true);
        super.onStop();
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            MLog.v(TAG, "onItemClicked");
        }
    }

    private class DetailsRowBuilderTask extends AsyncTask<Movie, Integer, DetailsOverviewRow> {
        @Override
        protected DetailsOverviewRow doInBackground(Movie... params) {
            Log.v(TAG, "DetailsRowBuilderTask doInBackground");
            int width, height;
            if(mSelectedMovie.getCategory().equals(CATEGORY_DETAILS_OVERVIEW_ROW_PRESENTER)) {
                /* If category name is "DetailsOverviewRowPresenter", show DetailsOverviewRowPresenter for demo purpose (this class is deprecated from API level 22) */
                width = DETAIL_THUMB_WIDTH;
                height = DETAIL_THUMB_HEIGHT;
            } else {
                /* Default behavior, show FullWidthDetailsOverviewRowPresenter */
                width = FULL_WIDTH_DETAIL_THUMB_WIDTH;
                height = FULL_WIDTH_DETAIL_THUMB_HEIGHT;
            }

            DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
            try {
                // Bitmap loading must be done in background thread in Android.
                String cardImageUrl = mSelectedMovie.getCardImageUrl();
                if(cardImageUrl != null) {
                    Bitmap poster = Picasso.with(getActivity())
                            .load(cardImageUrl)
                            .resize(Utils.convertDpToPixel(getActivity().getApplicationContext(), width),
                                    Utils.convertDpToPixel(getActivity().getApplicationContext(), height))
                            .centerCrop()
                            .get();
                    row.setImageBitmap(getActivity(), poster);
                } else {
                    // Skip to set card image if it is null
                }
            } catch (IOException e) {
                Log.w(TAG, e.toString());
            }
            return row;
        }

        @Override
        protected void onPostExecute(DetailsOverviewRow row) {
            Log.v(TAG, "DetailsRowBuilderTask onPostExecute");
            /* 1st row: DetailsOverviewRow */

              /* action setting*/
            SparseArrayObjectAdapter sparseArrayObjectAdapter = new SparseArrayObjectAdapter();
            sparseArrayObjectAdapter.set(0, new Action(ACTION_PLAY_VIDEO, "Play Video"));
            //sparseArrayObjectAdapter.set(1, new Action(1, "Action 2", "label"));

            row.setActionsAdapter(sparseArrayObjectAdapter);

            mFwdorPresenter.setOnActionClickedListener(new OnActionClickedListener() {
                @Override
                public void onActionClicked(Action action) {

                    switch (mType) {
                        case DetailsActivity.TYPE_YOUTUBE: /* Start Youtube playback */
                            if (action.getId() == ACTION_PLAY_VIDEO) {
                                Intent intent = YouTubeIntents.
                                        createPlayVideoIntentWithOptions(getActivity(), mYtInfo.getVideoId(), true, true);
                                startActivity(intent);
                            }
                        case DetailsActivity.TYPE_INVALID:
                        default:
                            MLog.e(TAG, "Intent type invalid");
                            break;
                    }
                }
            });

            /* 2nd row: ListRow CardPresenter */
/*
            CardPresenter cardPresenter = new CardPresenter();
            ArrayObjectAdapter cardRowAdapter = new ArrayObjectAdapter(cardPresenter);
            HeaderItem header = new HeaderItem(0, "Related Videos");
            mRelatedVideoRow = new ListRow(header, cardRowAdapter);
*/

            mAdapter = new ArrayObjectAdapter(mClassPresenterSelector);
            /* 1st row */
            mAdapter.add(row);
            /* 2nd row */
            if(mRelatedVideoRow != null) {
                mAdapter.add(mRelatedVideoRow);
            }
            //mAdapter.add(new ListRow(headerItem, listRowAdapter));

            /* 3rd row */
            //adapter.add(new ListRow(headerItem, listRowAdapter));
            setAdapter(mAdapter);
        }
    }
}