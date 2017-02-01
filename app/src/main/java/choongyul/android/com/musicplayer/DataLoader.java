package choongyul.android.com.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;

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


                datas.add(music);
            }
            // * 중요 : 사용 후 close를 호출하지 않으면 메모리 누수가 발생할 수 있다.
            cursor.close();
        }





    }

}
