package cn.lt.game.lib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.mob.MobSDK;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.lib.util.AppIsInstalledUtil;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.threadPool.ThreadPoolProxyFactory;
import cn.lt.game.ui.app.community.model.ShareBean;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

import static cn.lt.game.db.service.DownFileService.mContext;


/**
 * Created by zhengweijian on 15/8/19.
 */
public class ShareView extends LinearLayout implements View.OnClickListener {
    private static final int SUCCESS = 0;
    private static final int FAILED = 1;
    private static final int CANCEL = 2;
    private ShareBean shareBean;
    private TextView share_wechatIv;// 微信分享
    private TextView share_pyqIv;// 朋友圈分享
    private TextView share_sinaIv;// 新浪分享
    private TextView share_qqIv;// QQ分享
    private shareViewOnclick onclick;

    public static final String gameCenterDownloadUrl = "http://dwz.cn/HjWTK";
    public static final String APK_DownloadUrl = "http://www.ttigame.com/downloadApk ";// 末尾加空格是为了防止新浪微博@人时，链接会失效
    public static final String gameCenterHostPageUrl = "http://www.ltbl.cn/Game.html";
    private String weiboIconPath = "";


    public ShareView(Context context) {
        this(context, null);
    }

    public ShareView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShareView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
        initListener();
        MobSDK.init(context,"20668a9e9421a", "0460a35b5705e7e97275abfbb4f85405");
    }

    private void initListener() {

        share_pyqIv.setOnClickListener(this);
        share_sinaIv.setOnClickListener(this);
        share_qqIv.setOnClickListener(this);
        share_wechatIv.setOnClickListener(this);

    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_shareview, this);


        share_pyqIv = (TextView) findViewById(R.id.share_pyqIv);
        share_sinaIv = (TextView) findViewById(R.id.share_sinaIv);
        share_qqIv = (TextView) findViewById(R.id.share_qqIv);
        share_wechatIv = (TextView) findViewById(R.id.share_wechatIv);
        fitterTextSize();
        if (!AppIsInstalledUtil.isInstalled(getContext(), "com.tencent.mm")) {
            share_wechatIv.setAlpha(0.3f);
            share_pyqIv.setAlpha(0.3f);
            share_wechatIv.setEnabled(false);
            share_pyqIv.setEnabled(false);
        }

        if (!AppIsInstalledUtil.isInstalled(getContext(), "com.tencent.mobileqq")) {
            share_qqIv.setAlpha(0.3f);
            share_qqIv.setEnabled(false);
        }

        if (!AppIsInstalledUtil.isInstalled(getContext(), "com.sina.weibo")) {
            share_sinaIv.setAlpha(0.3f);
            share_sinaIv.setEnabled(false);
        }
    }

    /**
     * 根据屏幕尺寸设置字体大小
     */
    private void fitterTextSize() {
        int size = 0;

        if (MyApplication.screenSize < 4.5) {// 4.5寸以下
            size = 10;
        } else if (MyApplication.screenSize < 5) {// 5寸以下
            size = 12;
        } else if (MyApplication.screenSize < 5.5) {// 5.5寸以下
            size = 14;
        } else {// 5.5寸以上
            size = 15;
        }

        share_pyqIv.setTextSize(size);
        share_sinaIv.setTextSize(size);
        share_qqIv.setTextSize(size);
        share_wechatIv.setTextSize(size);
    }

    public ShareView setShareBean(ShareBean shareBean) {
        this.shareBean = shareBean;
        return this;
    }

    @Override
    public void onClick(final View view) {

        if (!NetUtils.isConnected(view.getContext())) {
            ToastUtils.showToast(view.getContext(), "请检查网络");
            return;
        }
        switch (view.getId()) {
            case R.id.share_pyqIv:
                showShare(false, "WechatMoments");
                if (onclick != null) {
                    onclick.shareOnClick(view);
                }
                break;
            case R.id.share_sinaIv:
                shareWeibo(view);
                break;
            case R.id.share_qqIv:
                showShare(false, "QQ");
                if (onclick != null) {
                    onclick.shareOnClick(view);
                }
                break;
            case R.id.share_wechatIv:
                showShare(false, "Wechat");
                if (onclick != null) {
                    onclick.shareOnClick(view);
                }
                break;

            default:
                break;
        }

    }


    private void shareWeibo(final View view) {
        if (shareBean.shareType == ShareBean.GAME) {
            // 受微博平台限制，图片需要用本地图片才能正常显示，所以只能下载后使用缓存作为分享
            ThreadPoolProxyFactory.getNormalThreadPoolProxy().execute(new Runnable() {
                @Override
                public void run() {
                    FutureTarget<File> future = Glide.with(mContext).load(shareBean.getGameIconUrl()).downloadOnly(144, 144);
                    try {
                        File cacheFile = future.get();
                        weiboIconPath = cacheFile.getAbsolutePath();

                        MyApplication.getMainThreadHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                showShare(false, "SinaWeibo");
                                if (onclick != null) {
                                    onclick.shareOnClick(view);
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        } else {

            showShare(false, "SinaWeibo");
            if (onclick != null) {
                onclick.shareOnClick(view);
            }
        }
    }

    private void showShare(boolean silent, String platform) {// 分享的方法调用OnekeyShare来封装和发送
        final OnekeyShare oks = new OnekeyShare();

        if (shareBean.getText().equals("&nbsp;<br>")) {
            shareBean.setText("");
        }

        oks.setTitleUrl(shareBean.getTitleurl());
        oks.setText(shareBean.getText());
        oks.setTitle(shareBean.getTitle());// 分享的标题
        oks.setUrl(shareBean.getTitleurl());

        if (platform.contains("Wechat") && shareBean.getTitleurl().contains(".apk")) {
            oks.setUrl(gameCenterDownloadUrl);
        }


        if (platform.contains("SinaWeibo")) {
            oks.setUrl("");
            oks.setText(shareBean.getText() + "  " + shareBean.getTitleurl() + "  ");
            // 末尾加空格是为了防止新浪微博@人时，链接会失效
        }
//
        /*朋友圈*/
        if (platform.contains("WechatMoments")) {
            String content = shareBean.getTitleurl();
            oks.setTitle(shareBean.getText());
            /*引导页链接～*/
            if (content.contains(".apk")) {
                oks.setUrl(gameCenterDownloadUrl);
            }

        }

        oks.setComment(getContext().getString(R.string.share));
        oks.setSite(getContext().getString(R.string.app_name));
        oks.setLatitude(23.056081f);
        oks.setLongitude(113.385708f);
        oks.setSilent(silent);
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        oks.setPlatform(platform);

        oks.disableSSOWhenAuthorize();

        if (shareBean.shareType == ShareBean.GAME) {
            if (platform.equals("SinaWeibo")) {
                oks.setImagePath(weiboIconPath);
            } else {
                oks.setImageUrl(shareBean.getGameIconUrl());// 图标URL
            }
        } else {
            Bitmap enableLogo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            String label = getResources().getString(R.string.app_name);

            oks.setImagePath(getLocalImageUrl(getContext()));
            oks.setCustomerLogo(enableLogo, label, null);
        }

        oks.show(getContext());
    }

    private String getLocalImageUrl(Context context) {
        File file = context.getCacheDir();
        File image = new File(file, "ic_launcher.png");
        if (!image.exists()) {
            InputStream in = null;
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                in = context.getAssets().open("ic_launcher.png");
                bis = new BufferedInputStream(in);
                int length = bis.available();
                byte[] bytes = new byte[length];
                bis.read(bytes);
                bos = new BufferedOutputStream(new FileOutputStream(image));
                bos.write(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                        in = null;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                if (bis != null) {
                    try {
                        bis.close();
                        bis = null;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                if (bos != null) {
                    try {
                        bos.close();
                        bos = null;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        return image.getAbsolutePath();
    }

    public interface shareViewOnclick {
        void shareOnClick(View view);
    }


    public void setOnclick(shareViewOnclick onclick) {
        this.onclick = onclick;
    }
}
