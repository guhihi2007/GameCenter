package cn.lt.game.lib.netdata;

import android.text.TextUtils;
import android.util.Base64;

/**
 * Created by Administrator on 2015/12/17.
 */
public class NetDataEncoding {
    //TODO 需要传入key
    public static String encode(String str) {
        if (!TextUtils.isEmpty(str)) {
//        long time = System.currentTimeMillis();
            byte[] byteArr = str.getBytes();
            for (int i = 0; i < byteArr.length; i++) {
                byteArr[i] ^= 121;
            }
            String str1 = new String(Base64.encode(byteArr, Base64.NO_PADDING));
//        Log.e("base64","encode time:"+(System.currentTimeMillis() - time));
//        Log.e("base64","encode data:"+str1);
            return str1;
        } else {
            return str;
        }
    }

    //TODO 需要传入key
    public static String decode(String str) {
        if (!TextUtils.isEmpty(str)) {
//        long time = System.currentTimeMillis();
            byte[] byteArr = Base64.decode(str.getBytes(), Base64.NO_PADDING);
//        Log.e("base64","decodePre data:"+new String(byteArr));
            for (int i = 0; i < byteArr.length; i++) {
                byteArr[i] ^= 121;
            }
            String str1 = new String(byteArr);
//        Log.e("base64","decode time:"+(System.currentTimeMillis() - time));
            return str1;
        } else {
            return str;
        }
    }
}
