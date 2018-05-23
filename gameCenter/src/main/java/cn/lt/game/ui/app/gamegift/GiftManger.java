package cn.lt.game.ui.app.gamegift;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.lt.game.db.service.DownFileService;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.global.Constant;
import cn.lt.game.install.ApkInstallManger;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.util.AppIsInstalledUtil;
import cn.lt.game.lib.util.ClipBoardManagerUtil;
import cn.lt.game.lib.util.OpenAppUtil;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.ToastDialog;
import cn.lt.game.lib.widget.ToastDialog.ConFirmBTCallBack;
import cn.lt.game.lib.widget.ToastDialog.StateEnum;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.gamegift.exception.NullGameInfoException;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;
import de.greenrobot.event.EventBus;

public class GiftManger implements UserInfoUpdateListening {

    private final static String TAG = "GiftManger";
    private static final String UNLOGIN = "请先登录，登录后礼包可保存在您的个人账号，方便查看使用";
    private Context mContext;
    private GameBaseDetail mGame;
    private GiftDomainDetail mGift;
    private State mSate;
    private UserBaseInfo mUserInfo;
    private GetGiftResponseListener mGetGiftResponeseListener;
    private String pageName;

    public GiftManger(Context context, GiftDomainDetail gift) {
        this.mContext = context;
        this.mGift = gift;
        mGame = giftGame2GameDetail(gift.getGame(), mContext);
        if (mGame == null) {
            throw new NullGameInfoException("游戏信息为空！ 领取礼包时，礼包信息里面必须包含完整的游戏信息....");
        }
        mUserInfo = UserInfoManager.instance().getUserInfo();
    }

    /**
     * 此方法通过礼包游戏基础信息得到游戏基础数据；
     */
    public static GameBaseDetail giftGame2GameDetail(GameDomainBaseDetail gameInfo, Context
            mContext) {
        GameBaseDetail game = null;
        try {
            game = DownFileService.getInstance(mContext).getDownFileById(Integer
                    .valueOf(gameInfo.getUniqueIdentifier()));
            if (game == null) {
                game = new GameBaseDetail();
                game.setGameBaseInfo(gameInfo);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return game;
    }

    public GetGiftResponseListener getGetGiftResponeseListener() {
        return mGetGiftResponeseListener;
    }

    public void setGetGiftResponeseListener(GetGiftResponseListener mListener) {
        this.mGetGiftResponeseListener = mListener;
    }

    public void getGift(String pageName) {
        this.pageName = pageName;
        if (mUserInfo == null) {
            UserInfoManager.instance().isLoginHaveCall(mContext, UNLOGIN, true);
            if (mGetGiftResponeseListener != null) {
                mGetGiftResponeseListener.onFailure(mGift);
            }
        } else {
            // 需要先发送请求领取礼包再弹出dialog；
//            EventBus.getDefault().register(this);
            requestData();
        }
    }

    /**
     * 请求网络
     */
    private void requestData() {
        Map<String, String> params = new HashMap<>();
        params.put("id", mGift.getUniqueIdentifier());
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.getObtainGiftUri(mGift.getUniqueIdentifier()), params, new WebCallBackToObject<UIModuleList>() {

            /**
             * 网络请求出错时调用
             *
             * @param statusCode 异常编号
             * @param error      异常信息
             */
            @Override
            public void onFailure(int statusCode, Throwable error) {
                if (statusCode == 201) {
                    UserInfoManager.instance().isLoginHaveCall(mContext, UNLOGIN, true);
                } else {
                    ToastUtils.showToast(mContext, "礼包领取失败!");
                }
                mSate = State.ReceviedFail;
                notifyFail();
            }

            @Override
            protected void handle(UIModuleList list) {
                UIModule module = (UIModule) list.get(0);
                GiftDomainDetail info = (GiftDomainDetail) module.getData();
                if (info != null) {
                    mSate = State.ReceviedSucess;
                    mGift.setUsage(info.getUsage());
                    mGift.setCode(info.getCode());
                    mGift.setContent(info.getContent());
                    showDialog();
                    notifySuccess(info);
                } else {
                    mSate = State.ReceviedFail;
                    ToastUtils.showToast(mContext, "已经没有礼包可领了!");
                    mGift.setRemain(0);
                    notifyFail();
                }
            }
        });
    }


    private void notifySuccess(GiftDomainDetail info) {
        EventBus.getDefault().post(mGift);
        if (mGetGiftResponeseListener != null) {
            mGetGiftResponeseListener.onSuccess();
        }
    }

    private void notifyFail() {
        if (mGetGiftResponeseListener != null) {
            mGetGiftResponeseListener.onFailure(mGift);
        }
    }

    private void showDialog() {
        ToastDialog dialog = null;
        if (!AppIsInstalledUtil.isInstalled(mContext, mGame.getPkgName())) {
            mSate = State.UnInstall;
            if (mGame.getState() == InstallState.install) {
                dialog = new ToastDialog(mContext, StateEnum.GiftWaitInstall,
                        mGift);
            } else {
                dialog = new ToastDialog(mContext,
                        StateEnum.NoInstallForPackage, mGift);
            }
            dialog.setConFirm(new ConFirmCallBack());

        } else {
            mSate = State.ReceviedSucess;
            dialog = new ToastDialog(mContext, StateEnum.SuccessForPackage,
                    mGift);
            dialog.setConFirm(new ConFirmCallBack());
        }
        dialog.show();
    }

    @Override
    public void updateUserInfo(UserBaseInfo userBaseInfo) {

    }

    @Override
    public void userLogout() {

    }

    @Override
    public void userLogin(UserBaseInfo userBaseInfo) {

    }

    public enum State {
        UnInstall, ReceviedSucess, ReceviedFail
    }

    public interface GetGiftResponseListener {

        void onSuccess();

        void onFailure(GiftDomainDetail gift);
    }

    /**
     * dialog确认按钮回调
     *
     * @author Administrator
     */
    class ConFirmCallBack implements ConFirmBTCallBack {

        @Override
        public void ConFirmListener() {
            switch (mSate) {
                case UnInstall:
                    if (mGame != null) {
                        if (mGame.getState() == InstallState.install) {
                            ApkInstallManger.self().installPkg(mGame, Constant.MODE_SINGLE, null, false);
                        } else {
                            Utils.gameDown(mContext, mGame, pageName, true, Constant.MODE_SINGLE, Constant.DOWNLOAD_TYPE_NORMAL, null);
                        }
                    }
                    break;
                case ReceviedSucess:
                    if (mGift != null) {
                        ClipBoardManagerUtil.self().save2ClipBoard(mGift.getCode());
                        OpenAppUtil.openApp(mGame, mContext,pageName);
                    }
                    break;

                case ReceviedFail:
                    break;
            }
        }
    }

}
