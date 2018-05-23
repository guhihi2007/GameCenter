package cn.lt.game.lib.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.FromPageManager;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.ui.app.HomeActivity;
import cn.lt.game.ui.app.awardgame.view.ScrollVerticalView;
import cn.lt.game.ui.app.community.model.Group;
import cn.lt.game.ui.app.community.model.ShareBean;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.widget.ShareDialog;
import cn.lt.game.ui.app.search.SearchActivity;

public class TitleBarView extends RelativeLayout implements OnClickListener {
    private ImageView backBT;
    private TextView tx;
    private TitleMoreButton moreBT;
    private ImageButton searchIB;
    private ScrollVerticalView scrollVerticalView;

    public TitleBarView(Context context) {
        super(context);
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.not_serach_action_bar, this);
        int height = (int) context.getResources().getDimension(R.dimen.not_serache_actionbar_height);
        setMinimumHeight(height);
        setBackgroundResource(R.color.theme_green);
        initView();
    }

    private void initView() {
        tx = (TextView) findViewById(R.id.not_serach_tv_title);
        backBT = (ImageView) findViewById(R.id.not_serach_btn_back);
        moreBT = (TitleMoreButton) findViewById(R.id.not_serach_btn_moreButton);
        searchIB = (ImageButton) findViewById(R.id.game_detail_search);
        scrollVerticalView = (ScrollVerticalView) findViewById(R.id.download_action);

        backBT.setOnClickListener(this);
        searchIB.setOnClickListener(this);
        scrollVerticalView.setOnClickListener(this);
    }


    /**
     * 下载图标动画
     */
    public void startDownloadAnimation() {
        scrollVerticalView.setDownloadNum(FileDownloaders.getDownloadTaskCount());
        scrollVerticalView.startAnimation();
    }

    public void setScrollVerticalViewVisible(int visible){
        scrollVerticalView.setVisibility(visible);
    }


    public void setTitle(String title) {
        tx.setText(title);
    }

    public void setMoreButtonType(TitleMoreButton.MoreButtonType type) {
        moreBT.setType(type);
        setSrcDrawble(type);

    }

    private void setSrcDrawble(TitleMoreButton.MoreButtonType type) {
        switch (type) {
            case GameDetail:
                moreBT.setImageDrawable(getResources().getDrawable(R.mipmap.ic_share));
                scrollVerticalView.setVisibility(View.GONE);
                break;
            case TopicDetail:
            case GroupTopic:
                moreBT.setImageDrawable(getResources().getDrawable(R.mipmap.ic_more));
                scrollVerticalView.setVisibility(View.GONE);
                break;
            case TopicGroup:
                break;
            case BackHome:
                moreBT.setImageDrawable(getResources().getDrawable(R.mipmap.icon_backhome));
                scrollVerticalView.setVisibility(View.GONE);
                break;
            case Special:
                moreBT.setImageDrawable(getResources().getDrawable(R.mipmap.btn_game_detail_search));
                scrollVerticalView.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        moreBT.setVisibility(VISIBLE);
    }

    public void setTitle(int titleResId) {
        tx.setText(titleResId);
    }

    public void setBackHomeVisibility(int visibility) {
        moreBT.setVisibility(visibility);
    }

    public void setShareBean(ShareBean sb, ShareDialog.ShareDialogType type) {
        moreBT.setShareBean(sb, type);
    }

    public void setSortCallBack(ShareDialog.ItopdetailSortCallback callBack) {
        moreBT.setSortListener(callBack);
    }

    public void setTopicDetail(TopicDetail topicDetail) {
        moreBT.setTopicDetail(topicDetail);
    }

    public void setActivity(Activity act) {
        moreBT.setActivity(act);
    }

    public void setGroupInfo(Group groupInfo) {
        moreBT.setGroupInfo(groupInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.not_serach_btn_back:
                ((Activity) getContext()).finish();
                break;
            case R.id.game_detail_search:
                Intent intent = new Intent(getContext(), SearchActivity.class);
                getContext().startActivity(intent);
                break;
            case R.id.download_action:
                ActivityActionUtils.JumpToManager(getContext(),0);
                break;
            default:
                break;
        }
    }

    public void release() {
        moreBT.release();
    }

    /**
     * 设置游戏已下架，点击分享按钮不显示
     */
    public void hideShareBtn() {
        moreBT.setVisibility(View.GONE);
    }


    /**
     * 设置游戏已下架，点击搜索按钮不显示
     */
    public void hideSearchBtn() {
        searchIB.setVisibility(View.GONE);
    }

    /**
     * 设置游戏已上架，点击分享按钮设置为显示
     */
    public void displayShareBtn() {
        moreBT.setVisibility(View.VISIBLE);
    }

    /**
     * 设置游戏已上架，点击搜索按钮设置为显示
     */
    public void displaySearchBtn() {
        searchIB.setVisibility(View.VISIBLE);
    }

}
