package cn.lt.game.lib.util.image;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.SimpleTarget;

import cn.lt.game.R;


/**
 * Created by atian on 2016/10/17.
 */

public class ImageloaderUtil {
    private static RequestManager reqManager;

    public void init(Context context) {
        reqManager = Glide.with(context);
    }

    public static class ImageLoaderHolder {
        private final static ImageloaderUtil instance = new ImageloaderUtil();
    }

    public static ImageloaderUtil getInstance() {
        return ImageLoaderHolder.instance;
    }

    /***
     * 暂停加载图片
     *
     * @param context
     */
    public static void pauseImageLoader(Context context) {
        reqManager.pauseRequests();
    }

    /***
     * 暂停加载图片
     *
     * @param context
     */
    public static void destoryImageLoader(Context context) {
        reqManager.onDestroy();
    }

    /***
     * 回复图片加载
     *
     * @param context
     */
    public static void resumeImageLoader(Context context) {
        reqManager.resumeRequests();
    }

    /***
     * 加载普通图片
     *  @param context
     * @param url
     * @param view
     * @param isFromSubEntrance
     */
    public static void loadImage(Context context, String url, ImageView view, boolean isFromSubEntrance) {
        Glide.with(context).load(url).animate(R.anim.item_alpha_in).placeholder(isFromSubEntrance ? R.mipmap.entrance_120x120px : R.mipmap.img_default_80x80_round).into(view);
    }

    /**
     * 加载轮播图
     *
     * @param context
     * @param url
     * @param view
     */
    public static void loadBannerImage(Context context, String url, ImageView view) {
        if (context == null) {
            return;
        }
        if (context instanceof Activity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!((Activity) context).isDestroyed()) {
                Glide.with(context).load(url).animate(R.anim.item_alpha_in).placeholder(R.mipmap.img_default).into(view);
            }
        } else {
            Glide.with(context).load(url).animate(R.anim.item_alpha_in).placeholder(R.mipmap.img_default).into(view);
        }

//        if (context != null && !((Activity) context).isDestroyed()) {
//            Glide.with(context).load(url).animate(R.anim.item_alpha_in).placeholder(R.mipmap.img_default).into(view);
//        }
    }

    /***
     * 加载横幅长图
     *
     * @param context
     * @param url
     * @param view
     */
    public static void loadRecImage(Context context, String url, ImageView view) {
        Glide.with(context).load(url).animate(R.anim.item_alpha_in).placeholder(R.mipmap.photo).into(view);
    }

    /***
     * 需要回调的加载图片
     *  @param context
     * @param url
     * @param mTarget
     */
    public static SimpleTarget<GlideDrawable> loadImageCallBack(Context context, String url, SimpleTarget<GlideDrawable> mTarget) {
        return Glide.with(context).load(url).placeholder(R.mipmap.img_default).fitCenter().into(mTarget);
    }

    /***
     * 加载圆形图片
     *
     * @param context
     * @param url
     * @param view
     */
    public static void loadRoundImage(Context context, String url, ImageView view) {
        Glide.with(context).load(url).placeholder(R.mipmap.img_default_80x80_round).transform(new GlideRoundTransform(context)).crossFade().into(view);
    }

    /***
     * @param context
     * @param url
     * @param view    首页轮播图
     */
    public static void loadBigImage(Context context, String url, ImageView view) {
        Glide.with(context).load(url).thumbnail(0.1f).placeholder(R.mipmap.img_default).into(view);
    }

    /***
     * 加载用户头像
     *
     * @param context
     * @param url
     * @param view
     */
    public static void loadUserHead(Context context, String url, ImageView view) {
//        Glide.with(context).load(url).transform(new GlideCircleTransform(context)).into(view);
        if (TextUtils.isEmpty(url)) {
            view.setImageResource(R.mipmap.user_center_avatar);
        } else {
            Glide.with(context).load(url).transform(new GlideCircleTransform(context)).into(view);
        }
    }

    /***
     * 加载应用市场
     *
     * @param context
     * @param url
     * @param view
     */
    public static void loadLTLogo(Context context, String url, ImageView view) {
        if (context == null) return;
        Glide.with(context).load(url).crossFade().placeholder(R.mipmap.icon_default_back).into(view);
    }
}
