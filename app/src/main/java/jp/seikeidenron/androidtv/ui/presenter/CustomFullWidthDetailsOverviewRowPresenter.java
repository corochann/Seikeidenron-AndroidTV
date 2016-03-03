package jp.seikeidenron.androidtv.ui.presenter;

import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Presenter to demonstrate {@link FullWidthDetailsOverviewRowPresenter}
 */
public class CustomFullWidthDetailsOverviewRowPresenter extends FullWidthDetailsOverviewRowPresenter {

    private static final String TAG = CustomFullWidthDetailsOverviewRowPresenter.class.getSimpleName();
    public static final int DEFAULT_DISPLAY_WIDTH_IN_PX = 1920; // assume display size 1920 x 1080 as default
    public static final int DISPLAY_WIDTH_SMALL = 1920;         // Threshold to assume display size is small

    private int mDisplayWidth = DEFAULT_DISPLAY_WIDTH_IN_PX;

    public CustomFullWidthDetailsOverviewRowPresenter(Presenter presenter) {
        super(presenter);
    }

    public CustomFullWidthDetailsOverviewRowPresenter(Presenter presenter, int width) {
        super(presenter);
        mDisplayWidth = width;
    }

    @Override
    protected void onRowViewAttachedToWindow(RowPresenter.ViewHolder vh) {
        Log.v(TAG, "onRowViewAttachedToWindow");
        if(mDisplayWidth < DISPLAY_WIDTH_SMALL) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) ((FullWidthDetailsOverviewRowPresenter.ViewHolder) vh).getDetailsDescriptionFrame().getLayoutParams();
            params.setMarginStart(100);
        }
        super.onRowViewAttachedToWindow(vh);
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        Log.v(TAG, "onBindRowViewHolder");
        super.onBindRowViewHolder(holder, item);
    }

    @Override
    protected void onLayoutOverviewFrame(ViewHolder viewHolder, int oldState, boolean logoChanged) {
        Log.v(TAG, "onLayoutOverviewFrame");

        /* Please try selecting either one. */
        //setState(viewHolder, FullWidthDetailsOverviewRowPresenter.STATE_SMALL);
        //setState(viewHolder, FullWidthDetailsOverviewRowPresenter.STATE_FULL);
        setState(viewHolder, FullWidthDetailsOverviewRowPresenter.STATE_HALF);  // Default behavior
        setAlignmentMode(ALIGN_MODE_START);

        super.onLayoutOverviewFrame(viewHolder, oldState, logoChanged);
    }
}