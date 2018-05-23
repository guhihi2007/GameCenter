package cn.lt.game.ui.app.community.topic.my;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.ui.app.community.DraftsActivity;
import cn.lt.game.ui.app.community.MyPrivateMessageActivity;
import cn.lt.game.ui.app.community.personalpage.ComNotificationActivity;
import cn.lt.game.ui.app.community.personalpage.MyConcernActivity;
import cn.lt.game.ui.app.community.personalpage.PersonalPageHeadWidget;
import cn.lt.game.ui.app.community.topic.my.MyComFragment.GroupSubject;
import cn.lt.game.ui.app.community.topic.my.MyComFragment.GroupSubject.Type;
import cn.lt.game.ui.app.community.widget.UserGradeProgressBar;

/***
 * 我的社区适配器
 */
public class ComMineAdapter extends BaseAdapter {

    private List<ItemData> mList;

    private Context mContext;

    private LayoutInflater mInflater;

    private ClickListener mClickListener = new ClickListener();

    public ComMineAdapter(Context context, List<ItemData> list) {

        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        setmList(list);
    }

    public void resetList() {
        if (mList != null) {
            mList.clear();
            this.notifyDataSetChanged();
        }
    }

    public List<? extends Object> getmList() {
        return mList;
    }

    public void setmList(List<ItemData> list) {
        if (list == null) {
            mList = new ArrayList<ItemData>();
        } else {
            this.mList.clear();
            this.mList.addAll(list);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ItemData.ObjectType type = mList.get(position).getmObjType();
        UserInfoHolder userInfoHolder = null;
        ComInfoHodler comInfoHodler = null;
        ItemInfoHodler itemInfoHodler = null;
        if (convertView == null) {
            switch (type) {
                case UserInfo:
                    userInfoHolder = new UserInfoHolder();
                    convertView = new PersonalPageHeadWidget(mContext);
                    userInfoHolder.root = (RelativeLayout) convertView.findViewById(R.id.rl_root);
                    userInfoHolder.mUserName = (TextView) convertView.findViewById(R.id.tv_user_nickname);
                    userInfoHolder.mUserSign = (TextView) convertView.findViewById(R.id.tv_user_sign);
                    userInfoHolder.mUserLevel = (ImageView) convertView.findViewById(R.id.tv_user_level);
                    userInfoHolder.mGold = (TextView) convertView.findViewById(R.id.tv_user_gold);
                    userInfoHolder.mUserHeadView = (ImageView) convertView.findViewById(R.id.iv_user_head);
                    userInfoHolder.mProgressBar = (UserGradeProgressBar) convertView.findViewById(R.id.pb_user_grade);
                    convertView.setTag(userInfoHolder);
                    break;
                case ComInfo:
                    comInfoHodler = new ComInfoHodler();
                    convertView = mInflater.inflate(R.layout.personal_tab_view, null);
                    comInfoHodler.mTopic = (TextView) convertView.findViewById(R.id.tv_topic);
                    comInfoHodler.mComment = (TextView) convertView.findViewById(R.id.tv_comment);
                    comInfoHodler.mGroup = (TextView) convertView.findViewById(R.id.tv_group);
                    convertView.setTag(comInfoHodler);
                    break;
                case ItemInfo:
                    itemInfoHodler = new ItemInfoHodler();
                    convertView = mInflater.inflate(R.layout.layout_item_com_mine_subject, null);
                    itemInfoHodler.mTitle = (TextView) convertView.findViewById(R.id.tv_title);
                    itemInfoHodler.tv_redPoint = (TextView) convertView.findViewById(R.id.tv_redPoint);
                    convertView.setTag(itemInfoHodler);
                    break;
                case NoInfo:
                    break;
            }
        } else {
            switch (type) {
                case UserInfo:
                    userInfoHolder = (UserInfoHolder) convertView.getTag();
                    break;
                case ComInfo:
                    comInfoHodler = (ComInfoHodler) convertView.getTag();
                    break;
                case ItemInfo:
                    itemInfoHodler = (ItemInfoHodler) convertView.getTag();
                    break;
            }
        }

        switch (type) {
            case UserInfo:
                fillUserInfo(userInfoHolder, position);
                break;
            case ComInfo:
                fillComInfo(comInfoHodler, position);
                convertView.setOnClickListener(mClickListener);
                break;
            case ItemInfo:
                convertView.setOnClickListener(mClickListener);
                convertView.setTag(R.id.list_item_click, mList.get(position));
                fillItemInfo(itemInfoHodler, position);
        }
        return convertView;
    }

    /**
     * 填充用户社区发表数量信息
     */
    private void fillComInfo(ComInfoHodler comInfoHodler, int position) {
        try {
            MyComFragment.ComInfo comInfo = (MyComFragment.ComInfo) mList.get(position);
            comInfoHodler.mTopic.setText("话题(" + comInfo.getTopic_count() + ")");
            comInfoHodler.mTopic.setOnClickListener(mClickListener);
            comInfoHodler.mComment.setText("评论(" + comInfo.getComment_count() + ")");
            comInfoHodler.mComment.setOnClickListener(mClickListener);
            comInfoHodler.mGroup.setText("小组(" + comInfo.getGroup_count() + ")");
            comInfoHodler.mGroup.setOnClickListener(mClickListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 填充用户信息
     */
    private void fillUserInfo(final UserInfoHolder userInfoHolder, int position) {
        try {
            MyComFragment.UserInfo userinfo = (MyComFragment.UserInfo) mList.get(position);
            userInfoHolder.mUserName.setText(userinfo.getUser_nickname());
            userInfoHolder.mGold.setText(userinfo.getUser_gold() + "");
//            ImageLoader.getInstance().display(userinfo.getUser_icon(), userInfoHolder.mUserHeadView);
            ImageloaderUtil.loadUserHead(mContext,userinfo.getUser_icon(), userInfoHolder.mUserHeadView);
            userInfoHolder.mUserLevel.setImageLevel(userinfo.getUser_level());
            userInfoHolder.mUserSign.setText(TextUtils.isEmpty(userinfo.getUser_summary()) ? "这个人很懒，什么也没留下" : userinfo.getUser_summary());
            userInfoHolder.mProgressBar.setVisibility(View.VISIBLE);
            userInfoHolder.mProgressBar.setProgress(userinfo.getUser_upgrade_percent());

            ImageloaderUtil.loadImageCallBack(mContext, userinfo.getBackground_img(), new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    userInfoHolder.root.setBackground(resource);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 填充ITEM数据
     */
    private void fillItemInfo(ItemInfoHodler infoHodler, int position) {
        try {
            GroupSubject item = (GroupSubject) mList.get(position);
            infoHodler.mTitle.setText(item.getmName());
            switch (item.getmType()) {
                case MY_LETTER:
                    infoHodler.tv_redPoint.setVisibility(item.getHasNewMsg() == 0 ? View.INVISIBLE : View.VISIBLE);
                    break;
                case MY_NOTICE:
                    infoHodler.tv_redPoint.setVisibility(item.getHasNewNotice() == 0 ? View.INVISIBLE : View.VISIBLE);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        ItemData.ObjectType type = mList.get(position).getmObjType();
        return type.type;
    }

    @Override
    public int getViewTypeCount() {
        return ItemData.ObjectType.values().length;
    }
    /**头部用户信息容器*/
    class UserInfoHolder {
        private RelativeLayout root;
        private ImageView mUserHeadView;
        private TextView mUserName;
        private ImageView mUserLevel;
        private TextView mUserSign;
        private TextView mGold;
        private UserGradeProgressBar mProgressBar;
    }
    /**社区发表数量容器*/
    class ComInfoHodler {
        private TextView mTopic;
        private TextView mComment;
        private TextView mGroup;
    }
    /**TIEM容器*/
    class ItemInfoHodler {
        public TextView mTitle;
        public TextView tv_redPoint;
    }

    class ClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = null;
            GroupSubject subject = (GroupSubject) v.getTag(R.id.list_item_click);
            if (subject != null) {
                Type mType = subject.getmType();
                switch (mType) {
                    //我的私信
                    case MY_LETTER:
                        intent = new Intent(mContext, MyPrivateMessageActivity.class);
                        break;
                    //社区通知
                    case MY_NOTICE:
                        intent = new Intent(mContext, ComNotificationActivity.class);
                        break;
                    //我的关注
                    case MY_CONCERN:
                        intent = new Intent(mContext, MyConcernActivity.class);
                        intent.putExtra("userType", MyConcernActivity.MYATTENTION);
                        break;
                    //我的粉丝
                    case MY_FAN:
                        intent = new Intent(mContext, MyConcernActivity.class);
                        intent.putExtra("userType", MyConcernActivity.MYFANS);
                        break;
                    //我的收藏
                    case MY_COLLECT:
                        intent = new Intent(mContext, CommentTopicActivity.class);
                        intent.putExtra("type", CommentTopicActivity.FAVORITE_TOPIC);
                        break;
                    //我的草稿
                    case MY_DRADT:
                        intent = new Intent(mContext, DraftsActivity.class);
                        break;
                    default:
                        break;
                }
            } else {
                switch (v.getId()) {
                    case R.id.tv_topic://我的话题
                        intent = new Intent(mContext, CommentTopicActivity.class);
                        intent.putExtra("type", CommentTopicActivity.PUBLISH_TOPIC);
                        break;
                    case R.id.tv_comment://我的评论
                        intent = new Intent(mContext, CommentTopicActivity.class);
                        intent.putExtra("type", CommentTopicActivity.COMMENT_TOPIC);
                        break;
                    case R.id.tv_group://我的小组
                        intent = new Intent(mContext, CommentTopicActivity.class);
                        intent.putExtra("type", CommentTopicActivity.MY_JOINED_GROUP);
                        break;
                }
            }
            mContext.startActivity(intent);


        }
    }

    public static class ItemData {

        private ObjectType mObjType;

        public ObjectType getmObjType() {
            return mObjType;
        }

        public void setmObjType(ObjectType mObjType) {
            this.mObjType = mObjType;
        }

        public enum ObjectType {
            UserInfo(0), ComInfo(1), ItemInfo(2), NoInfo(3);
            public int type;

            ObjectType(int type) {
                this.type = type;
            }
        }
    }
}
