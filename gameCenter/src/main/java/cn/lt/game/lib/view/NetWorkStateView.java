package cn.lt.game.lib.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.net.NetIniCallBack;
import cn.lt.game.ui.app.community.CheckUserRightsTool;
import cn.lt.game.ui.app.community.group.GroupMemberActivity;
import cn.lt.game.ui.app.gamestrategy.GameStrategyHomeActivity;
import cn.lt.game.ui.app.sidebar.feedback.FeedBackActivity;
import de.greenrobot.event.EventBus;

public class NetWorkStateView extends FrameLayout implements OnClickListener {
    public static final int goInformation = 1;
    public static final int gotoIndexActity = 0;
    public static final int JoinGroup = 2;
    public static final int publishComment = 3;
    public static final int gotoFeedback = 4;
    public static final int setPermission = 5;
    public boolean clickPersssion = false;
    private int notDataState = 0;
    private FrameLayout loadingBar;
    private TextView setting;
    private TextView tryAgain;
    private RelativeLayout netWorkLayout;
    private RelativeLayout notDataLayout;
    private TextView title;
    private TextView goDownLoad;
    private Context context;
    private RetryCallBack retryCallBack;
    private Boolean isfinish = true;
    private JumpIndexCallBack jumpIndexCallBack;
    private int groupId;
    private Button btn;
    private ImageView noData_cat;
    private ImageView iv_failCat;

    public enum CatStyle {
        SINISTER_SMILE, SMILE, NO_DATA
    }

    private static final int NO_DATA = 0;//没数据的猫
    private static final int SMILE_CAT = 1;//微笑的猫
    private static final int SINISTER_CAT = 2;//奸笑的猫
    private int CAT_STYPE = NO_DATA;

    public NetWorkStateView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.network_state, this);
        initView();
    }

    public NetWorkStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.network_state, this);
        initView();
    }

    public void setGroupId(Context context, int groupId, Button publishBtn) {
        this.groupId = groupId;
        this.btn = publishBtn;
    }

    private void initView() {
        loadingBar = (FrameLayout) findViewById(R.id.network_progress_bar);
        netWorkLayout = (RelativeLayout) findViewById(R.id.network_not_network);
        notDataLayout = (RelativeLayout) findViewById(R.id.network_notdata);
        noData_cat = (ImageView) findViewById(R.id.iv_noData);
        iv_failCat = (ImageView) findViewById(R.id.iv_failCat);
        setting = (TextView) findViewById(R.id.network_fail_set);
        tryAgain = (TextView) findViewById(R.id.network_fail_tryAgain);
        title = (TextView) findViewById(R.id.network_title);
        goDownLoad = (TextView) findViewById(R.id.network_goDownLoading);

        setting.setOnClickListener(this);
        tryAgain.setOnClickListener(this);
        goDownLoad.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.network_fail_set:
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                context.startActivity(intent);
                break;
            case R.id.network_fail_tryAgain:
                showLoadingBar();
                if (!NetUtils.isConnected(context)) {
                    ToastUtils.showToast(context, "网络连接失败");
                    showNetworkFailLayout();
                    return;
                }
                if (retryCallBack != null) {
                    retryCallBack.retry();
                }
                break;
            case R.id.network_goDownLoading:
                jump();
                break;
            default:
                break;
        }
    }

    public void showLoadingBar() {
        showView();
        loadingBar.setVisibility(View.VISIBLE);
        hideNetworkFailLayout();
    }

    public void hideLoadingBar() {
        loadingBar.setVisibility(View.GONE);
    }

    public void showNetworkFailLayout() {
        showView();
        hideLoadingBar();
        netWorkLayout.setVisibility(View.VISIBLE);
        hideNetworkNoDataLayout();
    }

    public void hideNetworkFailLayout() {
        netWorkLayout.setVisibility(View.GONE);
    }

    public void showNetworkNoDataLayout() {
        showView();
        hideLoadingBar();
        notDataLayout.setVisibility(View.VISIBLE);
        hideNetworkFailLayout();
    }

    public void showNetworkNoDataLayoutSmile() {
        showView();
        hideLoadingBar();
        notDataLayout.setVisibility(View.VISIBLE);
        hideNetworkFailLayout();
    }

    public void hideNetworkNoDataLayout() {
        notDataLayout.setVisibility(View.GONE);
    }

    public void hideNetworkView() {
        this.setVisibility(View.GONE);
    }

    public void showView() {
        this.setVisibility(View.VISIBLE);
    }


    public void setNoDataLayoutText(String title, String buttonText) {
        this.title.setText(title);
        if (!TextUtils.isEmpty(buttonText)) {
            goDownLoad.setText(buttonText);
        } else {
            goDownLoad.setVisibility(View.GONE);
        }

    }

    // 收藏页面用的
    public void setNotDataState(int notDataState) {
        this.notDataState = notDataState;
    }

    public RetryCallBack getRetryCallBack() {
        return retryCallBack;
    }

    public void setRetryCallBack(RetryCallBack retryCallBack) {
        this.retryCallBack = retryCallBack;
    }

    public interface RetryCallBack {
        void retry();
    }

    public interface JumpIndexCallBack {
        void jump();
    }

    public Boolean getIsfinish() {
        return isfinish;
    }

    public void setIsfinish(Boolean isfinish) {
        this.isfinish = isfinish;
    }

    public JumpIndexCallBack getJumpIndexCallBack() {
        return jumpIndexCallBack;
    }

    public void setJumpIndexCallBack(JumpIndexCallBack jumpIndexCallBack) {
        this.jumpIndexCallBack = jumpIndexCallBack;
    }

    private void jump() {
        switch (notDataState) {
            case gotoIndexActity:

                if (isfinish) {
                    ((Activity) context).finish();
                }

                if (jumpIndexCallBack != null) {
                    jumpIndexCallBack.jump();
                }

                break;
            case goInformation:
                ActivityActionUtils.activity_jump(context, GameStrategyHomeActivity.class);
                break;
            case JoinGroup:
                CheckUserRightsTool.instance().checkIsUserLoginAndGoinGroup(context, groupId, new NetIniCallBack() {
                    @Override
                    public void callback(int code) {
                        if (code == 0) {
                            ToastUtils.showToast(context, "加入小组成功！");
                            btn.setText("发表话题");
                            EventBus.getDefault().post(new GroupMemberActivity.EventBean("refreshData"));
                        } else {
                            ToastUtils.showToast(context, "加入失败，请重试！");
                        }
                    }
                });
                break;
            case gotoFeedback:
                ActivityActionUtils.activity_jump(context, FeedBackActivity.class);
                break;
            case setPermission:
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                clickPersssion = true;
                context.startActivity(intent);
                break;
            default:
                break;
        }
    }


    public void setNoDataCatSyle(CatStyle style) {
        switch (style) {
            case SMILE:
                noData_cat.setImageResource(R.mipmap.smile_cat);
                break;
            case SINISTER_SMILE:
                noData_cat.setImageResource(R.mipmap.sinister_smile_cat);
                break;
            case NO_DATA:
                noData_cat.setImageResource(R.mipmap.empty_data_img);
                break;
            default:
                noData_cat.setImageResource(R.mipmap.empty_data_img);
                break;
        }
    }

    public void setfailNotNetworkCatStyle(CatStyle style) {
        switch (style) {
            case SMILE:
                iv_failCat.setImageResource(R.mipmap.smile_cat);
                break;
            case SINISTER_SMILE:
                iv_failCat.setImageResource(R.mipmap.sinister_smile_cat);
                break;
            case NO_DATA:
                iv_failCat.setImageResource(R.mipmap.network_error);
                break;
            default:
                iv_failCat.setImageResource(R.mipmap.network_error);
                break;
        }
    }


}
