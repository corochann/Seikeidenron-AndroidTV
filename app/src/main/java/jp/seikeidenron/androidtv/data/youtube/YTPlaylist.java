package jp.seikeidenron.androidtv.data.youtube;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.seikeidenron.androidtv.common.MLog;
import jp.seikeidenron.androidtv.common.Utils;

/**
 * YouTube Data API v3 Playlist data
 */
public class YTPlaylist {

    private static final String TAG = YTPlaylist.class.getSimpleName();

    private String mChannelId;  // Parent channelId of this playlist
    private String mPlaylistId; // The id of this playlist
    private String mTitle;
    private String mDescription;
    private String mPublishedAt;
    private YTUtil.YTPlaylistThumbnail mThumbnail;
    private ArrayList<YTPlaylistItem> mPlaylistItemArray;

    public YTPlaylist(String playlistId) {
        mPlaylistId = playlistId;
    }

    public YTPlaylist(String channelId, String playlistId, String title, String description,
                      String publishedAt, String thumbUrl, int thumbWidth, int thumbHeight) {
        mChannelId  = channelId;
        mPlaylistId = playlistId;
        mTitle = title;
        mDescription = description;
        mPublishedAt = publishedAt;
        mThumbnail = new YTUtil.YTPlaylistThumbnail(thumbUrl, thumbWidth, thumbHeight);
    }

    /*--- Getter ---*/
    public String getChannelId() {
        return mChannelId;
    }

    public String getPlaylistId() {
        return mPlaylistId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getPublishedAt() {
        return mPublishedAt;
    }

    public YTUtil.YTPlaylistThumbnail getThumbnail() {
        return mThumbnail;
    }

    public ArrayList<YTPlaylistItem> getPlaylistItemArray() {
        return mPlaylistItemArray;
    }

    /**
     * set VideoArray member
     * by accessing YouTube Data API
     * Ex.
     */
    public void buildVideoArray() {
        MLog.v(TAG, "buildVideoArray");
        String nextPageToken = "";
        mPlaylistItemArray = new ArrayList<>(); // Init

        while(nextPageToken != null) {
            String url = new YTUtil.YouTubeDataAPIURLBuilder(YTUtil.YOUTUBE_PLAYLISTITEMS)
                    .append("part", "snippet")
                    .append("playlistId", mPlaylistId)
                    .append("maxResults", "50")
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
                    JSONObject playlistItemJSON = ItemArr.getJSONObject(i);
                    String playlistItemId = playlistItemJSON.getString("id");

                    JSONObject snippetJSON = playlistItemJSON.getJSONObject("snippet");

                    String channelId         = snippetJSON.getString("channelId");
                    String playlistItemTitle = snippetJSON
                            //.getJSONObject("localized")
                            .getString("title");
                    String playlistItemDescription = snippetJSON.getString("description");
                    String playlistItemPublishedAt = snippetJSON.getString("publishedAt");

                    String playlistItemThumbURL = "";
                    int playlistItemThumbWidth  = 0;
                    int playlistItemThumbHeight = 0;

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
                        playlistItemThumbURL = thumbnailJSON.getString("url");
                        playlistItemThumbWidth  = thumbnailJSON.getInt("width");
                        playlistItemThumbHeight = thumbnailJSON.getInt("height");
                    }



                    String playlistId = snippetJSON.getString("playlistId");
                    int position = snippetJSON.getInt("position");
                    String videoId = snippetJSON.getJSONObject("resourceId").getString("videoId");

                    mPlaylistItemArray.add(new YTPlaylistItem(channelId, playlistId, playlistItemId, videoId,
                            playlistItemTitle, playlistItemDescription, playlistItemPublishedAt, position,
                            playlistItemThumbURL, playlistItemThumbWidth, playlistItemThumbHeight));
                    MLog.v(TAG, "PlaylistItem Info: id = " + playlistItemId +
                            ", channelId = " + channelId +
                            ", playlistId = " + playlistId +
                            ", videoId = " + videoId +
                            ", title = " + playlistItemTitle +
                            ", description = " + playlistItemDescription +
                            ", publishedAt = " + playlistItemPublishedAt +
                            ", position = " + position +
                            ", Thumbnail URL = " + playlistItemThumbURL +
                            ", Thumbnail Width = " + playlistItemThumbWidth +
                            ", Thumbnail Height = " + playlistItemThumbHeight);
                }
            } catch (JSONException e) {
                MLog.e(TAG, e.toString());
            }
        }

    }
}