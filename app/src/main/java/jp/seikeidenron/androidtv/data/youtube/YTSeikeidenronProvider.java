package jp.seikeidenron.androidtv.data.youtube;

import android.content.Context;

import java.util.ArrayList;

/**
 * Manages Youtube data of seikeidenron channel
 */
public class YTSeikeidenronProvider {

    private static final String TAG = YTSeikeidenronProvider.class.getSimpleName();

    public static final String YT_SEIKEIDENRON_CHANNEL_ID = "UCH0hL0DApXKRa2AqmBgA05g";
    public static final String YT_SEIKEIDENRON_PLAYLIST_ID = "PLLuMFcOhC919XdpH0x5mjz-wHhvPxz-nk";

    private static YTChannel sSeikeidenron = null;

    /**
     * Prepare Seikeidenron all channel data.
     * It may take time, must be done in background thread.
     * @return
     */
    public static YTChannel buildMedia(Context context) {
        if( sSeikeidenron == null) {
            sSeikeidenron = new YTChannel(YT_SEIKEIDENRON_CHANNEL_ID);
            sSeikeidenron.buildAll();

            /* Prepare Bitmap only for playlistItem */
            ArrayList<YTPlaylist> seikeidenronPlaylists = sSeikeidenron.getPlaylistArray();
            for(YTPlaylist playlist : seikeidenronPlaylists) {

                ArrayList<YTPlaylistItem> playlistItemArray = playlist.getPlaylistItemArray();
                for(YTPlaylistItem playlistItem : playlistItemArray) {
                    YTUtil.YTPlaylistThumbnail thumbnail = playlistItem.getThumbnail();
                    thumbnail.buildBitmap(context);
                }
            }
        }
        return sSeikeidenron;
    }
}