package cn.lt.game.ui.app.community.personalpage;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cn.lt.game.R;
import cn.lt.game.ui.app.community.CommunityBaseFragment;
import cn.lt.game.ui.app.community.ta.TaTopicFragment;
import cn.lt.game.ui.app.community.topic.my.CommentMineFragment;

/**
 * 社区个人中心适配器
 * Created by tiantian on 2015/11/10.
 */
public class PersonalAdapter extends FragmentPagerAdapter  {
//    private int mIconRes[] = {R.mipmap.ic_my_topic, R.mipmap.ic_my_comment, R.mipmap.ic_my_group};
//
//    @Override
//    public int getIconResId(int position) {
//        return mIconRes[position];
//    }

    private Context mContext;
    private String mTabTile[] = {"话题", "评论", "小组"};
    private CommunityBaseFragment[] fragments;

    public PersonalAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        fragments = new CommunityBaseFragment[]{
                TaTopicFragment.newInstance(R.string.ta_topic),
                CommentMineFragment.newInstance(R.string.ta_comment),
                TaGroupFragment.newInstance(R.string.ta_group)
        };
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragments[arg0];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTile[position];
    }

    @Override
    public int getCount() {
        return mTabTile.length;
    }

    public void setmTabTile(String[] mTabTile) {
        this.mTabTile = mTabTile;
    }
}
