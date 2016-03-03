package jp.seikeidenron.androidtv.data.youtube;

import android.content.Context;
import android.graphics.Bitmap;

import jp.seikeidenron.androidtv.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jp.seikeidenron.androidtv.common.MLog;
import jp.seikeidenron.androidtv.ui.presenter.YTPresenter;

/**
 * YouTube API Config
 */
public class YTUtil {

    private static final String TAG = YTUtil.class.getSimpleName();

    public static final int YT_IMAGE_DEFAULT_WIDTH = YTPresenter.IMAGE_WIDTH;
    public static final int YT_IMAGE_DEFAULT_HEIGHT = YTPresenter.IMAGE_HEIGHT;

    /**
     * TODO: Please replace this with a valid API key which is enabled for the
     * YouTube Data API v3 service. Go to the
     * <a href="https://console.developers.google.com/">Google Developers Console</a>
     * to register a new developer key.
     */
    public static final String DEVELOPER_KEY = ""; 

    /**
     * Youtube Data API URL Construction
     */
    public static final String YOUTUBE_DATA_API_PREFIX = "https://www.googleapis.com/youtube/v3/";

    /**
     * YouTube Data API type
     */
    public static final String YOUTUBE_CHANNELS = "channels";
    public static final String YOUTUBE_PLAYLISTS = "playlists";
    public static final String YOUTUBE_PLAYLISTITEMS = "playlistItems";
    public static final String YOUTUBE_VIDEOS = "videos";

    /**
     * query key string for specifying API key
     */
    public static final String YOUTUBE_KEY_KEY = "key";

    public static class YouTubeDataAPIURLBuilder {
        String mURL;

        /**
         *
         * @param type
         */
        public YouTubeDataAPIURLBuilder(String type) {
            mURL = YOUTUBE_DATA_API_PREFIX + type + "?";
        }

        public YouTubeDataAPIURLBuilder append(String key, String value) {
            try {
                String encodedKey   = URLEncoder.encode(key, "UTF-8");
                String encodedValue = URLEncoder.encode(value, "UTF-8");
                mURL += encodedKey + "=" + encodedValue + "&";
            } catch (UnsupportedEncodingException e) {
                MLog.e(TAG, e.toString());
            }
            return this;
        }

        public String build() {
            this.append(YOUTUBE_KEY_KEY, DEVELOPER_KEY);
            return mURL;
        }
    }

    public static class YTPlaylistThumbnail {
        private String mUrl;
        private int    mWidth;
        private int    mHeight;
        private Bitmap mBitmap = null; // Bitmap instance

        YTPlaylistThumbnail(String url, int w, int h) {
            mUrl = url;
            mWidth = w;
            mHeight = h;
        }

        public String getUrl() {
            return mUrl;
        }

        public int getWidth() {
            return mWidth;
        }

        public int getHeight() {
            return mHeight;
        }

        public Bitmap getBitmap() {
            if(mBitmap == null) {
                MLog.w(TAG, "Bitmap is null. Did you create Bitmap from URL beforehand?");
            }
            return mBitmap;
        }

        /**
         * build Bitmap from URL
         */
        public void buildBitmap(Context context) {
            buildBitmap(context, YT_IMAGE_DEFAULT_WIDTH, YT_IMAGE_DEFAULT_HEIGHT);
        }

        public void buildBitmap(Context context, int w, int h) {
            if(mUrl != null) {
                try {
                    mBitmap = Picasso
                            .with(context)
                            .load(mUrl)
                            .resize(w, h)
                                    //.centerCrop()
                            .error(context.getDrawable(R.drawable.default_background))
                            .get();
                } catch (IOException e) {
                    MLog.e(TAG, e.toString());
                }
            } else {
                MLog.w(TAG, "ThumbnailURL is null!");
            }
        }
    }
}