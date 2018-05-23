package cn.lt.game.ui.app.community.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.net.NetIniCallBack;
import cn.lt.game.ui.app.community.CheckUserRightsTool;
import cn.lt.game.ui.app.community.group.GroupMemberActivity;
import cn.lt.game.ui.app.community.model.Group;
import cn.lt.game.ui.app.community.topic.group.GroupTopicActivity;

/***
 * 加入小组ITEM
 *
 * @author tiantian
 * @des
 */
public class GroupView extends RelativeLayout {

    private Context mContext;

    public Group getmGroup() {
        return mGroup;
    }

    public void setmGroup(Group mGroup) {
        this.mGroup = mGroup;
    }

    private Group mGroup;

    private LinearLayout mConvertView;

    private ImageView mIconIV;

    private TextView mNameTV;

    private TextView mTopicTV;

    private TextView mTopocDes;

    private TextView mMemberTV;

    private LinearLayout mGroupCount;

    private Button mJoinBtn;

    private MyClickListener mListener;
    private GroupViewType type;

    boolean isUserLogin = false;

    public enum GroupViewType {
        RecommendGroup(), MyHub()
    }

    public GroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public GroupView(Context context, GroupViewType type) {
        super(context);
        this.mContext = context;
        this.type = type;
        init();
    }

    private void initView() {
        mConvertView = (LinearLayout) findViewById(R.id.ll_convertView);
        mIconIV = (ImageView) findViewById(R.id.iv_group);
        mNameTV = (TextView) findViewById(R.id.tv_group_name);
        mTopicTV = (TextView) findViewById(R.id.tv_topic_count);
        mMemberTV = (TextView) findViewById(R.id.tv_number_count);
        mTopocDes = (TextView) findViewById(R.id.tv_group_des);
        mGroupCount = (LinearLayout) findViewById(R.id.ll_group_count);
        mJoinBtn = (Button) findViewById(R.id.btn_join);
        setJoinBtnTextSize();
        if (type == GroupViewType.MyHub) {
            mJoinBtn.setVisibility(View.GONE);
        }
        mConvertView.setOnClickListener(mListener);
        mGroupCount.setOnClickListener(mListener);
        mJoinBtn.setOnClickListener(mListener);

    }

    private void setJoinBtnTextSize() {

        int mScreenWidth = MyApplication.width; // 当前分辨率 宽度
        if (mScreenWidth <= 720) {
            mJoinBtn.setTextSize(12);
        } else {
            mJoinBtn.setTextSize(15);
        }
    }

    public void initButtonState(Group mGroup) {
        isUserLogin = CheckUserRightsTool.instance().isLogin();
        // 已登录状态下，判断用户是否加入该小组；未登录则统一显示加入小组
        if (isUserLogin) {
            if (mGroup.is_join) {
                mJoinBtn.setText("进入小组");
            } else {
                mJoinBtn.setText("加入小组");
            }
        } else {
            mJoinBtn.setText("加入小组");
        }
    }

    private void init() {
        mListener = new MyClickListener();
        LayoutInflater.from(mContext).inflate(R.layout.group_item, this);
        initView();
    }

    public void fillView(Group mGroup) {
        setmGroup(mGroup);
        ImageloaderUtil.loadLTLogo(getContext(),mGroup.group_icon, mIconIV);
        mNameTV.setText(mGroup.group_title);
        mTopicTV.setText(mGroup.popularity + "");
        mMemberTV.setText(mGroup.member_count + "");
        String summary = ToDBC(mGroup.group_summary);
        mTopocDes.setText(summary);
        initButtonState(mGroup);

    }

    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375) c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    class MyClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_join:
                    if ("加入小组".equals(mJoinBtn.getText().toString().trim())) {
                        CheckUserRightsTool.instance().checkIsUserLoginAndGoinGroup(mContext, mGroup.group_id, new NetIniCallBack() {
                            @Override
                            public void callback(int code) {
                                if (code == 0) {
                                    ToastUtils.showToast(mContext, "加入小组成功");
                                    mJoinBtn.setText("进入小组");
                                    mGroup.is_join = true;
                                    ++mGroup.member_count;
                                    fillView(mGroup);
                                } else {
                                    ToastUtils.showToast(mContext, "已加入该小组，不能重复加入！");
                                }
                            }
                        });
                    } else {
                        ActivityActionUtils.activity_Jump_Value(mContext, GroupTopicActivity.class, "group_id", mGroup.group_id);
                    }
                    break;
                case R.id.ll_convertView:
                    // 跳转到小组话题列表页面
                    ActivityActionUtils.activity_Jump_Value(mContext, GroupTopicActivity.class, "group_id", mGroup.group_id);
                    break;
                case R.id.ll_group_count:
                    // 跳转到小组成员列表
                    ActivityActionUtils.activity_Jump_Values(mContext, GroupMemberActivity.class, "GroupMember", mGroup);
                    break;
                default:
                    break;
            }
        }
    }

}
