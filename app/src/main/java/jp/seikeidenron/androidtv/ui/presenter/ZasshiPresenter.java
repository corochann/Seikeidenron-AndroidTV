package jp.seikeidenron.androidtv.ui.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import jp.seikeidenron.androidtv.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.HashMap;

import jp.seikeidenron.androidtv.common.MLog;
import jp.seikeidenron.androidtv.common.Utils;
import jp.seikeidenron.androidtv.model.ImageInfo;
import jp.seikeidenron.androidtv.model.Zasshi;

/**
 * ZasshiPresenter is very similar to CardPresenter.
 * Difference is how to specify imageView's image data.
 * ZasshiPresenter will use {@link ImageInfo} model, where image is specified drawable/bitmap object,
 * (cf. CardPresenter uses CardInfo where image is specified by resourceId)
 */
public class ZasshiPresenter extends Presenter {

    /*-----------------------------------------------------*/
    // Constant
    /*-----------------------------------------------------*/
    private static final String TAG = ZasshiPresenter.class.getSimpleName();

    private static int CARD_WIDTH = 320; // 313;
    private static int CARD_HEIGHT = CARD_WIDTH * 7/5; // 176;
    private static final int CARD_IMAGE_PADDING_HORIZONTAL = (int)(CARD_WIDTH * 0.0);
    private static final int CARD_IMAGE_PADDING_VERTICAL = (int)(CARD_HEIGHT * 0.0);
    private static final int IMAGE_WIDTH = CARD_WIDTH - 2 * CARD_IMAGE_PADDING_HORIZONTAL;
    private static final int IMAGE_HEIGHT = CARD_HEIGHT - 2 * CARD_IMAGE_PADDING_VERTICAL;
    /*-----------------------------------------------------*/
    // Relation
    /*-----------------------------------------------------*/
    private static Context mContext;
    /* key: CARD_ID - value: ViewHolder reference */
    private HashMap<Integer, WeakReference<ViewHolder>> viewHolderHashMap = new HashMap<>();

    /*-----------------------------------------------------*/
    // Attribute
    /*-----------------------------------------------------*/

    /*-----------------------------------------------------*/
    // Getter/Setter
    /*-----------------------------------------------------*/
    public HashMap<Integer, WeakReference<ViewHolder>> getViewHolderHashMap(){
        return viewHolderHashMap;
    }

    /*-----------------------------------------------------*/
    // Ctor
    /*-----------------------------------------------------*/
    public ZasshiPresenter() {

    }

    /*-----------------------------------------------------*/
    //	Override/Implementation
    /*-----------------------------------------------------*/

    /*-----------------------------------------------------*/
    //	Public method
    /*-----------------------------------------------------*/
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        MLog.d(TAG, "onCreateViewHolder");
        mContext = parent.getContext();

        ImageCardView cardView = new ImageCardView(mContext);
        //cardView.setCardType(BaseCardView.CARD_TYPE_INFO_UNDER);
        cardView.setCardType(BaseCardView.CARD_TYPE_MAIN_ONLY);
        //cardView.setInfoVisibility(BaseCardView.CARD_REGION_VISIBLE_ALWAYS);
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        //cardView.setBackgroundColor(mContext.getResources().getColor(R.color.fastlane_background));
        //cardView.setBackgroundColor(mContext.getResources().getColor(R.color.grey10));

/*
        Preferences preferences = new Preferences(mContext);
        CARD_WIDTH = preferences.getIconSizeWidth();
        CARD_HEIGHT = preferences.getIconSizeHeight();
*/

        TextView titleTextView = new TextView(mContext);
        //cardView.addView(titleTextView); //TODO: tmporary impl.

        return new ViewHolder(cardView, titleTextView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        Zasshi zasshi = (Zasshi) item;
        MLog.v(TAG, "onBindViewHolder title: " + zasshi.getTitle());

        ((ViewHolder) viewHolder).setZasshi(zasshi);
        /* store information to viewHolder */
        ImageCardView imageCardView = ((ViewHolder) viewHolder).mCardView;
        //((ViewHolder) viewHolder).mTitleTextView.setText(zasshi.getTitle());
        //titleTextView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent)); // not work now..
        //titleTextView.setDrawingCacheBackgroundColor(mContext.getResources().getColor(R.color.transparent));

        imageCardView.setTitleText(zasshi.getTitle());
        imageCardView.setContentText(zasshi.getStudio());
        imageCardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
                /* set padding */
        ImageView mainImageView = imageCardView.getMainImageView();
        mainImageView.setPadding(
                CARD_IMAGE_PADDING_HORIZONTAL, // left
                CARD_IMAGE_PADDING_VERTICAL,   // top
                CARD_IMAGE_PADDING_HORIZONTAL, // right
                CARD_IMAGE_PADDING_VERTICAL    // botoom
        );

        ((ViewHolder) viewHolder).updateCardViewImage(zasshi.getCardImageURI());

        /* store viewHolder reference to viewHolderHashMap */
        viewHolderHashMap.put((int) zasshi.getId(), new WeakReference<>((ViewHolder) viewHolder));
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        MLog.v(TAG, "onUnbindViewHolder title: " + ((ViewHolder) viewHolder).getZasshi().getTitle());

        viewHolderHashMap.remove(((ViewHolder) viewHolder).getZasshi().getId());
    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
        MLog.v(TAG, "onViewAttachedToWindow title: " + ((ViewHolder) viewHolder).getZasshi().getTitle());
    }

    public static class PicassoImageCardViewTarget implements Target {
        private ImageCardView mImageCardView;

        public PicassoImageCardViewTarget(ImageCardView imageCardView) {
            mImageCardView = imageCardView;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            Drawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
            mImageCardView.setMainImage(bitmapDrawable);
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
            mImageCardView.setMainImage(drawable);
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {
            // Do nothing, default_background manager has its own transitions
        }
    }

    /*-----------------------------------------------------*/
    //	Protected method
    /*-----------------------------------------------------*/

    /*-----------------------------------------------------*/
    //	Private method
    /*-----------------------------------------------------*/
    
    /*-----------------------------------------------------*/
    //	Inner class
    /*-----------------------------------------------------*/
    public class ViewHolder extends Presenter.ViewHolder {
        private Zasshi mZasshi;
        private ImageCardView mCardView;
        private Drawable mDefaultCardImage;
        private PicassoImageCardViewTarget mImageCardViewTarget;
        private TextView mTitleTextView;

        public ViewHolder(View view) {
            super(view);
            mCardView = (ImageCardView) view;
            mImageCardViewTarget = new PicassoImageCardViewTarget(mCardView);
            mDefaultCardImage = mContext.getDrawable(R.drawable.default_background);
        }

        public ViewHolder(View view, TextView textView) {
            super(view);
            mCardView = (ImageCardView) view;
            mImageCardViewTarget = new PicassoImageCardViewTarget(mCardView);
            mDefaultCardImage = mContext.getDrawable(R.drawable.default_background);
            mTitleTextView    = textView;
        }

        public void setZasshi(Zasshi zasshi) {
            mZasshi = zasshi;
        }

        public Zasshi getZasshi() {
            return mZasshi;
        }

        public ImageCardView getCardView() {
            return mCardView;
        }

        protected void updateCardViewImage(URI uri) {
            Picasso.with(mContext)
                    .load(uri.toString())
                    .resize(Utils.convertDpToPixel(mContext, CARD_WIDTH),
                            Utils.convertDpToPixel(mContext, CARD_HEIGHT))
                    .placeholder(mDefaultCardImage)
                    .error(mDefaultCardImage)
                    .into(mImageCardViewTarget);
        }

        protected void updateCardViewImage(int resourceId) {
            Picasso.with(mContext)
                    .load(resourceId)
                    .resize(Utils.convertDpToPixel(mContext, CARD_WIDTH),
                            Utils.convertDpToPixel(mContext, CARD_HEIGHT))
                    .error(mDefaultCardImage)
                    .into(mImageCardViewTarget);
        }

        protected void updateCardViewImage(Bitmap bitmap) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);
            Drawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), scaledBitmap);
            mImageCardViewTarget.mImageCardView.setMainImage(bitmapDrawable);
        }
    }
}