package cn.lt.game.lib.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.lib.util.ClipBoardManagerUtil;

public class ToastDialog extends Dialog implements OnClickListener {
    /**
     * NoInstallForPackage:领取礼包提示框（没安装） NotSignInForPackage：领取礼包提示框（没登录）
     * SuccessForPackage：领取礼包提示框（领取成功） NotSignInForGroup：小组发言提示框（没登录）
     * NotSignInForComment：评论提示框（没登录） NoInstallForComment：评论提示框（没安装）
     * CleanHistory: 清除历史记录,FialedGetGift
     *
     * @author Administrator
     */
    public enum StateEnum {
        NoInstallForPackage, NotSignInForPackage, SuccessForPackage, NotSignInForGroup, NotSignInForComment, NoInstallForComment, CleanHistory, FailedGetGift, GiftWaitInstall
    }

    private TextView titleView, valueView;
    private ImageButton closeView;
    private Button cancelBt, confirmBt;
    private LinearLayout root;
    private StateEnum stateEnum;
    private Context context;
    private String key = " ";
    private ConFirmBTCallBack conFirm;
    private GiftDomainDetail mGift;

    private Button mFailedGetGift;

    public GiftDomainDetail getmGift() {
        return mGift;
    }

    public void setmGift(GiftDomainDetail mGift) {
        this.mGift = mGift;
    }

    public ToastDialog(Context context, StateEnum state) {
        super(context, R.style.updateInfoDialogStyle);
        stateEnum = state;
        this.context = context;
    }

    public ToastDialog(Context context, StateEnum state, GiftDomainDetail data) {
        this(context, state);
        setmGift(data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tostdialoglayout);
        initView();
        setView();
    }

    private void initView() {
        root = (LinearLayout) findViewById(R.id.toastDialog_valuelayout);
        titleView = (TextView) findViewById(R.id.toastDialog_titleText);
        valueView = (TextView) findViewById(R.id.toastDialog_valueText);
        closeView = (ImageButton) findViewById(R.id.toastDialog_close);
        cancelBt = (Button) findViewById(R.id.toastDialog_cancel);
        confirmBt = (Button) findViewById(R.id.toastDialog_confirm);
        mFailedGetGift = (Button) findViewById(R.id.toastDialog_failed_get_gfit);
        mFailedGetGift.setOnClickListener(this);
        cancelBt.setOnClickListener(this);
        confirmBt.setOnClickListener(this);
        closeView.setOnClickListener(this);
    }

    private void setView() {
        switch (stateEnum) {
        /* 没安装，礼包提示框 */
            case NoInstallForPackage:
                titleView.setText("领取成功");
                root.removeAllViews();
                root.addView(new SuccessValue(context));
                confirmBt.setText("下载游戏");
                break;
		/* 待安装，礼包提示框 */
            case GiftWaitInstall:
                titleView.setText("领取成功");
                root.removeAllViews();
                root.addView(new SuccessValue(context));
                confirmBt.setText("安装");
                break;

		/* 没登录，礼包提示框 */
            case NotSignInForPackage:
                titleView.setText("温馨提示");
                valueView.setText("请先登录，登录后礼包可保存在您的个人账号，方便查看使用");
                confirmBt.setText("登录");
                break;
		/* 领取礼包成功，提示框 */
            case SuccessForPackage:
                titleView.setText("领取成功");
                root.removeAllViews();
                root.addView(new SuccessValue(context));
                cancelBt.setText("复制");
                confirmBt.setText("复制并打开");
                break;
		/* 没登录，小组提示框 */
            case NotSignInForGroup:
                titleView.setText("温馨提示");
                valueView.setText("请先登录，登录后您将可以在该小组自由发起对话。");
                confirmBt.setText("登录");
                break;
		/* 没登录，评论提示框 */
            case NotSignInForComment:
                titleView.setText("温馨提示");
                valueView.setText("请先登录，登录后方便您对安装的游戏进行评论");
                confirmBt.setText("登录");
                break;
		/* 没安装，游戏评论提示框 */
            case NoInstallForComment:
                titleView.setText("温馨提示");
                valueView.setText("安装游戏才能进行评论哟～");
                confirmBt.setText("安装");
                break;
		/* 清除历史记录提示框 */
            case CleanHistory:
                titleView.setText("清空");
                valueView.setText("是否清除历史记录？");
                confirmBt.setText("确定");
                break;

            case FailedGetGift:
                titleView.setText("提示");
                valueView.setText((mGift.getTitle() == null ? mGift.getTitle() : mGift.getTitle()) + "领取失败！");
                confirmBt.setVisibility(View.GONE);
                cancelBt.setVisibility(View.GONE);
                mFailedGetGift.setVisibility(View.VISIBLE);
                mFailedGetGift.setTextColor(Color.parseColor("#86bf13"));
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toastDialog_confirm:
                if (conFirm != null) {
                    conFirm.ConFirmListener();
                }
                this.cancel();
                break;
            case R.id.toastDialog_cancel:
                if (StateEnum.SuccessForPackage.equals(stateEnum) && mGift != null) {
                    ClipBoardManagerUtil.self().save2ClipBoard(mGift.getCode());
                }
                this.cancel();
                break;
            case R.id.toastDialog_close:
            case R.id.toastDialog_failed_get_gfit:
                // if (stateEnum == StateEnum.SuccessForPackage && mGift != null) {
                // ClipBoardManagerUtil.self().save2ClipBoard(mGift.getCode());
                // } else if (stateEnum == StateEnum.SuccessForPackage) {
                // }

                this.cancel();
                break;
            default:
                break;
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public interface ConFirmBTCallBack {
        void ConFirmListener();
    }

    public ConFirmBTCallBack getConFirm() {
        return conFirm;
    }

    public void setConFirm(ConFirmBTCallBack conFirm) {
        this.conFirm = conFirm;
    }

    /**
     * @领取礼包成功的View
     */
    public class SuccessValue extends RelativeLayout {
        private String hintValue = stateEnum == StateEnum.NoInstallForPackage ? "礼包内容：" + mGift.getContent() : "使用方法 : " + mGift.getUsage();

        public SuccessValue(Context context) {
            super(context);
            // 添加激活码信息
            TextView title = new TextView(context);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            title.setId(1);
            title.setText("激活码 : " + mGift.getCode());
            title.setTextColor(Color.parseColor("#333333"));
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelOffset(R.dimen.gift_dialog_text_one));
            title.setLayoutParams(params);
            addView(title);
            // 使用规则；
            TextView hintText = new TextView(context);
            RelativeLayout.LayoutParams hintParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            hintParams.addRule(RelativeLayout.BELOW, 1);
            hintParams.topMargin = context.getResources().getDimensionPixelOffset(R.dimen.gift_dialog_tx_margintop);
            hintText.setText(hintValue);
            hintText.setLineSpacing(context.getResources().getDimensionPixelOffset(R.dimen.gift_dialog_tx_space),1);
            hintText.setLayoutParams(hintParams);
            hintText.setTextColor(Color.parseColor("#999999"));
            hintText.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelOffset(R.dimen.gift_dialog_text_two));
            addView(hintText);
        }
    }
}
