package choongyul.android.com.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by myPC on 2017-02-01.
 */

public class DataLoader {
    private ArrayList<Music> datas = new ArrayList<>();
    private Context context;

    public DataLoader(Context context) {
        this.context = context;
        load();
    }

    public ArrayList<Music> getDatas() {
        return datas;
    }

    public void load() {
        // 1. 주소록에 접근하기 위해 ContentResolver를 불러온다.
        ContentResolver resolver = context.getContentResolver();

        // 2. 데이터 컨텐츠 URI 정의
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        // 3. 데이터에서 가져올 데이터 컬럼명을 정의한다.
        // 데이터 컬럼명은 Content URI의 패키지에 들어있다.
        String projections[] = {
                MediaStore.Audio.Media._ID
                ,MediaStore.Audio.Media.ALBUM_ID
                ,MediaStore.Audio.Media.TITLE
                ,MediaStore.Audio.Media.ARTIST
        };

        // 4. Content Resolver 로 불러온(쿼리한) 데이터를 커서에 담는다.
        // 데이터 URI : MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        Cursor cursor = resolver.query(uri, projections, null, null, null);

        if( cursor != null) {
            // 5. 커서에 넘어온 데이터가 있다면 반복문을 돌면서 datas에 담아준다.
            while ( cursor.moveToNext() ) {
                Music music = new Music();

                int idx = cursor.getColumnIndex(projections[0]);
                music.setId(cursor.getString(idx));
                idx = cursor.getColumnIndex(projections[1]);
                music.setAlbum_id(cursor.getString(idx));
                idx = cursor.getColumnIndex(projections[2]);
                music.setTitle(cursor.getString(idx));
                idx = cursor.getColumnIndex(projections[3]);
                music.setArtist(cursor.getString(idx));

                music.album_image = getAlbumImageSimple(music.album_id);

                //glide 사용으로 인해 주석처리.
                // bitmap 사용방법 해보려고 써봤었음.
//                music.bitmap_image = getAlbumImageBitmap(music.album_id);
                datas.add(music);
            }
            // * 중요 : 사용 후 close를 호출하지 않으면 메모리 누수가 발생할 수 있다.
            cursor.close();
        }
    }

    // 가장 간단하게 앨범 이미지를 가져오는 방법.
    // 문제점 : 실제 앨범 데이터만 있어서 이미지를 불러오지 못하는 경우가 있다.
    private Uri getAlbumImageSimple(String album_id) {
        return Uri.parse("content://media/external/audio/albumart/" + album_id);
    }

    //경로에 가서 이미지를 비트맵으로 바꿔서 가져오는 메소드
    @Deprecated
    private Bitmap getAlbumImageBitmap(String album_id) {
        // 1. 앨범 아이디로 Uri 생성
        Uri uri = getAlbumImageSimple(album_id);
        // 2. 컨텐트 리졸버 가져오기
        ContentResolver resolver = context.getContentResolver();
        // 3. 리졸버에서 스트림 열기
        try {
            InputStream is = resolver.openInputStream(uri);
            // 4. Bitmap Factory를 통해 이미지 데이터를 가져온다.
            Bitmap image = BitmapFactory.decodeStream(is);
            // 5. 가져온 이미지를 리턴한다.
            return image;
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
