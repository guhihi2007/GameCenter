package cn.lt.game.update;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.lt.game.application.MyApplication;
import cn.lt.game.bean.VersionInfoBean;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.AppUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.redpoints.RedPointsManager;
import cn.lt.game.lib.util.file.FileUtil;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.threadPool.ThreadPoolProxyFactory;
import cn.lt.game.ui.app.sidebar.UpdateInfo;

/***
 * Created by Administrator on 2015/12/18.
 */
public class VersionCheckManger {
    //    mode=self 手动 auto 自动
    private static final String MODE_TYPE = "mode";
    public static final String MODE_SELF = "self";
    public static final String MODE_AUTO = "auto";
    private Context mContext;
    private boolean needShowRedPointAtSearchBar;

    private VersionCheckManger() {
    }


    public static VersionCheckManger getInstance() {
        return VersionCheckMangerHolder.mInstance;
    }

    public boolean isNeedShowRedPointAtSearchBar() {
        return needShowRedPointAtSearchBar;
    }

    public void setNeedShowRedPointAtSearchBar(boolean needShowRedPointAtSearchBar) {
        this.needShowRedPointAtSearchBar = needShowRedPointAtSearchBar;
//        EventBus.getDefault().post(new PlatUpdatePrompteType(this.needShowRedPointAtSearchBar));
    }

    public void init(Context context) {
        mContext = context;
    }

    public void checkVerison(final VersionCheckCallback callback, final String modeType) {
        LogUtils.d(LogTAG.HTAG, "开始检测新版本");
        // 网络请求数据；
        ThreadPoolProxyFactory.getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<>();
                params.put(MODE_TYPE, modeType);
                params.put("version", AppUtils.getVersionName(mContext));
                params.put("channel", AppUtils.getChannel(mContext));
                String uri;
                if (MODE_AUTO.equals(modeType)) {
                    uri = Uri2.CLIENT_UPDATE_URI;
                } else {
                    uri = Uri2.CLIENT_UPDATE_URI_MANUAL;
                }
                Net.instance().executeGet(Host.HostType.GCENTER_HOST, uri, params, new WebCallBackToObject<UIModuleList>() {
                    VersionCheckCallback.Result result = null;

                    /**
                     * 网络请求出错时调用
                     *
                     * @param statusCode 异常编号
                     * @param error      异常信息
                     */
                    @Override
                    public void onFailure(int statusCode, Throwable error) {
                        result = VersionCheckCallback.Result.fail;
                        LogUtils.d(LogTAG.HTAG, "检测无新版本onFailure");
                        callback.callback(result);  //请求结果通知观察者
                        RedPointsManager.getInstance().redPointsBean.setPlatUpdate(false);
                    }

                    @Override
                    protected void handle(UIModuleList list) {
                        RedPointsManager.getInstance().redPointsBean.setPlatUpdate(false);
                        if (list != null && list.size() > 0) {
                            UIModule module = (UIModule) list.get(0);
                            VersionInfoBean info = (VersionInfoBean) module.getData();
                            if (info != null) {
                                LogUtils.d(LogTAG.HTAG, "检测有新版本" + info.toString());
                                long updateCode = -1;
                                try {
                                    updateCode = Long.parseLong(info.getVersion_code());
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                if (updateCode > AppUtils.getVersionCode(mContext)) {
                                    LogUtils.d(LogTAG.HTAG, "检测有新版本并大于当前版本");
                                    result = setHaveVersion(info);
                                    long lastDownloadVersion = PlatUpdateManager.getLastDownloadVersion(mContext);
                                    if (lastDownloadVersion == 0 || lastDownloadVersion != updateCode) {
                                        boolean delResult = FileUtil.deleteFile(PlatUpdatePathManger.getDownloadPath(mContext));
                                        LogUtils.d(LogTAG.HTAG, "删除客户端升级文件==>" + delResult + "");
                                    }
                                    PlatUpdateManager.saveLastDownloadVersion(mContext, updateCode);
                                    RedPointsManager.getInstance().redPointsBean.setPlatUpdate(true);
                                } else {
                                    LogUtils.d(LogTAG.HTAG, "检测到的新版本并不大于当前版本");
                                    result = setNoneVersion();
                                }


                            } else {
                                LogUtils.d(LogTAG.HTAG, "检测无新版本");
                                result = setNoneVersion();
                            }
                        } else {
                            LogUtils.d(LogTAG.HTAG, "检测无新版本");
                            result = setNoneVersion();
                        }
                        callback.callback(result);  //请求结果通知观察者
                    }
                });
            }
        });

    }


    private MyApplication setUpdateInfoToApplication(String version) {
        MyApplication application = (MyApplication) mContext.getApplicationContext();
        application.setUpdateInfoToApplication();
        return application;
    }

    private VersionCheckCallback.Result setHaveVersion(VersionInfoBean info) {
        VersionCheckCallback.Result result;
        UpdateInfo.setVersion(info.getVersion_name());
        UpdateInfo.setVersion_code(Integer.valueOf(info.getVersion_code()));
        UpdateInfo.setDownload_link(info.getDownload_link());
        UpdateInfo.setFeature(info.getChangelog());
        UpdateInfo.setIs_force(info.isForce());
        UpdateInfo.setCreated_at(info.getCreated_at());
        UpdateInfo.setPackage_md5(info.getPackage_md5());
        UpdateInfo.setPackage_size(info.getPackage_size());
        result = VersionCheckCallback.Result.have;
        setUpdateInfoToApplication(UpdateInfo.getVersion());
        if (info.isForce()) {
            PlatUpdateManager.savePlatUpdateMode(mContext, PlatUpdateMode.force);
        }
        PlatUpdateManager.saveTargetVersionCode(mContext, info.getVersion_name());
        return result;
    }

    private VersionCheckCallback.Result setNoneVersion() {
        VersionCheckCallback.Result result;
        UpdateInfo.setVersion(null);
        UpdateInfo.setVersion_code(0);
        UpdateInfo.setDownload_link(null);
        UpdateInfo.setFeature(null);
        UpdateInfo.setIs_force(false);
        UpdateInfo.setCreated_at(null);
        UpdateInfo.setPackage_md5(null);
        UpdateInfo.setPackage_size(0);
        PlatUpdateManager.setShowRedPoint(mContext, false);
        setNeedShowRedPointAtSearchBar(false);
        result = VersionCheckCallback.Result.none;
        return result;
    }

    //观察者模式，以接口方式回调给所有观察者
    public interface VersionCheckCallback {
        /**
         * 此方法为版本请求完成后用来通知调用方的回调；
         * 收到回调之后请移除此回调；
         *
         * @param result
         */
        void callback(Result result);

        enum Result {
            /**
             * 有新版本；
             */
            have, /**
             * 无更新；
             */
            none, /**
             * 请求失败；
             */
            fail
        }
    }

    private static class VersionCheckMangerHolder {
        private static VersionCheckManger mInstance = new VersionCheckManger();
    }

    /*public static class PlatUpdatePrompteType {
        *//**
         * 是否需要显示小红点；
         *//*
        public boolean show;

        public PlatUpdatePrompteType(boolean flag) {
            this.show = flag;
        }
    }*/

}
