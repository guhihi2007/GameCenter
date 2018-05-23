/**
 *
 */
package cn.lt.game.ui.app.sidebar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.AppIsInstalledUtil;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.view.ShareView;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

/**
 * @author zhaoqile
 * @Description “关于”页面
 * @date 2014-09-01
 */
public class AboutActivity extends BaseActivity {

    @ViewInject(R.id.about_logo)
    private ImageView logoIv;
    @ViewInject(R.id.share_wechatIv)
    private ImageView share_wechatIv;

    @ViewInject(R.id.share_pyqIv)
    private ImageView share_pyqIv;

    @ViewInject(R.id.share_sinaIv)
    private ImageView share_sinaIv;
    @ViewInject(R.id.tv_page_title)
    private TextView titileTv;
    @ViewInject(R.id.about_version)
    private TextView versionTv;

    @ViewInject(R.id.share_qqIv)
    private ImageView share_qqIv;

    @ViewInject(R.id.share_plusIv)
    private ImageView share_plusIv;

    private int clickAboutLogoCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ViewUtils.inject(this);
        initView();
    }

    private void initView() {
        titileTv.setText("关于天天游戏中心");
        if (!AppIsInstalledUtil.isInstalled(this, "com.tencent.mm")) {
            share_wechatIv.setAlpha(0.3f);
            share_pyqIv.setAlpha(0.3f);
            share_wechatIv.setEnabled(false);
            share_pyqIv.setEnabled(false);
        }

        if (!AppIsInstalledUtil.isInstalled(this, "com.tencent.mobileqq")) {
            share_qqIv.setAlpha(0.3f);
            share_qqIv.setEnabled(false);
        }

        if (!AppIsInstalledUtil.isInstalled(this, "com.sina.weibo")) {
            share_sinaIv.setAlpha(0.3f);
            share_sinaIv.setEnabled(false);
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        String version = "V" + AppUtils.getVersionName(this);
        versionTv.setText("版本：" + version);
    }

    @OnClick({R.id.share_sinaIv, R.id.share_qqIv, R.id.share_wechatIv, R.id.share_plusIv, R.id.share_pyqIv, R.id.about_logo, R.id.btn_page_back})
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.share_sinaIv:

                if (!NetUtils.isConnected(v.getContext())) {
                    ToastUtils.showToast(v.getContext(), "请检查网络");
                    return;
                }
                showShare(false, "SinaWeibo", true);

                break;

            case R.id.share_pyqIv:

                if (!NetUtils.isConnected(v.getContext())) {
                    ToastUtils.showToast(v.getContext(), "请检查网络");
                    return;
                }
                showShare(false, "WechatMoments", true);
                break;
            case R.id.share_wechatIv:
                if (!NetUtils.isConnected(v.getContext())) {
                    ToastUtils.showToast(v.getContext(), "请检查网络");
                    return;
                }
                showShare(false, "Wechat", true);

                break;

            case R.id.share_qqIv:
                if (!NetUtils.isConnected(v.getContext())) {
                    ToastUtils.showToast(v.getContext(), "请检查网络");
                    return;
                }
                showShare(false, "QQ", true);

                break;

            case R.id.share_plusIv:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "选择要分享的应用");
                intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.about_shared_string));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, "选择要分享的应用"));

                break;

            case R.id.about_logo:
                startInfoAppActivity();
                break;

            case R.id.btn_page_back:
                finish();
                break;

        }

    }


    private void startInfoAppActivity() {
        clickAboutLogoCount++;
        if (clickAboutLogoCount == 6) {
            startActivity(new Intent(AboutActivity.this, AppInfoBackDoorActivity.class));
            clickAboutLogoCount = 0;
        }

    }

    private void showShare(boolean silent, String platform, boolean captureView) {
        Context context = this;
        final OnekeyShare oks = new OnekeyShare();

        oks.setTitle("【推荐】天天游戏中心");
        oks.setText("大家都在这发现好玩的游戏，戳这加入我们呗");
        if (platform.contains("Wechat")) {
            oks.setUrl(ShareView.gameCenterDownloadUrl);
            oks.setTitleUrl(ShareView.gameCenterDownloadUrl);
        } else if (platform.contains("SinaWeibo")) {
            /* 没有安卓新浪微博客户端时，需要文字里面需要加上链接，不然分享成功链接后显示不出来
             * 新浪微博不需要设置setUrl，否则图片logo会显示不出来
             */
            oks.setText("天天游戏中心，天天要你好玩。快来下载吧。  " + ShareView.APK_DownloadUrl + "  ");
            oks.setUrl("");
        } else {
            oks.setUrl(ShareView.APK_DownloadUrl);
            oks.setTitleUrl(ShareView.APK_DownloadUrl);
        }


        oks.setLatitude(23.056081f);
        oks.setLongitude(113.385708f);
        oks.setSilent(silent);
        oks.setTheme(OnekeyShareTheme.CLASSIC);

        oks.setPlatform(platform);

        // 在自动授权时可以禁用SSO方式
        oks.disableSSOWhenAuthorize();


        // 去除注释，演示在九宫格设置自定义的图标
        Bitmap enableLogo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Bitmap disableLogo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        String label = getResources().getString(R.string.app_name);
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                // String text = "Customer Logo -- ShareSDK " +
                // ShareSDK.getSDKVersionName();
            }
        };
        oks.setImagePath(getLocalImageUrl(context));
        oks.setCustomerLogo(enableLogo, label, listener);

        oks.show(context);
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

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_PERSONAL_ABOUT);
    }

}
