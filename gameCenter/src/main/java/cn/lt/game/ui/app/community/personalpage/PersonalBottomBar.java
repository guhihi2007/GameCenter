package cn.lt.game.ui.app.community.personalpage;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.net.NetIniCallBack;
import cn.lt.game.ui.app.community.CheckUserRightsTool;
import cn.lt.game.ui.app.community.MyPrivateMessageDetailActivity;
import cn.lt.game.ui.app.personalcenter.BindPhoneActivity;
import cn.lt.game.ui.app.personalcenter.UserInfoLoginCallback;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;


/**
 * Created by tiantian on 2015/11/13.
 * 个人主页加关注+发私信工具栏控件
 */
public class PersonalBottomBar extends LinearLayout implements View.OnClickListener {
    private LinearLayout ll_myAttention, ll_msendMsg;
    private TextView tv_myAttenton;
    private ImageView iv_attention;
    private int userId;
    private String nickName;
    private String userIcon;

    public PersonalBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PersonalBottomBar(Context context) {
        super(context);
        int height = (int) context.getResources().getDimension(R.dimen.topicdetail_bottomHeight);
        setMinimumHeight(height);
        initView(context);
    }

    public void setUserId(int userId, String nickName,String userIcon) {
        this.userId = userId;
        this.nickName = nickName;
        this.userIcon = userIcon;
        //发送私信需要依赖着2个参数
        ll_msendMsg.setOnClickListener(this);
    }

    public PersonalBottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.personal_page_bottom_bar, this);
        ll_myAttention = (LinearLayout) findViewById(R.id.ll_attention);
        ll_msendMsg = (LinearLayout) findViewById(R.id.ll_sendMsg);
        tv_myAttenton = (TextView) findViewById(R.id.tv_my_attention);
        iv_attention = (ImageView) findViewById(R.id.iv_attention);
        iv_attention.setBackgroundResource(R.drawable.ic_unattention_selector);
        ll_myAttention.setOnClickListener(this);

    }

    /**
     * 初始化关注状态：与此⽤户的关系，0-未关注，1-已 关注，2-互相关注
     */
    public void initAttentionState(int state) {
        switch (state) {
            case 0:
                tv_myAttenton.setText("加关注");
                iv_attention.setBackgroundResource(R.drawable.ic_unattention_selector);
                break;
            case 1:
                tv_myAttenton.setText("已关注");
                iv_attention.setBackgroundResource(R.drawable.ic_attentioned_selector);
                break;
            case 2:
                tv_myAttenton.setText("已关注");
                iv_attention.setBackgroundResource(R.drawable.ic_attentioned_eachother_selector);
                break;
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.ll_attention:
                final String state = tv_myAttenton.getText().toString();
                boolean  hasLogin = CheckUserRightsTool.instance().haveLoginAndNextBehavior(v.getContext(), new UserInfoLoginCallback() {
                    @Override
                    public void userLogin(UserBaseInfo userBaseInfo) {
                        attentionDeal(v.getContext(),state);
                    }
                });
                if (hasLogin){
                    attentionDeal(v.getContext(),state);
                }
                break;
            case R.id.ll_sendMsg://发私信
                CheckUserRightsTool.instance().hasBindPhone(v.getContext(), new NetIniCallBack() {
                    @Override
                    public void callback(int code) {
                        if (-1 == code) {
                            final MessageDialog messageDialog = new MessageDialog(v.getContext(), "提示", "您需要绑定手机号才能给对方发送私信哦", "取消", "现在绑定");
                            messageDialog.setLeftOnClickListener(new MessageDialog.LeftBtnClickListener() {
                                @Override
                                public void OnClick(View view) {
                                    messageDialog.dismiss();
                                }
                            });
                            messageDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
                                @Override
                                public void OnClick(View view) {
                                    getContext().startActivity(new Intent(view.getContext(), BindPhoneActivity.class));
                                    messageDialog.dismiss();
                                }
                            });
                            messageDialog.setCancelOnClickListener(new MessageDialog.CancelCliclListener() {
                                @Override
                                public void onClicl(View view) {
                                    messageDialog.dismiss();
                                }
                            });
                            messageDialog.show();
                        } else {
                            Intent intent = new Intent(getContext(), MyPrivateMessageDetailActivity.class);
                            intent.putExtra("friend_user_id", userId);
                            intent.putExtra("friend_name", nickName);
                            intent.putExtra("friend_head",userIcon);
                            getContext().startActivity(intent);
                        }
                    }
                });
                break;
        }
    }

    /***
     * 加关注
     * @param content
     * @param state
     */
    private void attentionDeal(Context content,String state){
        if ("加关注".equals(state)) {
            CheckUserRightsTool.instance().addAttention(content, userId, new NetIniCallBack() {
                @Override
                public void callback(int code) {
                    if (0 == code) {
                        initAttentionState(1);
                    }
                }
            });
        } else if ("已关注".equals(state)) {
            final MessageDialog messageDialog = new MessageDialog(content, "提示", "确定不再关注此人？", "取消", "确定");
            messageDialog.setLeftOnClickListener(new MessageDialog.LeftBtnClickListener() {
                @Override
                public void OnClick(View view) {
                    messageDialog.dismiss();
                }
            });
            messageDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
                @Override
                public void OnClick(View view) {
                    CheckUserRightsTool.instance().cancelAttention(view.getContext(), userId, new NetIniCallBack() {
                        @Override
                        public void callback(int code) {
                            if (0 == code) {
                                initAttentionState(0);
                            }
                        }
                    });
                    messageDialog.dismiss();
                }
            });
            messageDialog.setCancelOnClickListener(new MessageDialog.CancelCliclListener() {
                @Override
                public void onClicl(View view) {
                    messageDialog.dismiss();
                }
            });
            messageDialog.show();
        }
    }
}
