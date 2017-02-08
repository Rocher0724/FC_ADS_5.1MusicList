package choongyul.android.com.musicplayer;

import android.content.Context;
import android.widget.Toast;

/** Toast 함수가 사용되는 main에 들어가기 이상해서 따로 class를 만들어서 빼내었다.
 * Created by myPC on 2017-02-08.
 */

public class Message {

    public static void show(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
