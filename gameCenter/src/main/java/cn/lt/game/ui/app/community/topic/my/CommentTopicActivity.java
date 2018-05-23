package cn.lt.game.ui.app.community.topic.my;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragmentActivity;
import cn.lt.game.lib.view.TitleBarView;
import cn.lt.game.lib.view.TitleMoreButton.MoreButtonType;
import cn.lt.game.ui.app.community.personalpage.TaGroupFragment;
import cn.lt.game.ui.app.community.topic.TopicListFragment;

/***
 * 我的社区-发表的评论、他的主页-他的评论
 */
@SuppressLint("CommitTransaction")
public class CommentTopicActivity extends BaseFragmentActivity {

    FragmentTransaction mFragmentTransaction;
    private TitleBarView mTitleBar;

    /**
     * 我的话题
     */
    public static final int PUBLISH_TOPIC = 1;

    /**
     * 我的评论
     */
    public static final int COMMENT_TOPIC = 2;

    /**
     * 我的收藏
     */
    public static final int FAVORITE_TOPIC = 3;

    /**
     * 加入的小组
     */
    public static final int MY_JOINED_GROUP = 4;

    public Fragment mPublishFragment;

    public Fragment mFavoriteFragment;

    public Fragment mCommentFragment;

    public Fragment mGroupFragment;

    private int mType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.topic_page_layout);
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        getIntentData();
        mTitleBar = (TitleBarView) findViewById(R.id.title_bar);
        mTitleBar.setMoreButtonType(MoreButtonType.BackHome);
        mTitleBar.setBackHomeVisibility(View.VISIBLE);
        init();
    }

    private void init() {
        if (mFavoriteFragment != null) {
            mFragmentTransaction.detach(mFavoriteFragment);
        }

        if (mCommentFragment != null) {
            mFragmentTransaction.detach(mCommentFragment);
        }

        if (mPublishFragment != null) {
            mFragmentTransaction.detach(mPublishFragment);
        }

        if (mGroupFragment != null) {
            mFragmentTransaction.detach(mGroupFragment);
        }

        switch (mType) {
            case PUBLISH_TOPIC:
                mTitleBar.setTitle("我的话题");
                if (mPublishFragment == null) {
                    mPublishFragment = TopicListFragment.newInstance(R.string.published_topic);
                }

                mFragmentTransaction.add(R.id.root, mPublishFragment);
                break;

            case COMMENT_TOPIC:
                mTitleBar.setTitle("我的评论");
                if (mCommentFragment == null) {
                    mCommentFragment = CommentMineFragment.newInstance(R.string.published_comment);
                }
                mFragmentTransaction.add(R.id.root, mCommentFragment);
                break;

            case FAVORITE_TOPIC:
                mTitleBar.setTitle("我的收藏");
                if (mFavoriteFragment == null) {
                    mFavoriteFragment = TopicListFragment.newInstance(R.string.collected_topic);
                }

                mFragmentTransaction.add(R.id.root, mFavoriteFragment);
                break;
            case MY_JOINED_GROUP:
                mTitleBar.setTitle("我的小组");
                if (mGroupFragment == null) {
                    mGroupFragment = TaGroupFragment.newInstance(R.string.joined_groups);
                }
                mFragmentTransaction.add(R.id.root, mGroupFragment);
                break;
        }
        mFragmentTransaction.commit();
    }

    private void getIntentData() {
        mType = getIntent().getIntExtra("type", -1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void setNodeName() {
        // TODO Auto-generated method stub
        setmNodeName("");
    }
}
