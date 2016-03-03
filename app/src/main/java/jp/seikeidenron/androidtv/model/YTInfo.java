package jp.seikeidenron.androidtv.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Youtube Info
 */
public class YTInfo extends ImageInfo implements Parcelable {

    private static final String TAG = YTInfo.class.getSimpleName();

    private String mVideoId;
    private String mPlaylistItemId;
    private String mThumbnailUrl;

    public String getVideoId() {
        return mVideoId;
    }

    public void setVideoId(String mVideoId) {
        this.mVideoId = mVideoId;
    }

    public String getPlaylistItemId() {
        return mPlaylistItemId;
    }

    public void setPlaylistItemId(String playlistItemId) {
        this.mPlaylistItemId = playlistItemId;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public YTInfo(int id, String title, String studio, Bitmap image,
                       String videoId, String playlistItemId, String url) {
        super(id, title, studio, image);
        mVideoId = videoId;
        mPlaylistItemId = playlistItemId;
        mThumbnailUrl   = url;
    }

    public YTInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mVideoId);
        dest.writeString(this.mPlaylistItemId);
        dest.writeString(this.mThumbnailUrl);
    }

    protected YTInfo(Parcel in) {
        super(in);
        this.mVideoId = in.readString();
        this.mPlaylistItemId = in.readString();
        this.mThumbnailUrl = in.readString();
    }

    public static final Creator<YTInfo> CREATOR = new Creator<YTInfo>() {
        public YTInfo createFromParcel(Parcel source) {
            return new YTInfo(source);
        }

        public YTInfo[] newArray(int size) {
            return new YTInfo[size];
        }
    };
}