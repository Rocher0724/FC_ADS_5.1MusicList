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
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by myPC on 2017-02-01.
 */

public class DataLoader {

    // 데이터 로더 객체와 datas 를 싱글톤으로 구현하고
    // 데이터 로더를 통해서만 데이터를 불러와야한다.
    ////
//    private static DataLoader<?> dataLoader;
//    private ArrayList<T> datas;
//    private DAtaLoader(){}


    ////

    // 2. 데이터 컨텐츠 URI 정의
    private final static Uri URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    // 3. 데이터에서 가져올 데이터 컬럼명을 정의한다.
    // 데이터 컬럼명은 Content URI의 패키지에 들어있다.
    private final static String PROJ[] = {
            MediaStore.Audio.Media._ID
            ,MediaStore.Audio.Media.ALBUM_ID
            ,MediaStore.Audio.Media.TITLE
            ,MediaStore.Audio.Media.ARTIST    };


    //datas는 전역에서 사용되므로 static으로 빼고싶다. 이런경우 public 까지 주지는 않고 get함수를 public static을 선언해준다.
    // datas를 두개의 activity에서 공유하기 위해 static으로 변경
    private static List<Music> datas = new ArrayList<>();

    // static 변수인 data 를 체크해서 null이면 load를 실행
    public static List<Music> getDatas(Context context) {
        if (datas == null || datas.size() == 0) {
            load(context);
        }
        return datas;
    }

    // load는 외부에서 호출될일이 없고 get함수를 통해서만 접근된다.
    private static void load(Context context) {
        // 1. 주소록에 접근하기 위해 ContentResolver를 불러온다.
        ContentResolver resolver = context.getContentResolver();

        // 4. Content Resolver 로 불러온(쿼리한) 데이터를 커서에 담는다.
        // 데이터 URI : MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        Cursor cursor = resolver.query(URI, PROJ, null, null, null);

        if( cursor != null) {
            // 5. 커서에 넘어온 데이터가 있다면 반복문을 돌면서 datas에 담아준다.
            while ( cursor.moveToNext() ) {
                Music music = new Music();

                music.setId(getValue(cursor, PROJ[0]));
                music.setAlbum_id(getValue(cursor, PROJ[1]));
                music.setTitle(getValue(cursor, PROJ[2]));
                music.setArtist(getValue(cursor, PROJ[3]));

                music.album_image = getAlbumImageSimple(music.getAlbum_id());
                music.uri = getMusicUri(music.getId());

                datas.add(music);
            }
            // * 중요 : 사용 후 close를 호출하지 않으면 메모리 누수가 발생할 수 있다.
            cursor.close();
        }
    }

    private static String getValue(Cursor cursor, String columnName) {
        int idx = cursor.getColumnIndex(columnName);
        return cursor.getString(idx);
    }

    private static Uri getMusicUri(String music_id) {
        Uri content_uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        return Uri.withAppendedPath(content_uri, music_id);
    }

    // 가장 간단하게 앨범 이미지를 가져오는 방법.
    // 문제점 : 실제 앨범 데이터만 있어서 이미지를 불러오지 못하는 경우가 있다.
    private static Uri getAlbumImageSimple(String album_id) {
        return Uri.parse("content://media/external/audio/albumart/" + album_id);
    }

    //경로에 가서 이미지를 비트맵으로 바꿔서 가져오는 메소드
    @Deprecated
    private Bitmap getAlbumImageBitmap(Context context , String album_id) {
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
