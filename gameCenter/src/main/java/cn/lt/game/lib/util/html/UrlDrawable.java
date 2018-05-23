package cn.lt.game.lib.util.html;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by wenchao on 2015/8/25.
 */
public class UrlDrawable extends BitmapDrawable{
    protected Bitmap bitmap;


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(bitmap!=null){
            canvas.drawBitmap(bitmap,0,0,getPaint());
        }
    }
}
