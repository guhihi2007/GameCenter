package cn.lt.game.ui.app.gamedetail;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cn.lt.game.base.BaseFragment;
import cn.lt.game.domain.detail.GameDomainDetail;
import cn.lt.game.model.GameBaseDetail;

public class GameDetailPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] mTabTile;

    public String[] getmTabTile() {
        return mTabTile;
    }

    public void setmTabTile(String[] mTabTile) {
        this.mTabTile = mTabTile;
    }

    private BaseFragment[] fragments;
    private int groupId;

    private GameBaseDetail game;

    private GameDomainDetail gameDomainDetail;

    public GameDomainDetail getGameDomainDetail() {
        return gameDomainDetail;
    }

    public void setGameDomainDetail(GameDomainDetail gameDomainDetail) {
        this.gameDomainDetail = gameDomainDetail;
    }

    public GameBaseDetail getGame() {
        return game;
    }

    public void setGame(GameBaseDetail game) {
        this.game = game;
    }

    public void setGroupId(int groupId) {this.groupId = groupId;  }

    public GameDetailPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        mTabTile = new String[]{"游戏详情", "评论(0)"};
        fragments = new BaseFragment[]{new GameInfoFragment(), new GameCommentInfoFragment()};
    }

    public void setFragment(boolean hasForum) {
        if (!hasForum) {
            fragments = new BaseFragment[]{fragments[0], fragments[1]};
        }
        fragments = new BaseFragment[]{fragments[0], fragments[1], new GameCommunityInfoFragment()};
        this.notifyDataSetChanged();
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

}
