package jp.seikeidenron.androidtv.data.youtube;

/**
 * YouTube Data API v3 PlaylistItem data
 */
public class YTPlaylistItem {

    private static final String TAG = YTPlaylistItem.class.getSimpleName();

    private String mChannelId;  // Parent channelId  of this playlistItem
    private String mPlaylistId; // Parent playlistId of this playlistItem
    private String mPlaylistItemId; // The id of this playlistItem
    private String mVideoId;    // video id of this playlistItem
    private String mTitle;
    private String mDescription;
    private String mPublishedAt;
    private int    mPosition;      // Position of this playlistItem inside playlist
    private YTUtil.YTPlaylistThumbnail mThumbnail;

    public YTPlaylistItem(String channelId, String playlistId, String playlistItemId, String videoId,
                          String title, String description, String publishedAt, int position,
                          String thumbUrl, int thumbWidth, int thumbHeight) {
        mChannelId  = channelId;
        mPlaylistId = playlistId;
        mPlaylistItemId = playlistItemId;
        mVideoId = videoId;
        mTitle = title;
        mDescription = description;
        mPublishedAt = publishedAt;
        mPosition = position;
        mThumbnail = new YTUtil.YTPlaylistThumbnail(thumbUrl, thumbWidth, thumbHeight);
    }

    /*--- Getter ---*/
    public String getChannelId() {
        return mChannelId;
    }

    public String getPlaylistId() {
        return mPlaylistId;
    }

    public String getPlaylistItemId() {
        return mPlaylistItemId;
    }

    public String getVideoId() {
        return mVideoId;
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

    public int getPosition() {
        return mPosition;
    }

    public YTUtil.YTPlaylistThumbnail getThumbnail() {
        return mThumbnail;
    }
}