package choongyul.android.com.musicplayer;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by myPC on 2017-02-01.
 */

public class Music {
    private String id;
    private String album_id;
    private String artist;
    private String title;
    Uri album_image;
    Uri uri;
    private Bitmap bitmap_image;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
