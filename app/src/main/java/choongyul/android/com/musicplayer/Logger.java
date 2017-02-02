package choongyul.android.com.musicplayer;

import android.util.Log;


/**
 * @author choongyul.lee
 * @version 1.0
 * @since 2017
 */
public class Logger {
    public final static boolean DEBUG_MOD = true; // 디버그모드 이지만 잘 작동을 안해서 그냥 true를 쓴다. BuildConfig.DEBUG;


    /** 로그내용을 콘솔에 출력
     *
     * @param string
     * @param className
     */
    public static void print(String string, String className) {
        if(DEBUG_MOD) {
            Log.d(className, string);
        }
    }
}
