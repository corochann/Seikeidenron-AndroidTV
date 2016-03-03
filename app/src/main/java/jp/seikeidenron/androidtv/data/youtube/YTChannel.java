package jp.seikeidenron.androidtv.data.youtube;

import android.content.Context;
import android.graphics.Bitmap;

import jp.seikeidenron.androidtv.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import jp.seikeidenron.androidtv.common.MLog;
import jp.seikeidenron.androidtv.common.Utils;

/**
 * YouTube Data API v3 Channel data
 */
public class YTChannel {

    private static final String TAG = YTChannel.class.getSimpleName();

    private String mChannelId;
    private String mTitle;
    private String mDescription;
    private String mThumbnailUrl;
    private Bitmap mThumbnailBitmap;
    private String mPublishedAt;
    private ArrayList<YTPlaylist> mPlaylistArray;

    /**
     * Constructor, set channelID.
     * Call following methods to complete data preparation
     * 1. {@link #buildChannelInfo()}
     * 2. {@link #buildPlaylistArray()}
     * @param channelId
     */
    public YTChannel(String channelId) {
        mChannelId = channelId;
    }


    /*--- Getter ---*/
    public String getChannelId() {
        return mChannelId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public Bitmap getThumbnailBitmap() {
        if(mThumbnailBitmap == null) {
            MLog.w(TAG, "Bitmap is null. Did you create Bitmap from URL beforehand?");
        }
        return mThumbnailBitmap;
    }

    public String getPublishedAt() {
        return mPublishedAt;
    }

    public ArrayList<YTPlaylist> getPlaylistArray() {
        return mPlaylistArray;
    }

    /*---- build tools ---*/
    public void buildAll() {
        /* Data for YTChannel instance */
        buildChannelInfo();
        buildPlaylistArray();

        /* Data for YTPlaylist instance */
        for (YTPlaylist playlist : mPlaylistArray) {
            playlist.buildVideoArray();
        }
    }

    /**
     * set title, description, thumbnail URL and publishedAt members
     * by accessing YouTube Data API
     * Ex. https://www.googleapis.com/youtube/v3/channels?part=snippet&id=UCH0hL0DApXKRa2AqmBgA05g&key=AIzaSyA_PQN-MjQ7h27c6wzq1gjhVm-fMyyLJMU
     */
    public void buildChannelInfo() {

        String url = new YTUtil.YouTubeDataAPIURLBuilder(YTUtil.YOUTUBE_CHANNELS)
                .append("part", "snippet")
                .append("id", mChannelId)
                .build();

        MLog.v(TAG, "buildChannelInfo url: " + url);

        JSONObject json = Utils.parseUrl(url);
        if(json == null) {
            MLog.e(TAG, "parseUrl failed");
            return;
        }
        /* parse JSON */
        try {
            JSONObject channelJSON = json.getJSONArray("items").getJSONObject(0);
            String channelId = channelJSON.getString("id"); // It should be same with mChannelId.
            JSONObject snippetJSON = channelJSON.getJSONObject("snippet");
            mTitle = snippetJSON.getString("title");
            mDescription = snippetJSON.getString("description");
            mPublishedAt = snippetJSON.getString("publishedAt");
            JSONObject thumbnailJSON = snippetJSON.
                    getJSONObject("thumbnails")
                    .getJSONObject("default");  // "default", "medium", "high" are available
            mThumbnailUrl = thumbnailJSON.getString("url");
        } catch (JSONException e) {
            MLog.e(TAG, e.toString());
            //Toast.makeText(Context, "YouTube Data get Failed.", Toast.LENGTH_SHORT).show();
        }

        MLog.v(TAG, "channelId = " + mChannelId +
                        "title = " + mTitle +
                        ", description = " + mDescription +
                        ", publishedAt = " + mPublishedAt +
                        ", Thumbnail URL = " + mThumbnailUrl);
    }

    /**
     * set PlaylistArray member
     * by accessing YouTube Data API
     * Ex. https://www.googleapis.com/youtube/v3/playlists?part=snippet&channelId=UCVjS9AuBloqJJjhsy3vIfug&maxResults=50&pageToken=&key=AIzaSyA_PQN-MjQ7h27c6wzq1gjhVm-fMyyLJMU
     */
    public void buildPlaylistArray() {
        MLog.v(TAG, "buildPlaylistArray");
        String nextPageToken = "";
        mPlaylistArray = new ArrayList<>(); // Init

        while(nextPageToken != null) {
            String url = new YTUtil.YouTubeDataAPIURLBuilder(YTUtil.YOUTUBE_PLAYLISTS)
                    .append("part", "snippet")
                    .append("channelId", mChannelId)
                    .append("maxResults", "50")
                    .append("channelId", mChannelId)
                    .append("pageToken", nextPageToken)
                    .build();
            MLog.v(TAG, "url: " + url);

            JSONObject json = Utils.parseUrl(url);
            if(json == null) {
                MLog.e(TAG, "parseUrl failed");
                return;
            }
        /* parse JSON */
            try {
                if(!json.has("nextPageToken")) {
                    nextPageToken = null;
                } else {
                    nextPageToken = json.getString("nextPageToken");
                }

                JSONArray ItemArr = json.getJSONArray("items");
                for ( int i = 0; i < ItemArr.length(); i++ ) {
                    /* get playlistId */
                    JSONObject playlistJSON = ItemArr.getJSONObject(i);
                    String playlistId = playlistJSON.getString("id");
                    JSONObject snippetJSON = playlistJSON.getJSONObject("snippet");
                    String channelId     = snippetJSON.getString("channelId"); // It should be same with mChannelId.
                    String playlistTitle = snippetJSON
                            //.getJSONObject("localized")
                            .getString("title");
                    String playlistDescription = snippetJSON.getString("description");
                    String playlistPublishedAt = snippetJSON.getString("publishedAt");

                    String playlistThumbURL = "";
                    int playlistThumbWidth  = 0;
                    int playlistThumbHeight = 0;

                    if(snippetJSON.has("thumbnails")) {
                        JSONObject thumbnailJSONSet = snippetJSON.getJSONObject("thumbnails");
                        JSONObject thumbnailJSON;
                        // "default", "medium", "high", "standard", "maxres" are available
                        // Some of them Don't have "standard"!!
                        if(thumbnailJSONSet.has("standard")) {
                            thumbnailJSON = thumbnailJSONSet.getJSONObject("standard");
                        } else if (thumbnailJSONSet.has("medium")) {
                            thumbnailJSON = thumbnailJSONSet.getJSONObject("medium");
                        } else {
                            thumbnailJSON = thumbnailJSONSet.getJSONObject("default");
                        }
                        playlistThumbURL = thumbnailJSON.getString("url");
                        playlistThumbWidth  = thumbnailJSON.getInt("width");
                        playlistThumbHeight = thumbnailJSON.getInt("height");
                    }

                    mPlaylistArray.add(new YTPlaylist(channelId, playlistId, playlistTitle, playlistDescription, playlistPublishedAt,
                            playlistThumbURL, playlistThumbWidth, playlistThumbHeight));
                    MLog.v(TAG, "Playlist Info: id = " + playlistId +
                            ", title = " + playlistTitle +
                            ", description = " + playlistDescription +
                            ", publishedAt = " + playlistPublishedAt +
                            ", Thumbnail URL = " + playlistThumbURL +
                            ", Thumbnail Width = " + playlistThumbWidth +
                            ", Thumbnail Height = " + playlistThumbHeight);
                }
            } catch (JSONException e) {
                MLog.e(TAG, e.toString());
            }
        }
   }

    public void buildThumbnailBitmap(Context context, int w, int h) {
        if(mThumbnailUrl != null) {
            try {
                mThumbnailBitmap = Picasso
                        .with(context)
                        .load(mThumbnailUrl)
                        .resize(w, h)
                                //.resize(YTPresenter.CARD_WIDTH, YTPresenter.CARD_HEIGHT)
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