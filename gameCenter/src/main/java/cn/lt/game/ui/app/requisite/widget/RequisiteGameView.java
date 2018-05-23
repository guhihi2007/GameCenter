package cn.lt.game.ui.app.requisite.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.bean.GameInfoBean;
import cn.lt.game.lib.util.IntegratedDataUtil;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.ui.app.requisite.RequisiteDialog.RequisiteItem;

public class RequisiteGameView extends FrameLayout {
    /**
     * 游戏logo小图
     */
    private ImageView mLogoIv;

    private TextView mNameTv;// 游戏名

    private TextView mTagSizeTv;// 游戏标签和大小

    private RequisiteItem mItem;

    private ImageView mCheckView;

    public RequisiteGameView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.requisite_game_item, this);
        init();
    }

    public RequisiteGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RequisiteGameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        mLogoIv = (ImageView) findViewById(R.id.logoIv);
        mNameTv = (TextView) findViewById(R.id.nameTv);
        mTagSizeTv = (TextView) findViewById(R.id.tagSizeTv);
        mCheckView = (ImageView) findViewById(R.id.cb_requisite_game_item);
    }

    public void fillView(RequisiteItem item) {
        this.mItem = item;

        mCheckView.setBackgroundResource(mItem.isChecked() ? R.mipmap.requisite_checked : R.mipmap.requisite_no_check);

        GameInfoBean mGame = mItem.getGameInfoBean();
        if (mGame != null) {
            mNameTv.setText(mGame.getName());
            mTagSizeTv.setText(IntegratedDataUtil.calculateSizeMB(mGame.getPackage_size()));
            mLogoIv.setTag(R.id.index_click_gameId, mGame.getId());
            mLogoIv.setTag(R.id.index_click_packageName, mGame.getPackage_name());
            mNameTv.setTag(R.id.index_click_gameId, mGame.getId());
            mNameTv.setTag(R.id.index_click_packageName, mGame.getPackage_name());
            ImageloaderUtil.loadLTLogo(getContext(),mGame.getIcon_url(),mLogoIv);
        }
    }

    public void switchCheckView() {
        if (!mItem.isEnable()) {
            return;
        }

        if (mItem.isChecked()) {
            mCheckView.setBackgroundResource(R.mipmap.requisite_no_check);
            mItem.setChecked(false);
        } else {
            mCheckView.setBackgroundResource(R.mipmap.requisite_checked);
            mItem.setChecked(true);
        }
    }

}
