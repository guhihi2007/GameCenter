package cn.lt.game.lib.util.html;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.image.BitmapUtil;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.util.log.Logger;
import cn.lt.game.lib.util.net.NetUtils;

/**
 * Created by wenchao on 2015/8/25.
 * 网络图片加载器
 * 移动网络加载小图，wifi网络加载大图,
 */
public class RemoteImageGetter implements Html.ImageGetter {

    private TextView textView;
    private int screenWidth;
    private int screentHeight;

    public RemoteImageGetter(TextView textView) {
        this.textView = textView;
        screenWidth = Utils.getScreenWidth(textView.getContext());
        screentHeight = Utils.getScreenHeight(textView.getContext());
    }

    @Override
    public Drawable getDrawable(String source) {
        //设置默认图片
        final UrlDrawable urlDrawable   = new UrlDrawable();
        final Bitmap      defaultBitmap = BitmapFactory.decodeResource(textView.getResources(), R.mipmap.img_default_80x80_round);
        urlDrawable.bitmap = defaultBitmap;
        urlDrawable.setBounds(0, 0, defaultBitmap.getWidth(), defaultBitmap.getHeight());

        if (TextUtils.isEmpty(source)) {
            return urlDrawable;
        }

        //若为移动网络
        if (NetUtils.isMobileNet(textView.getContext())) {
            int index = source.lastIndexOf("/");
            StringBuilder sb = new StringBuilder(source);
            sb.insert(index, "/200_200");
            source = sb.toString();
        }
        loadBitmap(urlDrawable, source);


        return urlDrawable;
    }


    private void loadBitmap(final UrlDrawable urlDrawable, final String source) {




        Logger.i("开始加载图片"+source);


        ImageloaderUtil.loadImageCallBack(MyApplication.application, source, new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                Bitmap bitmap = BitmapUtil.drawable2Bitmap(resource.getCurrent());
                bitmap = BitmapUtil.compressBitmapByScreen(bitmap,screenWidth*4/5,screentHeight*4/5);
                urlDrawable.bitmap = bitmap;
                urlDrawable.setBounds(0, 0, urlDrawable.bitmap.getWidth(), urlDrawable.bitmap.getHeight());
                textView.invalidate();
                textView.setText(textView.getText());
            }
        });
    }
}


