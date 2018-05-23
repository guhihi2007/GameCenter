package cn.lt.game.ui.app;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.bean.FloatAdsBean;
import cn.lt.game.download.DownloadChecker;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.RedPointEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.install.autoinstaller.AutoInstallerContext;
import cn.lt.game.lib.PreferencesUtils;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.FromPageManager;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.PackageUtils;
import cn.lt.game.lib.util.PopWidowManageUtil;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.PageDetail;
import cn.lt.game.model.PageMap;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.jump.PageJumper;
import de.greenrobot.event.EventBus;

/**
 * Created by chon on 2017/6/8.
 * What? How? Why?
 */

class FloatAdsDialog extends Dialog {

    public static final int TYPE_DOWNLOAD_ONLY = 1;
    private final int TYPE_JUMP_ONLY = 2;
    private final int TYPE_DOWNLOAD_AND_JUMP = 3;

    private FloatAdsBean mFloatAdsBean;

    private FloatAdsDialog(@NonNull Context context) {
        super(context, android.R.style.Theme);
    }

    FloatAdsDialog(@NonNull Context context, FloatAdsBean floatAdsBean) {
        this(context);
        this.mFloatAdsBean = floatAdsBean;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//设置状态栏透明色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.dialog_float_ads);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        init();
    }

    private void init() {
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (mFloatAdsBean != null) {
            final ImageView mFloatImageView = (ImageView) findViewById(R.id.iv_ads);
            Glide.with(MyApplication.application).load(mFloatAdsBean.ads_icon).animate(R.anim.item_alpha_in).into(mFloatImageView);
            // 广告展示上报
            DCStat.adsSpreadEvent("adPresent", Constant.PAGE_FLOAT, "", mFloatAdsBean.id);


            mFloatImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 广告点击上报
                    DCStat.adsSpreadEvent("adClicked", Constant.PAGE_FLOAT, "", mFloatAdsBean.id);
                    dismiss();

                    // 点击跳转类型
                    int clickType = mFloatAdsBean.click_type;
                    if (TYPE_DOWNLOAD_ONLY == clickType) {
                        executeDownload(Constant.PAGE_FLOAT);
                    } else if (TYPE_JUMP_ONLY == clickType || TYPE_DOWNLOAD_AND_JUMP == clickType) {
                        FromPageManager.setLastPage(Constant.PAGE_FLOAT);
                        FromPageManager.setLastPageId("", mFloatAdsBean.id);

                        // 跳转 (1. 跳转的页面 2.页面需要的数据)
                        // 游戏详情 / 专题详情 /专题列表/ 常规活动         / 礼包中心/ 礼包详情 / H5
                        // game    / topic   / YM-ZT / routine_activity / YM-LB  / gift    / h5
                        String jumpType = mFloatAdsBean.jump_type;
                        PageDetail pageDetail = PageMap.instance().getPageDetail(jumpType);
                        if (pageDetail != null) {
                            if ("game".equals(jumpType) || "topic".equals(jumpType) || "gift".equals(jumpType)) {
                                pageDetail.value = mFloatAdsBean.resource_id;
                            } else if ("h5".equals(jumpType) || "hot_tab".equals(jumpType) || "hot_detail".equals(jumpType)) {
                                pageDetail.value = mFloatAdsBean.link;
                            }

                            PageJumper pageJumper = new PageJumper();
                            pageJumper.jump(pageDetail, getContext());
                        }

                        if (TYPE_DOWNLOAD_AND_JUMP == clickType) {
                            // TODO 比对版本是否开启下载
                            boolean[] array = AppUtils.appStatus(mFloatAdsBean.package_name, mFloatAdsBean.version_code);
                            if (!array[0] || array[1]) {
                                // 未安装或者可升级才下载
                                // 延迟为了在移动网络或者内存不足场景需要在落地页弹窗
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        executeDownload(Constant.PAGE_GAME_DETAIL);
                                    }
                                }, 800);
                            }
                        }
                    }

                }
            });
        }

    }

    public void showDialog() {
        boolean needShowFloatAds = PopWidowManageUtil.needShowFloatAds(this.getContext());

        PageDetail pageDetail = PageMap.instance().getPageDetail(mFloatAdsBean.jump_type);
        boolean canShow = pageDetail != null;
        LogUtils.e("nohc","showDialog: " + canShow + "\t" + needShowFloatAds + "\t");

        if (canShow && needShowFloatAds && !isShowing()) {
            super.show();
            PreferencesUtils.putLong(this.getContext(), PopWidowManageUtil.LAST_FLOAT_SHOW_TIME, System.currentTimeMillis());
            PreferencesUtils.putBoolean(this.getContext(), Constant.FLOAT_ADS_SHOWED, true);
        }
    }

    private void executeDownload(String page) {
        int gameID = Integer.parseInt(mFloatAdsBean.resource_id);
        GameBaseDetail gameBaseDetail = FileDownloaders.getDownFileInfoById(gameID);
        if (gameBaseDetail == null) {
            gameBaseDetail = new GameBaseDetail();
            gameBaseDetail.setId(gameID);
            gameBaseDetail.setDownUrl(mFloatAdsBean.download_url);
            gameBaseDetail.setPkgName(mFloatAdsBean.package_name);
            gameBaseDetail.setLogoUrl(mFloatAdsBean.icon_url);
            gameBaseDetail.setName(!TextUtils.isEmpty(mFloatAdsBean.alias) ? mFloatAdsBean.alias : mFloatAdsBean.name);
            gameBaseDetail.setMd5(mFloatAdsBean.package_md5);
            gameBaseDetail.setPkgSize(mFloatAdsBean.package_size);
            gameBaseDetail.setReview(mFloatAdsBean.reviews);
            gameBaseDetail.setDownloadCnt(mFloatAdsBean.download_count);
        }

        final GameBaseDetail game = gameBaseDetail;
        final StatisticsEventData eventData = new StatisticsEventData();

        eventData.setId(mFloatAdsBean.resource_id);
        eventData.setFrom_page(Constant.PAGE_FLOAT);
        eventData.setPage(page);

        DownloadChecker.getInstance().check(getContext(), new DownloadChecker.Executor() {
            @Override
            public void run() {
                // 显示小红点
                EventBus.getDefault().post(new RedPointEvent(true));
                MyApplication.castFrom(getContext()).setNewGameDownload(true);
                // 提示用户开启自动装辅助功能，并且在用户操作后，启动下载
                if (!MyApplication.castFrom(getContext()).getRootInstallIsChecked() && !PackageUtils.isSystemApplication(getContext())) {
                    AutoInstallerContext.getInstance().promptUserOpen(getOwnerActivity());
                }

                String name = !TextUtils.isEmpty(mFloatAdsBean.alias) ? mFloatAdsBean.alias : mFloatAdsBean.name;

                if (Constant.PAGE_FLOAT.equals(eventData.getPage())) {
                    ToastUtils.showToast(getContext(), name + " 开始下载了");
                }
                Utils.gameDown(MyApplication.application, game, Constant.PAGE_FLOAT, true, "single", "manual", eventData);

            }

            @Override
            public void reportOrderWifiClick() {

            }
        }, new DownloadChecker.Executor() {
            @Override
            public void run() {
            }

            @Override
            public void reportOrderWifiClick() {

            }
        }, new DownloadChecker.Executor() {// 预约wifi下载
            @Override
            public void run() {
                // 显示小红点
                EventBus.getDefault().post(new RedPointEvent(true));
                MyApplication.castFrom(getContext()).setNewGameDownload(true);
                // 提示用户开启自动装辅助功能，并且在用户操作后，启动下载
                if (!MyApplication.castFrom(getContext()).getRootInstallIsChecked() && !PackageUtils.isSystemApplication(getContext())) {
                    AutoInstallerContext.getInstance().promptUserOpen(getOwnerActivity());
                }

                Utils.gameDownByOrderWifi(getContext(), game, Constant.PAGE_FLOAT, true, "single", "manual", eventData);
            }

            @Override
            public void reportOrderWifiClick() {
                DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, "null",
                        0, null, 0, "" + mFloatAdsBean.resource_id, null, "manual", "orderWifiDownload", mFloatAdsBean.package_name,""));
            }
        });
    }
}
