package cn.lt.game.ui.app.requisite;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.bean.GameInfoBean;
import cn.lt.game.download.DownloadChecker;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.RedPointEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.install.InstallState;
import cn.lt.game.install.autoinstaller.AutoInstallerContext;
import cn.lt.game.lib.PreferencesUtils;
import cn.lt.game.lib.ScreenUtils;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.FromPageManager;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.PackageUtils;
import cn.lt.game.lib.util.PopWidowManageUtil;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.State;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsDataProductorImpl;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.statistics.pageunits.PageMultiUnitsReportManager;
import cn.lt.game.ui.app.index.IndexUtil;
import cn.lt.game.ui.app.requisite.adapter.GridAdapter;
import cn.lt.game.ui.app.requisite.adapter.GridAdapter.GameHolder;
import de.greenrobot.event.EventBus;

/**
 * 精选必玩 展示框；
 *
 * @author dxx
 */
public class RequisiteDialog extends Dialog implements android.view.View.OnClickListener, OnItemClickListener {

    private Context mContext;

    private GridView mGridView;

    private Button mDownloadBt;

    private TextView mNetworkEnv;

    private List<RequisiteItem> mDataList;

    private List<GameInfoBean> mBeans;

    private List<GameBaseDetail> downLoadList;
    private List<GameInfoBean> tempGame;

    public RequisiteDialog(Context context) {
        super(context, android.R.style.Theme);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//设置状态栏透明色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.dialog_requisite);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.setCanceledOnTouchOutside(false);
        initView();
        initGridView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        LogUtils.d("uuu", " dialog onWindowFocusChanged==>是否hasFocus-" + hasFocus);
        if (hasFocus) {
            try {
                MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PageMultiUnitsReportManager.buildBiWanEvent(tempGame, Constant.PAGE_INDEX_NECESSARY);
                    }
                }, 200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * 显示该界面；
     */
    public void show(String result) {
        LogUtils.d("uuu", "show dialog le");
        reportPageJump();
        PreferencesUtils.putLong(mContext, PopWidowManageUtil.LAST_SPREAD_SHOW_TIME, System.currentTimeMillis());//保存第一次弹窗推广弹出时间
        PreferencesUtils.putBoolean(mContext, Constant.SELECTION_PLAY_SHOWED, true);
        if (!parserJson(result)) {
            return;
        }
        show();
    }

    private void reportPageJump() {
        final StatisticsEventData event = StatisticsDataProductorImpl.produceStatisticsData(null, 0, 0, null, Constant.PAGE_INDEX_NECESSARY, ReportEvent.ACTION_PAGEJUMP, null, null, null, "");
        FromPageManager.pageJumpReport(event);
    }

    /**
     * 取消界面的显示；
     */
    public void hideView() {
        if (this.isShowing()) {
            this.cancel();
        }
    }

    /**
     * 解析数据；
     */
    private boolean parserJson(String result) {
        try {
            LogUtils.i("uuu", ",parserJson: ");
            if (result.toCharArray()[0] == '[') {
                mBeans = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    GameInfoBean game = new Gson().fromJson(jsonObj.opt("data").toString(), GameInfoBean.class);
                    mBeans.add(game);
                }
            }
            if (mBeans != null && mBeans.size() > 0) {
                tempGame = mBeans;
//                PageMultiUnitsReportManager.buildBiWanEvent(tempGame,Constant.PAGE_INDEX_NECESSARY);
                convertToRequisiteItem(tempGame);
                return true;
            }
        } catch (Exception e) {
            LogUtils.i("GOOD", e.getMessage() + ",Requisite: " + result);
            mBeans = null;
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("deprecated")
    private void setButtonState() {
        List<GameBaseDetail> list = getDownloadList();
        if (list == null || list.size() == 0) {
            mDownloadBt.setBackgroundResource(R.drawable.onekey_download_unselector);
            mDownloadBt.setTextColor(Color.parseColor("#ffffff"));
            mDownloadBt.setText(R.string.one_key_requisite_download);
        } else {
            mDownloadBt.setBackgroundResource(R.drawable.deep_get_gift_button_selector);

            float totalLength = 0;
            for (GameBaseDetail detail : list) {
                totalLength += detail.getPkgSize();
            }

            String text = getContext().getString(R.string.one_key_requisite_download_detail, list.size(), totalLength / 1024 / 1024);
            SpannableString spanText = new SpannableString(text);
            spanText.setSpan(new RelativeSizeSpan(0.8f), text.lastIndexOf("（"), spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mDownloadBt.setText(spanText);
        }

        boolean isWifiNet = NetUtils.isWifiNet(getContext());
        String reminders = getContext().getString(isWifiNet ? R.string.network_env_wifi : R.string.network_env_cellular);

        Drawable drawable = getContext().getResources().getDrawable(isWifiNet ? R.mipmap.wifi_indication : R.mipmap.ng_indication);
        drawable.setBounds(0, 0, (int) ScreenUtils.dpToPx(getContext(), 11), (int) ScreenUtils.dpToPx(getContext(), 11));

        SpannableString spannable = new SpannableString(reminders);
        //要让图片替代指定的文字就要用ImageSpan
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        spannable.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        mNetworkEnv.setText(spannable);
    }

    /**
     * 转换数据；
     */
    private void convertToRequisiteItem(List<GameInfoBean> games) {
        if (games != null) {
            if (mDataList == null) {
                mDataList = new ArrayList<>();
            }
            mDataList.clear();
            for (int i = 0; i < games.size(); i++) {
                GameInfoBean tempGame = games.get(i);
                boolean[] appStatus = AppUtils.appStatus(tempGame.getPackage_name(), tempGame.getVersion_code());

                RequisiteItem requisiteItem = new RequisiteItem(games.get(i));
                requisiteItem.setChecked(true);
                if (appStatus[0]) {
                    if (appStatus[1]) {
                        // FIXME 可升级
                        requisiteItem.setCanUpgrade(true);
                    } else {
                        // 已安装且无需更新
                        requisiteItem.setChecked(false);
                        requisiteItem.setEnable(false);
                    }
                }
                mDataList.add(requisiteItem);
            }
        }
    }

    /**
     * 获取view对象；初始化设置监听器；
     */
    private void initView() {
        mGridView = (GridView) findViewById(R.id.gv_content_requisite);
        View mCancelView = findViewById(R.id.bt_cancel_requisite);
        mDownloadBt = (Button) findViewById(R.id.bt_download_requisite);
        mNetworkEnv = (TextView) findViewById(R.id.network_env);
        mCancelView.setOnClickListener(this);
        mDownloadBt.setOnClickListener(this);
    }

    /**
     * 初始话GridView；
     */
    private void initGridView() {
        if (mDataList == null || mDataList.size() == 0) {
            hideView();
        }
        GridAdapter mAdapter = new GridAdapter(mContext, mDataList);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);

        //
        mGridView.post(new Runnable() {
            @Override
            public void run() {
                setButtonState();
            }
        });
    }

    @Override
    public void onClick(View v) {
        boolean isColse = true;
        int id = v.getId();
        switch (id) {
            case R.id.bt_cancel_requisite:
                break;

            case R.id.bt_download_requisite:
                isColse = downloadGame();
                break;
        }
        if (isColse) {
            this.hideView();
        } else {
            ToastUtils.showToast(mContext, "还没有选中任何游戏哦！");
        }
    }

    /**
     * 执行已将下载；
     */
    private boolean downloadGame() {
        try {
            final List<GameBaseDetail> downLoadList = getDownloadList();
            if (downLoadList == null || downLoadList.size() == 0) {
                return false;
            }
            DownloadChecker.getInstance().check(mContext, new DownloadChecker.Executor() {
                @Override
                public void run() {
                    // 显示小红点
                    EventBus.getDefault().post(new RedPointEvent(true));
                    MyApplication.castFrom(mContext).setNewGameDownload(true);
                    // 提示用户开启自动装辅助功能，并且在用户操作后，启动下载
                    if (!MyApplication.castFrom(mContext).getRootInstallIsChecked() && !PackageUtils.isSystemApplication(mContext)) {
                        AutoInstallerContext.getInstance().promptUserOpen((Activity) mContext);
                    }

                    for (int i = 0; i < mDataList.size(); i++) {
                        RequisiteItem item = mDataList.get(i);
                        if (item.isChecked) {
                            GameBaseDetail detail = item.getGameDetail();

                            if (item.canUpgrade()) {
                                // 精选必玩推送的可升级应用需设置状态 上报
                                detail.setState(InstallState.upgrade);
                            }
                            download(detail, false, Constant.MODE_ONEKEY, i + 1);
                        }
                    }

                }

                @Override
                public void reportOrderWifiClick() {

                }
            }, new DownloadChecker.Executor() {
                @Override
                public void run() {
                    for (GameBaseDetail game : downLoadList) {
                        pause(game);
                    }
                }

                @Override
                public void reportOrderWifiClick() {

                }
            }, new DownloadChecker.Executor() {// 预约wifi下载
                @Override
                public void run() {
                    // 显示小红点
                    EventBus.getDefault().post(new RedPointEvent(true));
                    MyApplication.castFrom(mContext).setNewGameDownload(true);
                    // 提示用户开启自动装辅助功能，并且在用户操作后，启动下载
                    if (!MyApplication.castFrom(mContext).getRootInstallIsChecked() && !PackageUtils.isSystemApplication(mContext)) {
                        AutoInstallerContext.getInstance().promptUserOpen((Activity) mContext);
                    }

//                    for (GameBaseDetail game : downLoadList) {
//                        download(game, true, downLoadList.size() > 1 ? Constant.MODE_ONEKEY : Constant.MODE_SINGLE);
//                    }
                    for (int i = 0; i < downLoadList.size(); i++) {
                        download(downLoadList.get(i), true, downLoadList.size() > 1 ? Constant.MODE_ONEKEY : Constant.MODE_SINGLE, i + 1);
                    }
                }

                @Override
                public void reportOrderWifiClick() {
                    for (GameBaseDetail game : downLoadList) {
                        DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, "null", 0, null, 0, "" + game.getId(), null, "manual", "orderWifiDownload", game.getPkgName(),""));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;

    }


    private void pause(GameBaseDetail game) {
        FileDownloaders.stopDownload(game.getId());
        State.updateState(game, DownloadState.downloadPause);
        FileDownloaders.downloadNext();
    }


    /**
     * 执行下载；
     */
    private void download(final GameBaseDetail game, boolean isOrderWifiDownload, String download_mode, int subPos) {
        StatisticsEventData eventData = new StatisticsEventData();
        eventData.setSubPos(subPos);
        if (isOrderWifiDownload) {
            Utils.gameDownByOrderWifi(mContext, game, Constant.PAGE_INDEX_NECESSARY, true, download_mode, Constant.DOWNLOAD_TYPE_NORMAL, eventData);
            return;
        }

        if (FileDownloaders.couldDownload(mContext)) {
            Utils.gameDown(mContext, game, Constant.PAGE_INDEX_NECESSARY, true, download_mode, Constant.DOWNLOAD_TYPE_NORMAL, eventData);
        } else {
            ToastUtils.showToast(mContext, R.string.network_fail);
        }
    }

    /**
     * 获取需要下载的游戏；
     */
    private List<GameBaseDetail> getDownloadList() {
        if (mDataList != null) {
            if (downLoadList == null) {
                downLoadList = new ArrayList<>();
            } else {
                downLoadList.clear();
            }
            for (int i = 0; i < mDataList.size(); i++) {
                RequisiteItem item = mDataList.get(i);
                if (item.isChecked) {
                    downLoadList.add(item.getGameDetail());
                }
            }
        }
        return downLoadList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GameHolder holder = (GameHolder) view.getTag();
        holder.view.switchCheckView();
        setButtonState();
    }

    public class RequisiteItem {

        private GameInfoBean gameInfoBean;
        private boolean isChecked;
        private boolean enable = true;
        private boolean canUpgrade;

        boolean canUpgrade() {
            return canUpgrade;
        }

        void setCanUpgrade(boolean canUpgrade) {
            this.canUpgrade = canUpgrade;
        }

        RequisiteItem(GameInfoBean game) {
            this.gameInfoBean = game;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public GameInfoBean getGameInfoBean() {
            return gameInfoBean;
        }

        public void setChecked(boolean isChecked) {
            this.isChecked = isChecked;
        }

        GameBaseDetail getGameDetail() {
            return IndexUtil.jsonGameToDownloadGame(gameInfoBean);
        }

        void setEnable(boolean enable) {
            this.enable = enable;
        }

        public boolean isEnable() {
            return this.enable;
        }
    }
}
