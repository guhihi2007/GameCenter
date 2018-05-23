package cn.lt.game.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.IntegratedDataUtil;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.model.GameBaseDetail;

/**
 * Created by erosion on 2016/11/21.
 */

public class StrategyListInfoView extends RelativeLayout {
    private ImageView logoIv;
    private TextView nameTv, type, size, downloadCtn;
    private GameBaseDetail game;
    public  int            id;

    public StrategyListInfoView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        LayoutInflater.from(context).inflate(R.layout.strategy_list_info_view, this);
        findView();
    }

    public StrategyListInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.strategy_list_info_view, this);
        findView();

    }

    private void initView() {
        nameTv.setText(game.getName());
        int downloadCount = 0;
        try {
            downloadCount = Integer.parseInt(game.getDownloadCnt());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        downloadCtn.setText(IntegratedDataUtil.calculateCountsV4(downloadCount));
        size.setText(game.getPkgSizeInM());
        type.setText(game.getCategory());
        loadLogo();
    }

    private void findView() {
        logoIv = (ImageView) findViewById(R.id.appMsg_logo_Iv);
        downloadCtn = (TextView) findViewById(R.id.tv_download_count);
        nameTv = (TextView) findViewById(R.id.appMsg_gameName);
        type = (TextView) findViewById(R.id.tv_game_category);
        size = (TextView) findViewById(R.id.tv_game_size);

    }

    public GameBaseDetail getGame() {
        return game;
    }

    public void setGame(GameBaseDetail game) {
        this.game = game;
        initView();
    }

    private void loadLogo() {
        ImageloaderUtil.loadRoundImage(getContext(),game.getLogoUrl(), logoIv);
    }
}
